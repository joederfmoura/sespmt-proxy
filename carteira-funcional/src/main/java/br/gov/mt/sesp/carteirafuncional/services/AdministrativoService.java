package br.gov.mt.sesp.carteirafuncional.services;

import br.gov.mt.sesp.carteirafuncional.dtos.AdministrativoDTO;
import br.gov.mt.sesp.carteirafuncional.dtos.AdministrativoSaveDTO;
import br.gov.mt.sesp.carteirafuncional.exceptions.NegocioException;
import br.gov.mt.sesp.carteirafuncional.exceptions.RegistroNaoEncontradoException;
import br.gov.mt.sesp.carteirafuncional.models.Administrativo;
import br.gov.mt.sesp.carteirafuncional.repositories.AdministrativoRepository;
import br.gov.mt.sesp.carteirafuncional.repositories.SetorRepository;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Serviço responsável pelas regras negociais referentes à {@link Administrativo}
 */
@Service
public class AdministrativoService {

    private static final Logger LOGGER = Logger.getLogger(AdministrativoService.class.getName());
    private static final String MENSAGEM_CONSULTA_ADM = "Recebida mensagem do serviço Gerenciamento Ocorrência para" +
            " consulta de administrativo.";

    private AdministrativoRepository administrativoRepository;

    private SetorRepository setorRepository;

    private ModelMapper modelMapper;

    @Autowired
    public AdministrativoService(AdministrativoRepository administrativoRepository,
                                 SetorRepository setorRepository,
                                 ModelMapper modelMapper) {
        this.administrativoRepository = administrativoRepository;
        this.setorRepository = setorRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Lista os administrativos com base no filtro informado. Caso o filtro não tenha atributos preenchidos, lista todos os administrativos.
     *
     * @param filtro filtro no formato {@link Administrativo}, utilizado para busca pelas propriedades nome, cpf, matricula e email
     * @return Set<AdministrativoDTO> lista de administrativos com base no filtro informado
     */
    public Page<AdministrativoDTO> listar(Pageable pageable, Administrativo filtro) {
        return administrativoRepository
                .findAll(pageable, filtro.getNome(), filtro.getCpf(), filtro.getMatricula(), filtro.getEmail())
                .map(administrativo -> modelMapper.map(administrativo, AdministrativoDTO.class));
    }

    /**
     * Salva um administrativo, criando um novo registro caso o identificador não tenha sido informado e alterando
     * um administrativo existente caso o identificador tenha sido informado.
     *
     * @param administrativoSaveDTO dados do administrativo para salvar, no formato {@link AdministrativoSaveDTO}
     * @return AdministrativoDTO dados do administrativo salvo, no formato {@link AdministrativoDTO}
     */
    public AdministrativoDTO salvar(AdministrativoSaveDTO administrativoSaveDTO) {
        Administrativo administrativo = desconstruirDTO(administrativoSaveDTO);

        validar(administrativo);
        administrativo = administrativoRepository.save(administrativo);

        return construirDTO(administrativo);
    }

    /**
     * Busca um administrativo com base no identificador informado.
     *
     * @param id identificador do administrativo
     * @return AdministrativoDTO dados do administrativo encontrado, no formato {@link AdministrativoDTO}
     */
    public AdministrativoDTO buscar(Long id) {
        Administrativo administrativo = administrativoRepository
                .findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("administrativo.naoEncontrado"));

        return construirDTO(administrativo);
    }

    /**
     * Exclui um administrativo com base no identificador informado.
     *
     * @param id identificador do administrativo que será excluído
     */
    public void excluir(Long id) {
        administrativoRepository.deleteById(id);
    }

    /**
     * Converte os dados de {@link Administrativo} para {@link AdministrativoDTO}.
     *
     * @param administrativo dados a serem convertidos no formato {@link Administrativo}
     * @return AdministrativoDTO dados convertidos no formato {@link AdministrativoDTO}
     */
    private AdministrativoDTO construirDTO(Administrativo administrativo) {
        return modelMapper.map(administrativo, AdministrativoDTO.class);
    }

    /**
     * Converte os dados de {@link AdministrativoDTO} para {@link Administrativo}.
     *
     * @param administrativoSaveDTO dados a serem convertidos no formato {@link AdministrativoSaveDTO}
     * @return Administrativo dados convertidos no formato {@link Administrativo}
     */
    private Administrativo desconstruirDTO(AdministrativoSaveDTO administrativoSaveDTO) {
        Administrativo administrativo = modelMapper.map(administrativoSaveDTO, Administrativo.class);

        atribuirSetor(administrativoSaveDTO, administrativo);

        return administrativo;
    }

    /**
     * Atribui os dados referentes ao setor do administrativo, da origem <b>administrativoSaveDTO</b>, para o destino <b>administrativo</b>.
     *
     * @param administrativoSaveDTO origem dos dados do setor no formato {@link AdministrativoSaveDTO}
     * @param administrativo        destino dos dados do setor no formato {@link Administrativo}
     */
    private void atribuirSetor(AdministrativoSaveDTO administrativoSaveDTO, Administrativo administrativo) {
        if (administrativoSaveDTO.getIdSetor() != null) {
            administrativo.setSetor(setorRepository.findById(administrativoSaveDTO.getIdSetor())
                    .orElseThrow(() -> new RegistroNaoEncontradoException("setor.naoEncontrado")));
        }
    }

    /**
     * Valida os dados do administrativo informado.
     *
     * @param administrativo dados do administrativo para validação no formato {@link Administrativo}
     */
    private void validar(Administrativo administrativo) {
        if (administrativo.getSetor() == null) {
            throw new NegocioException("administrativo.semSetor");
        }
    }

    /**
     * Metodo que escuta as mensagem que chegam na fila descrita e retorna o
     * objeto administrativoDTO
     *
     * @param administrativoDTO dados do administrativoDTO
     *
     * @return AdministrativoDTO administrativoDTO
     */
    @RabbitListener(queues = "q.carteirafuncional.administrativo.busca", concurrency = "2")
    public AdministrativoDTO buscarPorID(AdministrativoDTO administrativoDTO) {

        LOGGER.info(MENSAGEM_CONSULTA_ADM);

        Optional<Administrativo> administrativoBuscado = administrativoRepository
                .findById(administrativoDTO.getId()).stream().findFirst();

        return administrativoBuscado
                .map(administrativo -> modelMapper
                        .map(administrativo, AdministrativoDTO.class))
                .orElse(null);

    }




}
