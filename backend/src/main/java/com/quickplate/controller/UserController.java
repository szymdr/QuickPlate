package com.quickplate.controller;

import com.quickplate.exception.ResourceNotFoundException;
import com.quickplate.model.User;
import com.quickplate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
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

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        // Principal.getName() should be the userId (as set by your JWT filter)
        UUID userId = UUID.fromString(principal.getName());
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setPasswordHash(null);   // don’t send password back
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(
        @PathVariable UUID id,
        @RequestBody User updates,
        Principal principal
    ) {
        UUID me = UUID.fromString(principal.getName());
        if (!me.equals(id)) {
            return ResponseEntity.status(403).build();
        }
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setFirstName(updates.getFirstName());
        user.setLastName(updates.getLastName());
        user.setEmail(updates.getEmail());
        User saved = userRepository.save(user);
        saved.setPasswordHash(null);
        return ResponseEntity.ok(saved);
    }
}