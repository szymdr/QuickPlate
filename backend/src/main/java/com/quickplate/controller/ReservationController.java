package com.quickplate.controller;

import com.quickplate.model.Reservation;
import com.quickplate.model.Restaurant;
import com.quickplate.model.User;
import com.quickplate.repository.ReservationRepository;
import com.quickplate.repository.RestaurantRepository;
import com.quickplate.repository.UserRepository;
import com.quickplate.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationRepository repo;
    public ReservationController(ReservationRepository repo) { this.repo = repo; }

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RestaurantRepository restaurantRepo;

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

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> getByUserId(@PathVariable UUID userId) {
        User user = userRepo.findById(userId)
          .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Reservation> reservations = repo.findByUser(user);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Reservation>> getByRestaurant(
        @PathVariable UUID restaurantId
    ) {
        List<Reservation> list = repo.findByRestaurant_Id(restaurantId);
        return ResponseEntity.ok(list);
    }

    record ReservationReq(
      UUID restaurantId,
      int tableNumber,
      LocalDateTime reservationTime
    ) {}

    @PostMapping
    public ResponseEntity<Reservation> create(
        @RequestBody ReservationReq req,
        Principal principal
    ) {
        UUID userId = UUID.fromString(principal.getName());
        User current = userRepo.findById(userId)
          .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Restaurant rest = restaurantRepo.findById(req.restaurantId())
          .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

        Reservation r = new Reservation();
        r.setId(UUID.randomUUID());
        r.setUser(current);
        r.setRestaurant(rest);
        r.setTableNumber(req.tableNumber());
        r.setReservationTime(req.reservationTime());

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