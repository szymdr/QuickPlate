package com.quickplate.service;

import com.quickplate.model.User;
import com.quickplate.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RegistrationEventConsumer {

    @RabbitListener(queues = RabbitMQConfig.USER_REG_QUEUE)
    public void handleUserRegistration(User user) {
        System.out.println("Odebrano event rejestracji: " + user.getEmail());
    }
}