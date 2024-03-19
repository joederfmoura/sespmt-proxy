package br.gov.mt.sesp.gerenciamentoprocurado.configurations;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfiguration {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue procuradoQueue() {
        return new Queue("q.gerenciamentoprocurado.boletimocorrencia.novo");
    }

    @Bean
    public Queue procuradoCanceladoQueue() {
        return new Queue("q.gerenciamentoprocurado.boletimocorrencia.cancelado");
    }

    @Bean
    public Queue procuradoCompensacaoQueue() {
        return new Queue("q.gerenciamentoprocurado.boletimocorrencia.compensacaotarefa");
    }

    @Bean
    public Queue patrulhaQueue() {
        return new Queue("q.gerenciamentopatrulha.boletimocorrencia.novo");
    }

    @Bean
    public TopicExchange gerenciamentoOcorrenciaTopicExchange() {
        return new TopicExchange("te.gerenciamentoocorrencia.boletimocorrencia");
    }

    @Bean
    public Binding procuradoBinding(Queue procuradoQueue, TopicExchange gerenciamentoOcorrenciaTopicExchange) {
        return BindingBuilder.bind(procuradoQueue)
                .to(gerenciamentoOcorrenciaTopicExchange)
                .with("rk.gerenciamentoprocurado.boletimocorrencia.novo");
    }

    @Bean
    public Binding procuradoCompensacaoBinding(Queue procuradoCompensacaoQueue, TopicExchange gerenciamentoOcorrenciaTopicExchange) {
        return BindingBuilder.bind(procuradoCompensacaoQueue)
                .to(gerenciamentoOcorrenciaTopicExchange)
                .with("rk.gerenciamentoprocurado.boletimocorrencia.compensacaotarefa");
    }

    @Bean
    public Binding patrulhaBinding(Queue patrulhaQueue, TopicExchange gerenciamentoOcorrenciaTopicExchange) {
        return BindingBuilder.bind(patrulhaQueue)
                .to(gerenciamentoOcorrenciaTopicExchange)
                .with("rk.gerenciamentopatrulha.boletimocorrencia.novo");
    }
}
