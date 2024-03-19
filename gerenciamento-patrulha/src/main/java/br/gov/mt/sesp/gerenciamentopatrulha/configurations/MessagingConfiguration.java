package br.gov.mt.sesp.gerenciamentopatrulha.configurations;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
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
    public Queue patrulhaQueue() {
        return new Queue("q.gerenciamentopatrulha.boletimocorrencia.novo");
    }

    @Bean
    public Queue patrulhaCompensacaoQueue() {
        return new Queue("q.gerenciamentopatrulha.boletimocorrencia.compensacaotarefa");
    }

    @Bean
    public DirectExchange registroGeralBairroDirectExchange() {
        return new DirectExchange("de.registrogeral.bairro");
    }

    @Bean
    public DirectExchange carteiraFuncionalPolicialDirectExchange(){
        return new DirectExchange("de.carteirafuncional.policial");
    }

    @Bean
    public TopicExchange gerenciamentoOcorrenciaTopicExchange() {
        return new TopicExchange("te.gerenciamentoocorrencia.boletimocorrencia");
    }

    @Bean
    public Binding patrulhaBinding(Queue patrulhaQueue, TopicExchange gerenciamentoOcorrenciaTopicExchange) {
        return BindingBuilder.bind(patrulhaQueue)
                .to(gerenciamentoOcorrenciaTopicExchange)
                .with("rk.gerenciamentopatrulha.boletimocorrencia.novo");
    }

    @Bean
    public Binding patrulhaCompensacaoBinding(Queue patrulhaCompensacaoQueue, TopicExchange gerenciamentoOcorrenciaTopicExchange) {
        return BindingBuilder.bind(patrulhaCompensacaoQueue)
                .to(gerenciamentoOcorrenciaTopicExchange)
                .with("rk.gerenciamentopatrulha.boletimocorrencia.compensacaotarefa");
    }
}
