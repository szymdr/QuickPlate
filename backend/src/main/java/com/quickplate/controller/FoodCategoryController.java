package com.quickplate.controller;

import com.quickplate.model.FoodCategory;
import com.quickplate.repository.FoodCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/food-categories")
public class FoodCategoryController {

    @Autowired
    private FoodCategoryRepository repo;

    @GetMapping
    public ResponseEntity<List<FoodCategory>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Optional<FoodCategory> opt = repo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("FoodCategory not found");
        }
        return ResponseEntity.ok(opt.get());
    }

    @PostMapping
    public ResponseEntity<FoodCategory> create(@RequestBody FoodCategory fc) {
        if (fc.getId() == null) fc.setId(UUID.randomUUID());
        FoodCategory saved = repo.save(fc);
        return ResponseEntity
            .created(URI.create("/api/food-categories/" + saved.getId()))
            .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody FoodCategory fc) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(404).body("FoodCategory not found");
        }
        fc.setId(id);
        return ResponseEntity.ok(repo.save(fc));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(404).body("FoodCategory not found");
        }
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}