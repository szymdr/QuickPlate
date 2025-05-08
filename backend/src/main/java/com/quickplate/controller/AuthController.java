package com.quickplate.controller;

import com.quickplate.model.User;
import com.quickplate.repository.UserRepository;
import com.quickplate.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
        Optional<User> opt = userRepo.findByEmail(req.email());
        if (opt.isEmpty() || !encoder.matches(req.password(), opt.get().getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String jwt = jwtUtil.generateToken(opt.get().getId(), "USER");
        return ResponseEntity.ok(new TokenResponse(jwt));
    }

    record LoginRequest(String email, String password) {}
    record TokenResponse(String token) {}
}