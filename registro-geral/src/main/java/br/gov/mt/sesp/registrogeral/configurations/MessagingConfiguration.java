package br.gov.mt.sesp.registrogeral.configurations;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
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
    public DirectExchange registroGeralPessoaDirectExchange() {
        return new DirectExchange("de.registrogeral.pessoa");
    }

    @Bean
    public DirectExchange registroGeralBairroDirectExchange() {
        return new DirectExchange("de.registrogeral.bairro");
    }


    @Bean
    public Queue registroGeralPessoaQueue() {
        return new Queue("q.registrogeral.pessoa.busca");
    }

    @Bean
    public Queue registroGeralBairroQueue(){return new Queue("q.registrogeral.bairro.busca");}

    @Bean
    public Binding pessoaBinding(DirectExchange registroGeralPessoaDirectExchange, Queue registroGeralPessoaQueue) {
        return BindingBuilder.bind(registroGeralPessoaQueue)
                .to(registroGeralPessoaDirectExchange)
                .with("rk.registrogeral.pessoa.buscarporemail");
    }

    @Bean
    public Binding bairroBinding(DirectExchange registroGeralBairroDirectExchange, Queue registroGeralBairroQueue) {
        return BindingBuilder.bind(registroGeralBairroQueue)
                .to(registroGeralBairroDirectExchange)
                .with("rk.registrogeral.bairro.buscarporid");
    }
}
