package br.gov.mt.sesp.carteirafuncional.services;

import br.gov.mt.sesp.carteirafuncional.dtos.AdministrativoDTO;
import br.gov.mt.sesp.carteirafuncional.dtos.PolicialDTO;
import br.gov.mt.sesp.carteirafuncional.dtos.PolicialSaveDTO;
import br.gov.mt.sesp.carteirafuncional.exceptions.NegocioException;
import br.gov.mt.sesp.carteirafuncional.exceptions.RegistroNaoEncontradoException;
import br.gov.mt.sesp.carteirafuncional.models.Administrativo;
import br.gov.mt.sesp.carteirafuncional.models.Policial;
import br.gov.mt.sesp.carteirafuncional.repositories.BatalhaoRepository;
import br.gov.mt.sesp.carteirafuncional.repositories.PolicialRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Serviço responsável pelas regras negociais referentes à {@link Policial}
 */
@Service
public class PolicialService {

    private static final Logger LOGGER = LogManager.getLogger(PolicialService.class);

    private PolicialRepository policialRepository;

    private BatalhaoRepository batalhaoRepository;

    private ModelMapper modelMapper;

    @Autowired
    public PolicialService(PolicialRepository policialRepository,
                           BatalhaoRepository batalhaoRepository,
                           ModelMapper modelMapper) {
        this.policialRepository = policialRepository;
        this.batalhaoRepository = batalhaoRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Lista os policiais com base no filtro informado. Caso o filtro não tenha atributos preenchidos, lista todos os policiais.
     *
     * @param filtro filtro no formato {@link Policial}, utilizado para busca pelas propriedades nome, cpf, matricula e email
     * @return Set<PolicialDTO> lista de policiais com base no filtro informado
     */
    public Page<PolicialDTO> listar(Pageable pageable, Policial filtro) {



        return policialRepository
                .findAll(pageable, filtro.getNome(), filtro.getCpf(), filtro.getMatricula(), filtro.getEmail())
                .map(policial -> modelMapper.map(policial, PolicialDTO.class));
    }

    /**
     * Salva um policial, criando um novo registro caso o identificador não tenha sido informado e alterando
     * um policial existente caso o identificador tenha sido informado.
     *
     * @param policialSaveDTO dados do policial para salvar, no formato {@link PolicialSaveDTO}
     * @return PolicialDTO dados do policial salvo, no formato {@link PolicialDTO}
     */
    public PolicialDTO salvar(PolicialSaveDTO policialSaveDTO) {
        Policial policial = desconstruirDTO(policialSaveDTO);

        validar(policial);
        policial = policialRepository.save(policial);

        return construirDTO(policial);
    }

    /**
     * Busca um policial com base no identificador informado.
     *
     * @param id identificador do policial
     * @return PolicialDTO dados do policial encontrado, no formato {@link PolicialDTO}
     */
    public PolicialDTO buscar(Long id) {
        Policial policial = policialRepository
                .findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("policial.naoEncontrado"));

        return construirDTO(policial);
    }

    /**
     * Exclui um policial com base no identificador informado.
     *
     * @param id identificador do policial que será excluído
     */
    public void excluir(Long id) {
        policialRepository.deleteById(id);
    }

    /**
     * Converte os dados de {@link Policial} para {@link PolicialDTO}.
     *
     * @param policial dados a serem convertidos no formato {@link Policial}
     * @return PolicialDTO dados convertidos no formato {@link PolicialDTO}
     */
    private PolicialDTO construirDTO(Policial policial) {
        return modelMapper.map(policial, PolicialDTO.class);
    }

    /**
     * Converte os dados de {@link PolicialDTO} para {@link Policial}.
     *
     * @param policialSaveDTO dados a serem convertidos no formato {@link PolicialSaveDTO}
     * @return Policial dados convertidos no formato {@link Policial}
     */
    private Policial desconstruirDTO(PolicialSaveDTO policialSaveDTO) {
        Policial policial = modelMapper.map(policialSaveDTO, Policial.class);

        atribuirBatalhao(policialSaveDTO, policial);

        return policial;
    }

    /**
     * Atribui os dados referentes ao batalhao do policial, da origem <b>policialSaveDTO</b>, para o destino <b>policial</b>.
     *
     * @param policialSaveDTO origem dos dados do batalhao no formato {@link PolicialSaveDTO}
     * @param policial        destino dos dados do batalhao no formato {@link Policial}
     */
    private void atribuirBatalhao(PolicialSaveDTO policialSaveDTO, Policial policial) {

        LOGGER.debug("PolicialSaveDto: " + policialSaveDTO);

        if (policialSaveDTO.getIdBatalhao() != null) {
            policial.setBatalhao(batalhaoRepository.findById(policialSaveDTO.getIdBatalhao())
                    .orElseThrow(() -> new RegistroNaoEncontradoException("batalhao.naoEncontrado")));
        }
    }

    /**
     * Valida os dados do policial informado.
     *
     * @param policial dados do policial para validação no formato {@link Policial}
     */
    private void validar(Policial policial) {
        if (policial.getBatalhao() == null) {
            throw new NegocioException("policial.semBatalhao");
        }
    }

    /**
     * Metodo que escuta as mensagem que chegam na fila descrita e retorna o
     * objeto policialDTO
     *
     * @param policialDTO dados do policialDTO com o id para busca no repositório
     *
     * @return PolicialDTO policialDTO
     *
     */
    @RabbitListener(queues = "q.carteirafuncional.policial.busca", concurrency = "2")
    public PolicialDTO buscarPorID(PolicialDTO policialDTO) {



        Optional<Policial> policialBuscado = policialRepository
                .findById(policialDTO.getId()).stream().findFirst();

        return policialBuscado
                .map(policial -> modelMapper
                        .map(policial, PolicialDTO.class))
                .orElse(null);
    }

}
