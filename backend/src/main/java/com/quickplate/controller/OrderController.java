package com.quickplate.controller;

import com.quickplate.model.MenuItem;
import com.quickplate.model.Order;
import com.quickplate.model.OrderItem;
import com.quickplate.model.OrderStatus;
import com.quickplate.model.Reservation;
import com.quickplate.model.User;
import com.quickplate.repository.MenuItemRepository;
import com.quickplate.repository.OrderRepository;
import com.quickplate.repository.ReservationRepository;
import com.quickplate.repository.UserRepository;

import com.quickplate.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private UserRepository userRepo;
    @Autowired private ReservationRepository reservationRepo;
    @Autowired private MenuItemRepository menuItemRepo;
    @Autowired private OrderRepository orderRepo;

    // DTOs for the request
    record OrderItemReq(UUID menuItemId, int quantity) {}
    record OrderWithItemsReq(UUID reservationId, List<OrderItemReq> items) {}
    record IdResp(UUID id) {}

    @PostMapping("/with-items")
    public ResponseEntity<IdResp> createWithItems(
        @RequestBody OrderWithItemsReq req,
        Principal principal
    ) {
        UUID userId = UUID.fromString(principal.getName());
        User user = userRepo.findById(userId)
          .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Reservation reservation = reservationRepo.findById(req.reservationId())
          .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setUser(user);
        order.setRestaurant(reservation.getRestaurant());
        order.setReservation(reservation);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> itemEntities = new ArrayList<>();
        for (OrderItemReq i : req.items()) {
            MenuItem mi = menuItemRepo.findById(i.menuItemId())
              .orElseThrow(() -> new ResourceNotFoundException("MenuItem not found"));
            BigDecimal line = mi.getPrice().multiply(BigDecimal.valueOf(i.quantity()));
            total = total.add(line);

            OrderItem oi = new OrderItem();
            oi.setId(UUID.randomUUID());
            oi.setOrder(order);
            oi.setMenuItem(mi);
            oi.setQuantity(i.quantity());
            oi.setPrice(mi.getPrice());
            itemEntities.add(oi);
        }
        order.setTotalPrice(total);
        order.setItems(itemEntities);

        Order saved = orderRepo.save(order);
        return ResponseEntity
          .created(URI.create("/api/orders/" + saved.getId()))
          .body(new IdResp(saved.getId()));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAll() {
        List<Order> orders = new ArrayList<>();
        orderRepo.findAll().forEach(orders::add);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Optional<Order> opt = orderRepo.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Order not found");
        }
        return ResponseEntity.ok(opt.get());
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order o) {
        if (o.getId() == null) o.setId(UUID.randomUUID());
        Order saved = orderRepo.save(o);
        return ResponseEntity
            .created(URI.create("/api/orders/" + saved.getId()))
            .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Order o) {
        if (!orderRepo.existsById(id)) {
            return ResponseEntity.status(404).body("Order not found");
        }
        o.setId(id);
        return ResponseEntity.ok(orderRepo.save(o));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        if (!orderRepo.existsById(id)) {
            return ResponseEntity.status(404).body("Order not found");
        }
        orderRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // replace the Map<String,String> binding:
    record StatusReq(String status) {}

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
        @PathVariable UUID id,
        @RequestBody StatusReq body
    ) {
        String norm = body.status().toLowerCase();
        if (!OrderStatus.isValidStatus(norm)) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("error","Nieprawidłowy status zamówienia","status","400"));
        }

        Order order = orderRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(norm);
        orderRepo.save(order);

        return ResponseEntity.ok(Map.of("id", order.getId().toString(), "status", norm));
    }

    @PutMapping("/{id}/next-status")
    public ResponseEntity<?> updateNextStatus(@PathVariable UUID id) {
        Order order = orderRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        String nextStatus = OrderStatus.getNextStatus(order.getStatus());
        if (nextStatus == null) {
            return ResponseEntity
                .badRequest()
                .body(Map.of("error", "Cannot advance status from " + order.getStatus()));
        }

        order.setStatus(nextStatus);
        orderRepo.save(order);

        return ResponseEntity.ok(Map.of("id", order.getId().toString(), "status", nextStatus));
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<Order> getByReservation(
        @PathVariable UUID reservationId
    ) {
        Order order = orderRepo.findByReservationId(reservationId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Order not found for reservation " + reservationId));
        return ResponseEntity.ok(order);
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<String>> listAllStatuses() {
        return ResponseEntity.ok(List.of(
            OrderStatus.PENDING,
            OrderStatus.PAID,
            OrderStatus.ACCEPTED,
            OrderStatus.PREPARING,
            OrderStatus.READY,
            OrderStatus.COMPLETED,
            OrderStatus.CANCELLED
        ));
    }
}