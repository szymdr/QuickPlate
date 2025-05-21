package com.quickplate.controller;

import com.quickplate.model.AccountType;
import com.quickplate.repository.AccountTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/account-types")
public class AccountTypeController {

    @Autowired
    private AccountTypeRepository repo;

    @GetMapping
    public ResponseEntity<List<AccountType>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Optional<AccountType> opt = repo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("AccountType not found");
        }
        return ResponseEntity.ok(opt.get());
    }

    @PostMapping
    public ResponseEntity<AccountType> create(@RequestBody AccountType at) {
        if (at.getId() == null) at.setId(UUID.randomUUID());
        AccountType saved = repo.save(at);
        return ResponseEntity
            .created(URI.create("/api/account-types/" + saved.getId()))
            .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody AccountType at) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(404).body("AccountType not found");
        }
        at.setId(id);
        return ResponseEntity.ok(repo.save(at));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(404).body("AccountType not found");
        }
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}