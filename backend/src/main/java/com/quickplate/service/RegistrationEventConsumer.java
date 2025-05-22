package com.quickplate.service;

import com.quickplate.config.RabbitMQConfig;
import com.quickplate.controller.AuthController.RegisterReq;
import com.quickplate.model.User;
import com.quickplate.model.AccountType;
import com.quickplate.repository.UserRepository;
import com.quickplate.repository.AccountTypeRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class RegistrationEventConsumer {

    private final UserRepository userRepo;
    private final AccountTypeRepository typeRepo;
    private final BCryptPasswordEncoder encoder;

    public RegistrationEventConsumer(UserRepository userRepo,
                                     AccountTypeRepository typeRepo,
                                     BCryptPasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.typeRepo = typeRepo;
        this.encoder  = encoder;
    }

    @RabbitListener(queues = RabbitMQConfig.USER_REG_QUEUE)
    public void handleUserRegistration(RegisterReq req) {
        AccountType acct = typeRepo.findByName(req.role())
            .orElseThrow(() -> new RuntimeException("Unknown role: " + req.role()));
        User u = new User();
        u.setFirstName(req.firstName());
        u.setLastName(req.lastName());
        u.setEmail(req.email());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setAccountType(acct);
        userRepo.save(u);
    }
}