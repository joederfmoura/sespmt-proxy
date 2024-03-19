package br.gov.mt.sesp.registrogeral.services;

import br.gov.mt.sesp.registrogeral.dtos.BairroDTO;
import br.gov.mt.sesp.registrogeral.exceptions.RegistroNaoEncontradoException;
import br.gov.mt.sesp.registrogeral.models.Bairro;
import br.gov.mt.sesp.registrogeral.repositories.BairroRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Serviço responsável pelas regras negociais referentes à {@link Bairro}
 */
@Service
public class BairroService {

  private static final Logger LOGGER = LogManager.getLogger(BairroService.class);

  private static final String MENSAGEM_RECEBIDA_BUSCA_BAIRRO = "Recebida mensagem do serviço " +
          "Gerenciamento Ocorrẽncia para consulta por bairro";


  private BairroRepository bairroRepository;

  private ModelMapper modelMapper;

  @Autowired
  public BairroService(BairroRepository bairroRepository, ModelMapper modelMapper) {
    this.bairroRepository = bairroRepository;
    this.modelMapper = modelMapper;
  }

  /**
   * Lista os bairros com base no filtro informado. Caso o filtro não tenha atributos preenchidos, lista todos os bairros.
   *
   * @param idCidade identificador da cidade, para busca por cidade
   * @param nome     nome ou parte do nome do bairro, utilizado para busca por nome
   * @return Page<BairroDTO> lista de bairros com base no filtro informado
   */
  @Cacheable("bairros")
  public Page<BairroDTO> listar(Pageable pageable, Long idCidade, String nome) {
    return bairroRepository.findAll(pageable, idCidade, nome)
        .map(bairro -> modelMapper.map(bairro, BairroDTO.class));
  }

  /**
   * Busca um bairro com base no identificador informado.
   *
   * @param idCidade identificador da cidade, para busca por cidade
   * @param id identificador do bairro
   * @return BairroDTO dados do bairro encontrado, no formato {@link BairroDTO}
   */
  @Cacheable("bairros")
  public BairroDTO buscar(Long idCidade, Long id) {

    LOGGER.debug(String.format("Parâmetros recebidos para busca de bairro: idCidade: %s - idBairro: %s",
            idCidade, id));

    Bairro bairro = bairroRepository
        .findByCidadeAndId(idCidade, id)
        .orElseThrow(() -> new RegistroNaoEncontradoException("bairro.naoEncontrado"));

    BairroDTO bairroDTO = modelMapper.map(bairro, BairroDTO.class);

    LOGGER.debug("Bairro retornado: " + bairroDTO);

    return bairroDTO;
  }

  /**
   * Busca um bairro com base no identificador informado.
   *
   * @param id identificador do bairro
   * @return BairroDTO dados do bairro encontrado, no formato {@link BairroDTO}
   */
  @Cacheable("bairros")
  public BairroDTO buscar(Long id) {
    Bairro bairro = bairroRepository.findById(id)
        .orElseThrow(() -> new RegistroNaoEncontradoException("bairro.naoEncontrado"));
    return modelMapper.map(bairro, BairroDTO.class);
  }


  /**
   * Busca um bairro utilizando o id recebido como parâmetro.
   *
   * @param bairroDTO dados do bairro para filtro
   * @return bairro encontrado com o filtro informado
   */
  @RabbitListener(queues = "q.registrogeral.bairro.busca", concurrency = "2")
  public BairroDTO buscarPorEmail(BairroDTO bairroDTO) {

    LOGGER.info(MENSAGEM_RECEBIDA_BUSCA_BAIRRO);

    Optional<Bairro> bairroEncontrado = bairroRepository.findById(bairroDTO.getId())
            .stream()
            .findFirst();

    return bairroEncontrado.map(bairro -> modelMapper.map(bairro, BairroDTO.class))
            .orElse(null);
  }

}
