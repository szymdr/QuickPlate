package com.quickplate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    
    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping
    public ResponseEntity<List<Restaurant>> getRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable UUID id) {
        Optional<Restaurant> restaurantOpt = restaurantRepository.findById(id);
        if (restaurantOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Restaurant not found");
        }
        return ResponseEntity.ok(restaurantOpt.get());
    }

    @PostMapping
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        if(restaurant.getId() == null) {
            restaurant.setId(UUID.randomUUID());
        }
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        return ResponseEntity.created(URI.create("/api/restaurants/" + savedRestaurant.getId())).body(savedRestaurant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable UUID id, @RequestBody Restaurant restaurant) {
        if (!restaurantRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Restaurant not found");
        }
        restaurant.setId(id);
        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        return ResponseEntity.ok(updatedRestaurant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRestaurant(@PathVariable UUID id) {
        if (!restaurantRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Restaurant not found");
        }
        restaurantRepository.deleteById(id);
        return ResponseEntity.ok("Restaurant deleted successfully");
}
