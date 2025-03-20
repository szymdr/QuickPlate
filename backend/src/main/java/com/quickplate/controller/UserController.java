package com.quickplate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Map<Integer, Map<String, String>> users = new
    HashMap<>() {{
        put(1, Map.of("id", "1", "name", "Jan Kowalski", "email", "jan@example.com"));
        put(2, Map.of("id", "2", "name", "Anna Nowak", "email", "anna@example.com"));
    }};
    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return ResponseEntity.ok(users.values());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable int id) {
        if (!users.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        return ResponseEntity.ok(users.get(id));
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody Map<String, String> user) {
        int id = users.size() + 1;
        user.put("id", String.valueOf(id));
        users.put(id, user);
        return ResponseEntity.created(URI.create("/api/users/" + id)).body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable int id, @RequestBody Map<String, String> user) {
        if (!users.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        user.put("id", String.valueOf(id));
        users.put(id, user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable int id) {
        if (!users.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        users.remove(id);
        return ResponseEntity.ok(Map.of("message", "User deleted"));
    }
}