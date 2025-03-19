package com.quickplate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private static final Map<Integer, Map<String, String>> restaurants = new
    HashMap<>() {{
        put(1, Map.of("id", "1", "name", "Restaurant 1", "address", "ul. Warszawska 1, Kraków", "phone", "123456789"));
        put(2, Map.of("id", "2", "name", "Restaurant 2", "address", "ul. Szlak 2, Kraków", "phone", "987654321"));
    }};
    @GetMapping
    public ResponseEntity<Object> getRestaurants() {
        return ResponseEntity.ok(restaurants.values());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> getRestaurantById(@PathVariable int id) {
        if (!restaurants.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Restaurant not found"));
        }
        return ResponseEntity.ok(restaurants.get(id));
    }

    @PostMapping
    public ResponseEntity<Object> createRestaurant(@RequestBody Map<String, String> restaurant) {
        int id = restaurants.size() + 1;
        restaurant.put("id", String.valueOf(id));
        restaurants.put(id, restaurant);
        return ResponseEntity.ok(restaurant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateRestaurant(@PathVariable int id, @RequestBody Map<String, String> restaurant) {
        if (!restaurants.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Restaurant not found"));
        }
        restaurant.put("id", String.valueOf(id));
        restaurants.put(id, restaurant);
        return ResponseEntity.ok(restaurant);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteRestaurant(@PathVariable int id) {
        if (!restaurants.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Restaurant not found"));
        }
        restaurants.remove(id);
        return ResponseEntity.ok(Map.of("message", "Restaurant deleted"));
    }
}
