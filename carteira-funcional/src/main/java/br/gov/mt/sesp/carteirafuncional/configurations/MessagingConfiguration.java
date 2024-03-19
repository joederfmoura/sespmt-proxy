package br.gov.mt.sesp.carteirafuncional.configurations;

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
    public RabbitTemplate template(CachingConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());

        return template;
    }

    @Bean
    public DirectExchange carteiraFuncionalAdmDirectExchange() {
        return new DirectExchange("de.carteirafuncional.administrativo");
    }

    @Bean
    DirectExchange carteiraFuncionalPolicialDirectExchange() {
        return new DirectExchange("de.carteirafuncional.policial");
    }

    @Bean
    public Queue carteiraFuncionalAdministrativoQueue() {
        return new Queue("q.carteirafuncional.administrativo.busca");
    }

    @Bean
    public Queue carteiraFuncionalPolicialQueue() {
        return new Queue("q.carteirafuncional.policial.busca");
    }

    @Bean
    public Binding bindingAdministrativo(DirectExchange carteiraFuncionalAdmDirectExchange, Queue carteiraFuncionalAdministrativoQueue) {
        return BindingBuilder.bind(carteiraFuncionalAdministrativoQueue)
                .to(carteiraFuncionalAdmDirectExchange)
                .with("rk.carteirafuncional.administrativo.buscarporid");
    }

    @Bean
    public Binding bindingPolicial(DirectExchange carteiraFuncionalPolicialDirectExchange, Queue carteiraFuncionalPolicialQueue) {
        return BindingBuilder.bind(carteiraFuncionalPolicialQueue)
                .to(carteiraFuncionalPolicialDirectExchange)
                .with("rk.carteirafuncional.policial.buscarporid");
    }


}
