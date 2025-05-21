package com.quickplate.controller;

import com.quickplate.model.RestaurantDetails;
import com.quickplate.repository.RestaurantDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/restaurant-details")
public class RestaurantDetailsController {

    @Autowired
    private RestaurantDetailsRepository repo;

    @GetMapping
    public ResponseEntity<List<RestaurantDetails>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Optional<RestaurantDetails> opt = repo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("RestaurantDetails not found");
        }
        return ResponseEntity.ok(opt.get());
    }

    @PostMapping
    public ResponseEntity<RestaurantDetails> create(@RequestBody RestaurantDetails rd) {
        if (rd.getId() == null) rd.setId(UUID.randomUUID());
        RestaurantDetails saved = repo.save(rd);
        return ResponseEntity
            .created(URI.create("/api/restaurant-details/" + saved.getId()))
            .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody RestaurantDetails rd) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(404).body("RestaurantDetails not found");
        }
        rd.setId(id);
        return ResponseEntity.ok(repo.save(rd));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(404).body("RestaurantDetails not found");
        }
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}