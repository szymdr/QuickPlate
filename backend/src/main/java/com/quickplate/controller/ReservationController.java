package com.quickplate.controller;

import com.quickplate.model.Reservation;
import com.quickplate.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationRepository repo;

    @GetMapping
    public ResponseEntity<List<Reservation>> getAll() {
        return ResponseEntity.ok(repo.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Optional<Reservation> opt = repo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Reservation not found");
        }
        return ResponseEntity.ok(opt.get());
    }

    @PostMapping
    public ResponseEntity<Reservation> create(@RequestBody Reservation r) {
        if (r.getId() == null) r.setId(UUID.randomUUID());
        Reservation saved = repo.save(r);
        return ResponseEntity
            .created(URI.create("/api/reservations/" + saved.getId()))
            .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Reservation r) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(404).body("Reservation not found");
        }
        r.setId(id);
        return ResponseEntity.ok(repo.save(r));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.status(404).body("Reservation not found");
        }
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}