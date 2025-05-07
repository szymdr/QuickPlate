package com.quickplate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.quickplate.model.MenuItem;
import com.quickplate.repository.MenuItemRepository;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @GetMapping
    public ResponseEntity<List<MenuItem>> getMenuItems() {
        List<MenuItem> menuItems = menuItemRepository.findAll();
        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMenuItem(@PathVariable UUID id) {
        Optional<MenuItem> opt = menuItemRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Menu item not found");
        }
        return ResponseEntity.ok(opt.get());
    }

    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItem menuItem) {
        if (menuItem.getId() == null) {
            menuItem.setId(UUID.randomUUID());
        }
        MenuItem saved = menuItemRepository.save(menuItem);
        return ResponseEntity
            .created(URI.create("/api/menu-items/" + saved.getId()))
            .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenuItem(@PathVariable UUID id, @RequestBody MenuItem menuItem) {
        if (!menuItemRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Menu item not found");
        }
        menuItem.setId(id);
        MenuItem updated = menuItemRepository.save(menuItem);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable UUID id) {
        if (!menuItemRepository.existsById(id)) {
            return ResponseEntity.status(404).build();
        }
        menuItemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
