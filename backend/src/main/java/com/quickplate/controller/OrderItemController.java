package com.quickplate.controller;

import com.quickplate.model.OrderItem;
import com.quickplate.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    @Autowired
    private OrderItemRepository repo;

    @GetMapping
    public ResponseEntity<List<OrderItem>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Optional<OrderItem> opt = repo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("OrderItem not found");
        }
        return ResponseEntity.ok(opt.get());
    }

    @PostMapping
    public ResponseEntity<OrderItem> create(@RequestBody OrderItem oi) {
        if (oi.getId() == null) oi.setId(UUID.randomUUID());
        OrderItem saved = repo.save(oi);
        return ResponseEntity
            .created(URI.create("/api/order-items/" + saved.getId()))
            .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody OrderItem oi) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(404).body("OrderItem not found");
        }
        oi.setId(id);
        return ResponseEntity.ok(repo.save(oi));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(404).body("OrderItem not found");
        }
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}