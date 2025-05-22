package com.quickplate.service;

import com.quickplate.config.RabbitMQConfig;
import com.quickplate.controller.AuthController.RegisterReq;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RegistrationEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public RegistrationEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUserRegistration(RegisterReq req) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.USER_REG_EXCHANGE,
            RabbitMQConfig.USER_REG_ROUTING_KEY,
            req
        );
    }
}