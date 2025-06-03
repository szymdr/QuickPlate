package com.quickplate.controller;

import com.quickplate.model.Restaurant;
import com.quickplate.model.Reservation;
import com.quickplate.model.User;
import com.quickplate.repository.RestaurantRepository;
import com.quickplate.repository.ReservationRepository;
import com.quickplate.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
  private final RestaurantRepository restaurantRepo;
  private final ReservationRepository reservationRepo;
  private final UserRepository userRepo;

  public AdminController(
      RestaurantRepository restaurantRepo,
      ReservationRepository reservationRepo,
      UserRepository userRepo
  ) {
    this.restaurantRepo    = restaurantRepo;
    this.reservationRepo   = reservationRepo;
    this.userRepo          = userRepo;
  }

  @GetMapping("/restaurants")
  public ResponseEntity<List<Restaurant>> allRestaurants() {
    return ResponseEntity.ok(restaurantRepo.findAll());
  }

  @GetMapping("/reservations")
  public ResponseEntity<List<Reservation>> allReservations() {
    return ResponseEntity.ok(reservationRepo.findAll());
  }

  @GetMapping("/users")
  public ResponseEntity<List<User>> allUsers() {
    return ResponseEntity.ok(userRepo.findAll());
  }
}