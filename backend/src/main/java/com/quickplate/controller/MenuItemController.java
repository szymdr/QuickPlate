package com.quickplate.controller;

import com.quickplate.model.MenuItem;
import com.quickplate.repository.MenuItemRepository;
import com.quickplate.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins="http://localhost:5173")
@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {
    @Autowired private MenuItemRepository menuItemRepo;

    @GetMapping("/restaurant/{rid}")
    public ResponseEntity<List<MenuItem>> listByRestaurant(@PathVariable UUID rid) {
        return ResponseEntity.ok(menuItemRepo.findByRestaurantId(rid));
    }

    @PostMapping
    public ResponseEntity<MenuItem> create(@RequestBody MenuItem mi) {
        if (mi.getId()==null) mi.setId(UUID.randomUUID());
        MenuItem saved = menuItemRepo.save(mi);
        return ResponseEntity.created(URI.create("/api/menu-items/"+saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> update(@PathVariable UUID id, @RequestBody MenuItem mi) {
        if (!menuItemRepo.existsById(id))
            throw new ResourceNotFoundException("MenuItem not found");
        mi.setId(id);
        return ResponseEntity.ok(menuItemRepo.save(mi));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!menuItemRepo.existsById(id))
            throw new ResourceNotFoundException("MenuItem not found");
        menuItemRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getById(@PathVariable UUID id) {
        MenuItem mi = menuItemRepo.findById(id)
          .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
        return ResponseEntity.ok(mi);
    }
}
