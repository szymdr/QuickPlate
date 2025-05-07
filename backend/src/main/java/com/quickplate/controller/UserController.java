package com.quickplate.controller;

import com.quickplate.model.User;
import com.quickplate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
         List<User> users = userRepository.findAll();
         return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
         Optional<User> userOpt = userRepository.findById(id);
         if (userOpt.isEmpty()) {
             return ResponseEntity.status(404).body("User not found");
         }
         return ResponseEntity.ok(userOpt.get());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
         if (user.getId() == null) {
             user.setId(UUID.randomUUID());
         }
         User savedUser = userRepository.save(user);
         return ResponseEntity.created(URI.create("/api/users/" + savedUser.getId())).body(savedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody User updatedUser) {
         if (!userRepository.existsById(id)) {
             return ResponseEntity.status(404).body("User not found");
         }
         updatedUser.setId(id);
         User savedUser = userRepository.save(updatedUser);
         return ResponseEntity.ok(savedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
         if (!userRepository.existsById(id)) {
             return ResponseEntity.status(404).body("User not found");
         }
         userRepository.deleteById(id);
         return ResponseEntity.ok("User deleted");
    }
}