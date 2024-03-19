package br.gov.mt.sesp.gerenciamentoprocurado.services;



import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import br.gov.mt.sesp.gerenciamentoprocurado.configurations.LogConfiguration;
import br.gov.mt.sesp.gerenciamentoprocurado.dtos.BoletimOcorrenciaDTO;
import br.gov.mt.sesp.gerenciamentoprocurado.dtos.SuspeitoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.slf4j.MDC;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import br.gov.mt.sesp.gerenciamentoprocurado.models.Procurado;
import br.gov.mt.sesp.gerenciamentoprocurado.dtos.ProcuradoDTO;
import br.gov.mt.sesp.gerenciamentoprocurado.dtos.ProcuradoSaveDTO;
import br.gov.mt.sesp.gerenciamentoprocurado.exceptions.NegocioException;
import br.gov.mt.sesp.gerenciamentoprocurado.exceptions.RegistroNaoEncontradoException;
import br.gov.mt.sesp.gerenciamentoprocurado.repositories.ProcuradoRepository;

/**
 * Serviço responsável pelas regras negociais referentes à {@link Procurado}
 */
@Service
public class ProcuradoService {

    private static final Logger LOGGER = LogManager.getLogger(ProcuradoService.class);
    private static final String MSG_PROCURADO_INSERIDO = "Suspeito de id %s do Boletim de Ocorrẽncia %s foi inserido na lista de procurados";
    private static final String MSG_PROCURADO_REMOVIDO = "Suspeito de id %s do Boletim de Ocorrẽncia %s foi removido da lista de procurados";
    private static final String MSG_PROCURADOS = "Mensagem de cadastro de procurados na publicação de um novo boletim foi enviada aos outros microsserviços.";
    private static final String MSG_RECEBIDA_NOVO_BO = "Mensagem referente ao novo BO recebida no serviço Gerenciamento Procurados.";
    private static final String MSG_RECEBIDA_COMPENSACAO_TAREFA_BO = "Mensagem referente a Compensação de Tarefa do BO %s recebida no serviço Gerenciamento Procurados.";
    private static final String MSG_RECEBIDA_CANCELAMENTO_BO = "Removendo suspeitos do BO Cancelado.";
    private static final String MSG_ERRO_ENVIO_MENSAGEM = "Não foi possível publicar mensagem.";
    private static final String ROUTING_KEY_PATRULHA = "rk.gerenciamentopatrulha.boletimocorrencia.novo";
    private static final String ROUTING_KEY_OCORRENCIA = "rk.gerenciamentoocorrencia.boletimocorrencia.compensacaotarefa";

    private ProcuradoRepository procuradoRepository;

    private ModelMapper modelMapper;

    private final RabbitTemplate template;

    private final TopicExchange gerenciamentoOcorrenciaTopicExchange;

    @Autowired
    public ProcuradoService(ProcuradoRepository procuradoRepository,
                            ModelMapper modelMapper,
                            RabbitTemplate template,
                            TopicExchange gerenciamentoOcorrenciaTopicExchange) {
        this.procuradoRepository = procuradoRepository;
        this.modelMapper = modelMapper;
        this.template = template;
        this.gerenciamentoOcorrenciaTopicExchange = gerenciamentoOcorrenciaTopicExchange;
    }

    /**
     * Lista os procurados com base no filtro informado. Caso o filtro não tenha atributos preenchidos, lista todas os procurados.
     *
     * @param filtro filtro no formato {@link Procurado}, utilizado para busca pelas propriedades caracteristicas, idPessoa e idBoletimOcorrencia
     * @return Set<PessoaDTO> lista de pessoas com base no filtro informado
     */
    public Page<ProcuradoDTO> listar(Pageable pageable, Procurado filtro) {

        return procuradoRepository
                .findAll(pageable, filtro.getCaracteristicas(), filtro.getIdPessoa(), filtro.getIdBoletimOcorrencia())
                .map(procurado -> modelMapper.map(procurado, ProcuradoDTO.class));
    }

    /**
     * Salva um procurado, criando um novo registro caso o identificador não tenha sido informado e alterando
     * um procurado existente caso o identificador tenha sido informado.
     *
     * @param procuradoSaveDTO dados do procurado para salvar, no formato {@link ProcuradoSaveDTO}
     * @return ProcuradoDTO dados do procurado salvo, no formato {@link ProcuradoDTO}
     */
    public ProcuradoDTO salvar(ProcuradoSaveDTO procuradoSaveDTO) {
        Procurado procurado = desconstruirDTO(procuradoSaveDTO);

        validar(procurado);
        procuradoRepository.save(procurado);

        ProcuradoDTO procuradoDTO = construirDTO(procurado);

        LOGGER.info(String.format(MSG_PROCURADO_INSERIDO, procuradoDTO.getId(), procuradoDTO.getIdBoletimOcorrencia()));

        return procuradoDTO;
    }

    /**
     * Busca um procurado com base no identificador informado.
     *
     * @param id identificador do procurado
     * @return ProcuradoDTO dados do procurado encontrado, no formato {@link ProcuradoDTO}
     */
    public ProcuradoDTO buscar(Long id) {
        Procurado procurado = procuradoRepository
                .findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("pessoa.naoEncontrada"));

        return construirDTO(procurado);
    }

    /**
     * Exclui um procurado com base no identificador informado.
     *
     * @param id identificador do procurado que será excluído
     */
    public void excluir(Long id) {
        procuradoRepository.deleteById(id);
    }

    /**
     * Converte os dados de {@link Procurado} para {@link ProcuradoDTO}.
     *
     * @param procurado dados a serem convertidos no formato {@link Procurado}
     * @return ProcuradoDTO dados convertidos no formato {@link ProcuradoDTO}
     */
    private ProcuradoDTO construirDTO(Procurado procurado) {
        return modelMapper.map(procurado, ProcuradoDTO.class);
    }

    /**
     * Converte os dados de {@link ProcuradoDTO} para {@link Procurado}.
     *
     * @param procuradoSaveDTO dados a serem convertidos no formato {@link ProcuradoSaveDTO}
     * @return Procurado dados convertidos no formato {@link Procurado}
     */
    private Procurado desconstruirDTO(ProcuradoSaveDTO procuradoSaveDTO) {
        Procurado procurado = modelMapper.map(procuradoSaveDTO, Procurado.class);

        return procurado;
    }

    /**
     * Valida os dados do procurado informado.
     *
     * @param procurado dados do procurado para validação no formato {@link Procurado}
     */
    private void validar(Procurado procurado) {
        if (procurado.getCaracteristicas() == null || procurado.getCaracteristicas().isBlank()) {
            throw new NegocioException("procurado.semCaracteristicas");
        }
    }

    /**
     * Atualiza os dados do procurado com base no novo boletim de ocorrencia criado
     *
     * @param boletimOcorrenciaDTO dados do boletim de ocorrencia para atualizacao dos dados dos procurados
     */
    @RabbitListener(queues = "q.gerenciamentoprocurado.boletimocorrencia.novo")
    private void atualizarProcurados(BoletimOcorrenciaDTO boletimOcorrenciaDTO, @Header("X-Correlation-Id") String correlationId) {
        MDC.put(LogConfiguration.PROPRIEDADE_CORRELATION_ID, correlationId);
        try {
            LOGGER.info(MSG_RECEBIDA_NOVO_BO);

            List<SuspeitoDTO> listaSuspeitos = boletimOcorrenciaDTO.getSuspeitos();
            Long idBoletim = boletimOcorrenciaDTO.getId();

            listaSuspeitos.forEach((suspeitoDTO) -> {
                var procuradoSaveDTO = new ProcuradoSaveDTO();
                procuradoSaveDTO.setIdPessoa(suspeitoDTO.getIdPessoa());
                procuradoSaveDTO.setCaracteristicas(suspeitoDTO.getCaracteristicas());
                procuradoSaveDTO.setIdBoletimOcorrencia(idBoletim);

                salvar(procuradoSaveDTO);
            });

            enviarMensagemTopicoOcorrencia(ROUTING_KEY_PATRULHA, boletimOcorrenciaDTO);
        } catch (AmqpException e) {
            LOGGER.error(String.format(MSG_ERRO_ENVIO_MENSAGEM, getStackTraceAmqpEx(e)));
        }
    }

    /**
     * Compensa a tarefa de registro de procurados no cadastro de um novo BO
     *
     * @param boletimOcorrenciaDTO dados do boletim de ocorrencia para notificação da patrulha
     */
    @RabbitListener(queues = "q.gerenciamentoprocurado.boletimocorrencia.compensacaotarefa")
    private void foo(BoletimOcorrenciaDTO boletimOcorrenciaDTO, @Header("X-Correlation-Id") String correlationId) {
        MDC.put(LogConfiguration.PROPRIEDADE_CORRELATION_ID, correlationId);
        try {
            LOGGER.info(String.format(MSG_RECEBIDA_COMPENSACAO_TAREFA_BO, boletimOcorrenciaDTO.getId()));

            removerProcuradosBO(boletimOcorrenciaDTO);

            enviarMensagemTopicoOcorrencia(ROUTING_KEY_OCORRENCIA, boletimOcorrenciaDTO);
        } catch (AmqpException e) {
            LOGGER.error(String.format(MSG_ERRO_ENVIO_MENSAGEM, getStackTraceAmqpEx(e)));
        }
    }

    /**
     * Remove os procurados vinculados ao boletim cancelado
     *
     * @param mensagem mensagem do Header Exchange
     */
    @RabbitListener(queues = "q.gerenciamentoprocurado.boletimocorrencia.cancelado")
    public void removerProcuradosBOCancelado(Message mensagem) throws JsonProcessingException {
        byte[] body = mensagem.getBody();
        ObjectMapper mapper = new ObjectMapper();
        BoletimOcorrenciaDTO boletimOcorrenciaDTO =
                mapper.readValue(new String(body), BoletimOcorrenciaDTO.class);

        LOGGER.info(String.format(MSG_RECEBIDA_CANCELAMENTO_BO, boletimOcorrenciaDTO.getId()));

        removerProcuradosBO(boletimOcorrenciaDTO);
    }

    /**
     * Adiciona a mensagem referente ao boletim de ocorrencia recebido como parametro
     *
     * @param routingKey           identificador de roteamento referente à fila
     * @param boletimOcorrenciaDTO Data Transfer Object do boletim de ocorrencia criado
     */
    private void enviarMensagemTopicoOcorrencia(String routingKey, BoletimOcorrenciaDTO boletimOcorrenciaDTO) {
        try {
            template.convertAndSend(gerenciamentoOcorrenciaTopicExchange.getName(),
                    routingKey,
                    boletimOcorrenciaDTO);

            LOGGER.info(MSG_PROCURADOS);
        } catch (AmqpException amqpException) {
            String stackTraceStr = getStackTraceAmqpEx(amqpException);
            LOGGER.error(String.format(MSG_ERRO_ENVIO_MENSAGEM, stackTraceStr));
        }
    }

    /**
     * Recebe a exceção e retorna o StackTrace em String
     *
     * @param exception exceção do tipo AmqpException que pode ocorrer ao tentar o envio da mensagem
     * @return StackTrace convertida para String para exibiçao no log
     */

    private String getStackTraceAmqpEx(AmqpException exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter(stringWriter);
        exception.printStackTrace(pw);

        return stringWriter.toString();
    }

    private void removerProcuradosBO(BoletimOcorrenciaDTO boletimOcorrenciaDTO) {
        Long idBo = boletimOcorrenciaDTO.getId();

        List<Procurado> listaProcurados = procuradoRepository.findAllByIdBoletimOcorrencia(idBo);

        listaProcurados.forEach(procurado -> {
            procuradoRepository.deleteById(procurado.getId());

            LOGGER.info(String.format(MSG_PROCURADO_REMOVIDO, procurado.getId(), procurado.getIdBoletimOcorrencia()));
        });

    }


}
