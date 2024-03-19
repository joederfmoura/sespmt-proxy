package br.gov.mt.sesp.registrogeral.services;

import br.gov.mt.sesp.registrogeral.controllers.BairroController;
import br.gov.mt.sesp.registrogeral.dtos.PessoaDTO;
import br.gov.mt.sesp.registrogeral.dtos.PessoaSaveDTO;
import br.gov.mt.sesp.registrogeral.exceptions.NegocioException;
import br.gov.mt.sesp.registrogeral.exceptions.RegistroNaoEncontradoException;
import br.gov.mt.sesp.registrogeral.models.Bairro;
import br.gov.mt.sesp.registrogeral.models.Pessoa;
import br.gov.mt.sesp.registrogeral.repositories.BairroRepository;
import br.gov.mt.sesp.registrogeral.repositories.PessoaRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelas regras negociais referentes à {@link Pessoa}
 */
@Service
public class PessoaService {

  private static final Logger LOGGER = LogManager.getLogger(PessoaService.class);
  private static final String MENSAGEM_RECEBIDA_BUSCA_PESSOA = "Recebida mensagem do serviço " +
          "Gerenciamento Ocorrência para consulta por pessoa.";

  private PessoaRepository pessoaRepository;

  private BairroRepository bairroRepository;

  private ModelMapper modelMapper;

  @Autowired
  public PessoaService(PessoaRepository pessoaRepository, 
                       BairroRepository bairroRepository,
                       ModelMapper modelMapper) {
    this.pessoaRepository = pessoaRepository;
    this.bairroRepository = bairroRepository;
    this.modelMapper = modelMapper;
  }

  /** 
   * Lista as pessoas com base no filtro informado. Caso o filtro não tenha atributos preenchidos, lista todas as pessoas.
   *
   * @param cpf CPF da pessoa, utilizado para busca por CPF
   * @param nome nome da pessoa, utilizado para busca por nome
   * @param nomeMae Nome da mãe da pessoa, utilizado para busca por nome da mãe
   * @param email email da pessoa, utilizado para busca por email
   * @return Page<PessoaDTO> lista de pessoas com base no filtro informado
   */
  public Page<PessoaDTO> listar(Pageable pageable, String cpf, String nome, String nomeMae, String email) {
    return pessoaRepository
            .findAll(pageable, cpf, nome, nomeMae, email)
            .map(pessoa -> modelMapper.map(pessoa, PessoaDTO.class));
  }
  
  /** 
   * Salva uma pessoa, criando um novo registro caso o identificador não tenha sido informado e alterando 
   * uma pessoa existente caso o identificador tenha sido informado.
   * 
   * @param pessoaSaveDTO dados da pessoa para salvar, no formato {@link PessoaSaveDTO}
   * @return PessoaDTO dados da pessoa salva, no formato {@link PessoaDTO}
   */
  public PessoaDTO salvar(PessoaSaveDTO pessoaSaveDTO) {
    Pessoa pessoa = desconstruirDTO(pessoaSaveDTO);

    validar(pessoa);
    pessoa = pessoaRepository.save(pessoa);

    return construirDTO(pessoa);
  }
  
  /** 
   * Busca uma pessoa com base no identificador informado.
   * 
   * @param id identificador da pessoa
   * @return PessoaDTO dados da pessoa encontrada, no formato {@link PessoaDTO}
   */
  public PessoaDTO buscar(Long id) {
    Pessoa pessoa = pessoaRepository
      .findById(id)
      .orElseThrow(() -> new RegistroNaoEncontradoException("pessoa.naoEncontrada"));

      return construirDTO(pessoa);
  }

  /** 
   * Exclui uma pessoa com base no identificador informado.
   * 
   * @param id identificador da pessoa que será excluída
   */
  public void excluir(Long id) {
    pessoaRepository.deleteById(id);
  }
  
  /** 
   * Converte os dados de {@link Pessoa} para {@link PessoaDTO}.
   * 
   * @param pessoa dados a serem convertidos no formato {@link Pessoa}
   * @return PessoaDTO dados convertidos no formato {@link PessoaDTO}
   */
  private PessoaDTO construirDTO(Pessoa pessoa) {
    return modelMapper.map(pessoa, PessoaDTO.class);
  }
  
  /** 
   * Converte os dados de {@link PessoaDTO} para {@link Pessoa}.
   * 
   * @param pessoaSaveDTO dados a serem convertidos no formato {@link PessoaSaveDTO}
   * @return Pessoa dados convertidos no formato {@link Pessoa}
   */
  private Pessoa desconstruirDTO(PessoaSaveDTO pessoaSaveDTO) {
    Pessoa pessoa = modelMapper.map(pessoaSaveDTO, Pessoa.class);

    atribuirEndereco(pessoaSaveDTO, pessoa);

    return pessoa;
  }
  
  /** 
   * Atribui os dados referentes ao endereço da pessoa, da origem <b>pessoaSaveDTO</b>, para o destino <b>pessoa</b>.
   * 
   * @param pessoaSaveDTO origem dos dados do endereço no formato {@link PessoaSaveDTO}
   * @param pessoa destino dos dados do endereço no formato {@link Pessoa}
   */
  private void atribuirEndereco(PessoaSaveDTO pessoaSaveDTO, Pessoa pessoa) {

    if (pessoaSaveDTO.getIdBairro() != null) {
      Bairro bairro = bairroRepository
              .findById(pessoaSaveDTO.getIdBairro())
              .orElseThrow(() -> new RegistroNaoEncontradoException("bairro.naoEncontrado"));
      pessoa.setBairro(bairro);
    }

    if (pessoaSaveDTO.getRua() != null && !pessoaSaveDTO.getRua().isBlank()) {
      pessoa.setRua(pessoaSaveDTO.getRua());
    }

    if (pessoaSaveDTO.getNumero() != null && !pessoaSaveDTO.getNumero().isBlank()) {
      pessoa.setNumero(pessoaSaveDTO.getNumero());
    }

    if (pessoaSaveDTO.getComplemento() != null && !pessoaSaveDTO.getComplemento().isBlank()) {
      pessoa.setComplemento(pessoaSaveDTO.getComplemento());
    }
    
    if (pessoaSaveDTO.getCep() != null && !pessoaSaveDTO.getCep().isBlank()) {
      pessoa.setCep(pessoaSaveDTO.getCep());
    }
  }
  
  /** 
   * Valida os dados da pessoa informada.
   * 
   * @param pessoa dados da pessoa para validação no formato {@link Pessoa}
   */
  private void validar(Pessoa pessoa) {
    if (pessoa.getDataNascimento() != null && pessoa.getDataNascimento().isAfter(LocalDate.now())) {
      throw new NegocioException("pessoa.dataNascimentoFutura");
    }
  }

  /**
   * Busca uma pessoa utilizando o e-mail recebido como parâmetro.
   *
   * @param pessoaDTO dados da pessoa para filtro
   * @return pessoa encontrada com o filtro informado
   */
  @RabbitListener(queues = "q.registrogeral.pessoa.busca", concurrency = "2")
  public PessoaDTO buscarPorEmail(PessoaDTO pessoaDTO) {

    LOGGER.info(MENSAGEM_RECEBIDA_BUSCA_PESSOA);

    Optional<Pessoa> pessoaEncontrada = pessoaRepository.findByEmail(pessoaDTO.getEmail())
            .stream()
            .findFirst();

    return pessoaEncontrada.map(pessoa -> modelMapper.map(pessoa, PessoaDTO.class))
            .orElse(null);
  }
}
