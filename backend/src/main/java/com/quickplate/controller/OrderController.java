package com.quickplate.controller;

import com.quickplate.model.Order;
import com.quickplate.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository repo;

    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Optional<Order> opt = repo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Order not found");
        }
        return ResponseEntity.ok(opt.get());
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order o) {
        if (o.getId() == null) o.setId(UUID.randomUUID());
        Order saved = repo.save(o);
        return ResponseEntity
            .created(URI.create("/api/orders/" + saved.getId()))
            .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Order o) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(404).body("Order not found");
        }
        o.setId(id);
        return ResponseEntity.ok(repo.save(o));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(404).body("Order not found");
        }
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}