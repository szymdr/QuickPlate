package com.quickplate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_REG_QUEUE    = "user.registration.queue";
    public static final String USER_REG_EXCHANGE = "user.registration.exchange";
    public static final String USER_REG_ROUTING_KEY = "user.register";

    @Bean
    Queue userRegistrationQueue() {
        return QueueBuilder.durable(USER_REG_QUEUE).build();
    }

    @Bean
    DirectExchange userRegistrationExchange() {
        return new DirectExchange(USER_REG_EXCHANGE);
    }

    @Bean
    Binding bindingUserRegistration(Queue userRegistrationQueue,
                                    DirectExchange userRegistrationExchange) {
        return BindingBuilder
            .bind(userRegistrationQueue)
            .to(userRegistrationExchange)
            .with(USER_REG_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf,
                                         Jackson2JsonMessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}