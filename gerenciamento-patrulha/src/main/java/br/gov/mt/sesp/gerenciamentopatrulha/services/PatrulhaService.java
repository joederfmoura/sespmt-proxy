package br.gov.mt.sesp.gerenciamentopatrulha.services;

import br.gov.mt.sesp.gerenciamentopatrulha.configurations.LogConfiguration;
import br.gov.mt.sesp.gerenciamentopatrulha.dtos.*;
import br.gov.mt.sesp.gerenciamentopatrulha.exceptions.NegocioException;
import br.gov.mt.sesp.gerenciamentopatrulha.exceptions.RegistroNaoEncontradoException;
import br.gov.mt.sesp.gerenciamentopatrulha.models.Patrulha;
import br.gov.mt.sesp.gerenciamentopatrulha.models.PatrulhaBairro;
import br.gov.mt.sesp.gerenciamentopatrulha.models.PatrulhaPolicial;
import br.gov.mt.sesp.gerenciamentopatrulha.repositories.PatrulhaRepository;
import org.apache.logging.log4j.LogManager;
import org.modelmapper.ModelMapper;
import org.slf4j.MDC;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.Logger;

@Service
public class PatrulhaService {

    private static final Logger LOGGER = LogManager.getLogger(PatrulhaService.class.getName());
    private static final String MSG_ERRO_ENVIO_MENSAGEM = "Não foi possível publicar mensagem.";
    private static final String MSG_NOTIFICACAO_PATRULHAS = "Patrulhas notificadas do Boletim %s no bairro %s";
    private static final String MSG_RECEBIDA_COMPENSACAO_TAREFA_BO = "Mensagem referente a Compensação de Tarefa do BO %s recebida no serviço Gerenciamento Patrulhas.";
    private static final String MSG_RECEBIDA_NOVO_BO = "Mensagem referente ao novo BO recebida no serviço Gerenciamento Patrulhas.";
    private static final String MSG_COMPENSACAO_TAREFA_INICIADA = "Flag de compensação de tarefas identificada. Iniciando compensação de tarefas para o BO %s.";
    private static final String MSG_BUSCA_BAIRRO = "Consulta por bairro realizada no serviço Registro Geral.";
    private static final String MSG_BUSCA_POLICIAL = "Consulta por policial realizada no serviço Carteira Funcional.";
    private static final String MSG_ERRO_PATRULHAS = "Não foi possível publicar mensagem de patrulhas";
    private static final String MSG_PATRULHAS = "Mensagem de notificação de patrulhas na publicação de um novo boletim foi enviada aos outros microsserviços.";
    private static final String ROUTING_KEY_BAIRRO = "rk.registrogeral.bairro.buscarporid";
    private static final String ROUTING_KEY_POLICIAL = "rk.carteirafuncional.policial.buscarporid";
    private static final String ROUTING_KEY_OCORRENCIA = "rk.gerenciamentoocorrencia.boletimocorrencia.novo";
    private static final String ROUTING_KEY_PATRULHA = "rk.gerenciamentopatrulha.boletimocorrencia.compensacaotarefa";
    private static final String ROUTING_KEY_PROCURADO = "rk.gerenciamentoprocurado.boletimocorrencia.compensacaotarefa";


    private PatrulhaRepository patrulhaRepository;

    private ModelMapper modelMapper;

    private RabbitTemplate template;

    private DirectExchange registroGeralBairroDirectExchange;

    private DirectExchange carteiraFuncionalPolicialDirectExchange;

    private final TopicExchange gerenciamentoOcorrenciaTopicExchange;


    @Autowired
    public PatrulhaService(
            PatrulhaRepository patrulhaRepository,
            ModelMapper modelMapper,
            RabbitTemplate template,
            DirectExchange registroGeralBairroDirectExchange,
            DirectExchange carteiraFuncionalPolicialDirectExchange,
            TopicExchange gerenciamentoOcorrenciaTopicExchange
    ) {
        this.patrulhaRepository = patrulhaRepository;
        this.modelMapper = modelMapper;
        this.template = template;
        this.registroGeralBairroDirectExchange = registroGeralBairroDirectExchange;
        this.carteiraFuncionalPolicialDirectExchange = carteiraFuncionalPolicialDirectExchange;
        this.gerenciamentoOcorrenciaTopicExchange = gerenciamentoOcorrenciaTopicExchange;
    }

    /**
     * Lista as patrulhas com base no filtro informado. Caso o filtro não tenha atributos preenchidos, lista todas as patrulhas.
     *
     * @param filtro filtro no formato {@link Patrulha}, utilizado para busca pelas propriedades viatura, idPolicial e idBairro
     * @return Set<PatrulhaDTO> lista de patrulhas com base no filtro informado
     */
    public Page<PatrulhaDTO> listar(Pageable pageable, Patrulha filtro) {
        Set<Patrulha> patrulhas = new HashSet<>();

        Long idPolicial = null;
        Long idBairro = null;

        if (!filtro.getBairros().isEmpty()) {
            idBairro = filtro.getBairros().stream().findFirst().orElse(null).getIdBairro();
        }
        if (!filtro.getPoliciais().isEmpty()) {
            idPolicial = filtro.getPoliciais().stream().findFirst().orElse(null).getIdPolicial();
        }

        return patrulhaRepository.findAll(pageable, filtro.getViatura(), idBairro, idPolicial)
                .map(patrulha -> {

                    var patrulhaDTO = modelMapper.map(patrulha, PatrulhaDTO.class);
                    patrulhaDTO.setBairros(insereNomeBairroPatrulha(patrulha));
                    patrulhaDTO.setPoliciais(insereNomePolicialPatrulha(patrulha));

                    return patrulhaDTO;
                });
    }


    /**
     * Insere o nome dos bairros a partir dos seus ids
     * <p>
     * * @param Patrulha patrulha objeto contendo o Set de objetos Bairro
     *
     * @return List<PatrulhaBairroDTO> retorna a lista de bairros com o nome preenchido
     */
    private List<PatrulhaBairroDTO> insereNomeBairroPatrulha(Patrulha patrulha) {
        Set<PatrulhaBairro> patrulhaBairros = patrulha.getBairros();

        List<PatrulhaBairroDTO> listaPatrulhaBairro = new ArrayList<>();



        patrulhaBairros.forEach(patrulhaBairro -> {

            var patrulhaBairroDTO = new PatrulhaBairroDTO();
            BairroDTO bairroDTO = obterBairroPatrulha(patrulhaBairro.getIdBairro());

            patrulhaBairroDTO.setId(patrulhaBairro.getId());
            patrulhaBairroDTO.setIdBairro(bairroDTO.getId());
            patrulhaBairroDTO.setBairro(bairroDTO.getNome());
            patrulhaBairroDTO.setCidade(bairroDTO.getCidade().getNome());
            listaPatrulhaBairro.add(patrulhaBairroDTO);
        });

        return listaPatrulhaBairro;

    }


    /**
     * Insere o nome dos policiais a partir dos seus ids
     * <p>
     * * @param Patrulha patrulha objeto contendo o Set de objetos Policiais
     *
     * @return List<PatrulhaPolicialDTO> retorna a lista de policiais com o nome preenchido
     */
    private List<PatrulhaPolicialDTO> insereNomePolicialPatrulha(Patrulha patrulha) {

        Set<PatrulhaPolicial> patrulhaPoliciais = patrulha.getPoliciais();

        List<PatrulhaPolicialDTO> listaPatrulhaPolicial = new ArrayList<>();

        patrulhaPoliciais.forEach(patrulhaPolicial -> {

            var patrulhaPolicialDTO = new PatrulhaPolicialDTO();

            PolicialDTO policialDTO = obterPolicialPatrulha(patrulhaPolicial.getIdPolicial());

            patrulhaPolicialDTO.setId(patrulhaPolicial.getId());
            patrulhaPolicialDTO.setIdPolicial(policialDTO.getId());
            patrulhaPolicialDTO.setNomePolicial(policialDTO.getNome());
            listaPatrulhaPolicial.add(patrulhaPolicialDTO);
        });

        return listaPatrulhaPolicial;

    }


    /**
     * Salva uma patrulha, criando um novo registro caso o identificador não tenha sido informado e alterando
     * uma patrulha existente caso o identificador tenha sido informado.
     *
     * @param patrulhaSaveDTO dados da patrulha para salvar, no formato {@link PatrulhaSaveDTO}
     * @return PatrulhaDTO dados da patrulha salva, no formato {@link PatrulhaDTO}
     */
    public PatrulhaDTO salvar(PatrulhaSaveDTO patrulhaSaveDTO) {
        Patrulha patrulha = desconstruirDTO(patrulhaSaveDTO);

        validar(patrulha);
        patrulhaRepository.save(patrulha);

        return construirDTO(patrulha);
    }

    /**
     * Busca uma patrulha com base no identificador informado.
     *
     * @param id identificador da patrulha
     * @return PatrulhaDTO dados da patrulha encontrada, no formato {@link PatrulhaDTO}
     */
    public PatrulhaDTO buscar(Long id) {
        Patrulha patrulha = patrulhaRepository
                .findById(id)
                .orElseThrow(() -> new RegistroNaoEncontradoException("patrulha.naoEncontrada"));


        var patrulhaDTO = construirDTO(patrulha);
        patrulhaDTO.setBairros(insereNomeBairroPatrulha(patrulha));
        patrulhaDTO.setPoliciais(insereNomePolicialPatrulha(patrulha));

        return patrulhaDTO;
    }

    /**
     * Exclui uma patrulha com base no identificador informado.
     *
     * @param id identificador da patrulha que será excluída
     */
    public void excluir(Long id) {
        patrulhaRepository.deleteById(id);
    }

    /**
     * Converte os dados de {@link Patrulha} para {@link PatrulhaDTO}.
     *
     * @param patrulha dados a serem convertidos no formato {@link Patrulha}
     * @return PatrulhaDTO dados convertidos no formato {@link PatrulhaDTO}
     */
    private PatrulhaDTO construirDTO(Patrulha patrulha) {
        return modelMapper.map(patrulha, PatrulhaDTO.class);
    }

    /**
     * Converte os dados de {@link PatrulhaDTO} para {@link Patrulha}.
     *
     * @param patrulhaSaveDTO dados a serem convertidos no formato {@link PatrulhaSaveDTO}
     * @return Patrulha dados convertidos no formato {@link Patrulha}
     */
    private Patrulha desconstruirDTO(PatrulhaSaveDTO patrulhaSaveDTO) {
        Patrulha patrulha = modelMapper.map(patrulhaSaveDTO, Patrulha.class);

        for (Long idBairro : patrulhaSaveDTO.getBairros()) {
            patrulha.incluirBairro(idBairro);
        }

        for (Long idPolicial : patrulhaSaveDTO.getPoliciais()) {
            patrulha.incluirPolicial(idPolicial);
        }

        return patrulha;
    }

    /**
     * Valida os dados da patrulha informada.
     *
     * @param patrulha dados da patrulha para validação no formato {@link Patrulha}
     */
    private void validar(Patrulha patrulha) {
        if (patrulha.getBairros().isEmpty()) {
            throw new NegocioException("patrulha.semBairro");
        }
        if (patrulha.getPoliciais().isEmpty()) {
            throw new NegocioException("patrulha.semPoliciais");
        }
    }

    /**
     * Recebe a notificação do cadastro de um novo boletim
     *
     * @param boletimOcorrenciaDTO dados do boletim de ocorrencia para notificação da patrulha
     */
    @RabbitListener(queues = "q.gerenciamentopatrulha.boletimocorrencia.novo")
    private void notificaPatrulhasNovoBO(BoletimOcorrenciaDTO boletimOcorrenciaDTO, @Header("X-Correlation-Id") String correlationId) {
        MDC.put(LogConfiguration.PROPRIEDADE_CORRELATION_ID, correlationId);
        try {
            LOGGER.info(MSG_RECEBIDA_NOVO_BO);

            if (boletimOcorrenciaDTO.isCompensacaoTarefa()) {
                LOGGER.info(String.format(MSG_COMPENSACAO_TAREFA_INICIADA, boletimOcorrenciaDTO.getId()));

                enviarMensagemTopicoOcorrencia(ROUTING_KEY_PATRULHA, boletimOcorrenciaDTO);

                return;
            }

            LOGGER.info(String.format(MSG_NOTIFICACAO_PATRULHAS, boletimOcorrenciaDTO.getId(), boletimOcorrenciaDTO.getBairro()));

            enviarMensagemTopicoOcorrencia(ROUTING_KEY_OCORRENCIA, boletimOcorrenciaDTO);
        } catch (AmqpException e) {
            LOGGER.error(String.format(MSG_ERRO_ENVIO_MENSAGEM, getStackTraceAmqpEx(e)));
        }
    }

    /**
     * Recebe a notificação da compensação de tarefas de um boletim
     *
     * @param boletimOcorrenciaDTO dados do boletim
     */
    @RabbitListener(queues = "q.gerenciamentopatrulha.boletimocorrencia.compensacaotarefa")
    private void notificarPatrulhasCompensacaoBO(BoletimOcorrenciaDTO boletimOcorrenciaDTO, @Header("X-Correlation-Id") String correlationId) {
        MDC.put(LogConfiguration.PROPRIEDADE_CORRELATION_ID, correlationId);

        try {
            LOGGER.info(String.format(MSG_RECEBIDA_COMPENSACAO_TAREFA_BO,
                    boletimOcorrenciaDTO.getId(),
                    boletimOcorrenciaDTO.getBairro()));

            enviarMensagemTopicoOcorrencia(ROUTING_KEY_PROCURADO, boletimOcorrenciaDTO);
        } catch (AmqpException e) {
            LOGGER.error(String.format(MSG_ERRO_ENVIO_MENSAGEM, getStackTraceAmqpEx(e)));
        }

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

            LOGGER.info(MSG_PATRULHAS);
        } catch (AmqpException amqpException) {
            String stackTraceStr = getStackTraceAmqpEx(amqpException);
            LOGGER.error(String.format(MSG_ERRO_PATRULHAS, stackTraceStr));
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

    /**
     * Obtem o nome de bairro a partir do id
     *
     * @param id id do bairro a ser buscado
     * @return BairroDTO bairroDTO
     */
    private BairroDTO obterBairroPatrulha(Long id) {
        BairroDTO bairroDTO = new BairroDTO(id);

        LOGGER.info(MSG_BUSCA_BAIRRO);

        bairroDTO = template.convertSendAndReceiveAsType(registroGeralBairroDirectExchange.getName(),
                ROUTING_KEY_BAIRRO,
                bairroDTO,
                new ParameterizedTypeReference<>() {
                });

        return bairroDTO == null ? null : bairroDTO;
    }


    /**
     * Obtem o nome de policial a partir do id
     *
     * @param id id do policial a ser buscado
     * @return PolicialDTO policialDTO
     */
    private PolicialDTO obterPolicialPatrulha(Long id) {

        PolicialDTO policialDTO = new PolicialDTO(id);
        LOGGER.info(MSG_BUSCA_POLICIAL);

        policialDTO = template.convertSendAndReceiveAsType(carteiraFuncionalPolicialDirectExchange.getName(),
                ROUTING_KEY_POLICIAL,
                policialDTO,
                new ParameterizedTypeReference<>() {
                });

        return policialDTO == null ? null : policialDTO;
    }

}
