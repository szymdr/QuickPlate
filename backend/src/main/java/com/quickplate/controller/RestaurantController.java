package com.quickplate.controller;

import com.quickplate.model.MenuItem;
import com.quickplate.repository.MenuItemRepository;
import com.quickplate.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.quickplate.model.Restaurant;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @GetMapping
    public ResponseEntity<List<Restaurant>> getRestaurants() {
        List<Restaurant> list = restaurantRepository.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRestaurantById(@PathVariable UUID id) {
        Optional<Restaurant> opt = restaurantRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Restaurant not found");
        }
        return ResponseEntity.ok(opt.get());
    }

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        if (restaurant.getId() == null) {
            restaurant.setId(UUID.randomUUID());
        }
        Restaurant saved = restaurantRepository.save(restaurant);
        return ResponseEntity
            .created(URI.create("/api/restaurants/" + saved.getId()))
            .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRestaurant(@PathVariable UUID id, @RequestBody Restaurant restaurant) {
        if (!restaurantRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Restaurant not found");
        }
        restaurant.setId(id);
        Restaurant updated = restaurantRepository.save(restaurant);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRestaurant(@PathVariable UUID id) {
        if (!restaurantRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Restaurant not found");
        }
        restaurantRepository.deleteById(id);
        return ResponseEntity.ok("Restaurant deleted successfully");
    }

    @GetMapping("/{id}/menu")
    public ResponseEntity<List<MenuItem>> getMenuByRestaurant(@PathVariable UUID id) {
        if (!restaurantRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        List<MenuItem> menu = menuItemRepository.findByRestaurantId(id);
        return ResponseEntity.ok(menu);
    }
}
