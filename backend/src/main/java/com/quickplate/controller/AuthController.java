package com.quickplate.controller;

import com.quickplate.model.User;
import com.quickplate.model.AccountType;
import com.quickplate.repository.UserRepository;
import com.quickplate.repository.AccountTypeRepository;
import com.quickplate.security.JwtUtil;
import com.quickplate.service.RegistrationEventProducer;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepo;
    private final AccountTypeRepository typeRepo;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final RegistrationEventProducer producer;

    public AuthController(UserRepository userRepo,
                          AccountTypeRepository typeRepo,
                          BCryptPasswordEncoder encoder,
                          JwtUtil jwtUtil,
                          RegistrationEventProducer producer) {
        this.userRepo = userRepo;
        this.typeRepo = typeRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.producer = producer;
    }

    public record RegisterReq(
        String firstName,
        String lastName,
        String email,
        String password,
        String role
    ) {}

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterReq req) {
        AccountType acct = typeRepo.findByName(req.role())
             .orElseThrow(() ->
                new RuntimeException("Nie ma takiej roli: " + req.role()));
        User u = new User();
        u.setFirstName(req.firstName());
        u.setLastName(req.lastName());
        u.setEmail(req.email());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setAccountType(acct);
        User saved = userRepo.save(u);

        try {
            producer.sendUserRegistration(saved);
        } catch (Exception ex) {
            System.err.println("Failed to publish registration event: " + ex.getMessage());
        }

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
        Optional<User> opt = userRepo.findByEmail(req.email());
        if (opt.isEmpty() || !encoder.matches(req.password(), opt.get().getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String jwt = jwtUtil.generateToken(opt.get().getId(), opt.get().getAccountType().getName());
        return ResponseEntity.ok(new TokenResponse(jwt));
    }

    record LoginRequest(String email, String password) {}
    record TokenResponse(String token) {}
}