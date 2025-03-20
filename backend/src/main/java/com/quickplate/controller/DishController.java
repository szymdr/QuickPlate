package com.quickplate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/dishes")
public class DishController {
    private static final Map<Integer, Map<String, String>> dishes = new
    HashMap<>() {{
        put(1, Map.of("id", "1", "name", "Pizza", "price", "20.00"));
        put(2, Map.of("id", "2", "name", "Pasta", "price", "15.00"));
    }};
    @GetMapping
    public ResponseEntity<Object> getDishes() {
        return ResponseEntity.ok(dishes.values());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Object> getDishById(@PathVariable int id) {
        if (!dishes.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Dish not found"));
        }
        return ResponseEntity.ok(dishes.get(id));
    }

    @PostMapping
    public ResponseEntity<Object> createDish(@RequestBody Map<String, String> dish) {
        int id = dishes.size() + 1;
        dish.put("id", String.valueOf(id));
        dishes.put(id, dish);
        return ResponseEntity.created(URI.create("/api/dishes/" + id)).body(dish);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateDish(@PathVariable int id, @RequestBody Map<String, String> dish) {
        if (!dishes.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Dish not found"));
        }
        dish.put("id", String.valueOf(id));
        dishes.put(id, dish);
        return ResponseEntity.ok(dish);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteDish(@PathVariable int id) {
        if (!dishes.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Dish not found"));
        }
        dishes.remove(id);
        return ResponseEntity.ok(Map.of("message", "Dish deleted"));
    }
    
}
