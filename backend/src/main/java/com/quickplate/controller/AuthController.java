package com.quickplate.controller;

import com.quickplate.model.User;
import com.quickplate.repository.UserRepository;
import com.quickplate.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepo,
                          BCryptPasswordEncoder encoder,
                          JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public User register(@RequestBody User u) {
        u.setPasswordHash(encoder.encode(u.getPasswordHash()));
        return userRepo.save(u);
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest req) {
        Optional<User> opt = userRepo.findAll()
          .stream()
          .filter(u->u.getEmail().equals(req.email))
          .findFirst();
        if (opt.isEmpty() || !encoder.matches(req.password, opt.get().getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        User u = opt.get();
        String jwt = jwtUtil.generateToken(u.getId(), "USER");
        return new TokenResponse(jwt);
    }

    record LoginRequest(String email, String password) {}
    record TokenResponse(String token) {}
}