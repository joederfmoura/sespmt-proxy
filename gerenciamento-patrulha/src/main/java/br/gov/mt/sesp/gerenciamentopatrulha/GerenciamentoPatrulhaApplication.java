package br.gov.mt.sesp.gerenciamentopatrulha;

import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import br.gov.mt.sesp.gerenciamentopatrulha.configurations.LogConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSpringDataWebSupport
@EnableSwagger2
@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class,
    UserDetailsServiceAutoConfiguration.class
})
public class GerenciamentoPatrulhaApplication {

	public static void main(String[] args) {
		SpringApplication.run(GerenciamentoPatrulhaApplication.class, args);
	}

	@Bean
    public static BeanPostProcessor bpp() {
        return new BeanPostProcessor() {

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof RabbitTemplate) {
                    ((RabbitTemplate) bean).setBeforePublishPostProcessors(m -> {   
                        m.getMessageProperties().getHeaders().put("X-Correlation-Id",MDC.get(LogConfiguration.PROPRIEDADE_CORRELATION_ID));
                        return m;
                    });
                }
                return bean;
            }
        };
    }	
}
