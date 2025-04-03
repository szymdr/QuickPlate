package com.quickplate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {
    
    @Autowired
    private MenuItemRepository menuItemRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMenuItems() {
        List<MenuItem> menuItems = menuItemRepository.findAll());
        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItem(@PathVariable UUID id) {
        Optional<MenuItem> menuItemOpt = menuItemRepository.findById(id);
        if (menuItemOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Menu item not found");
        }
        return ResponseEntity.ok(menuItemOpt.get());
    }

    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@RequestBody MenuItem menuItem) {
        if (menuItem.getId( == null)) {
            menuItem.setId(UUID.randomUUID());
        }
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return ResponseEntity.created(URI.create("/api/menu-items/" + savedMenuItem.getId())).body(savedMenuItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable UUID id, @RequestBody MenuItem menuItem) {
        if (!menuItemRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Menu item not found");
        }
        menuItem.setId(id);
        MenuItem updatedMenuItem = menuItemRepository.save(menuItem);
        return ResponseEntity.ok(updatedMenuItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable UUID id) {
        if (!menuItemRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Menu item not found");
        }
        menuItemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
}
