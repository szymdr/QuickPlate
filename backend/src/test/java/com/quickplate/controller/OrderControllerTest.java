package com.quickplate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickplate.security.JwtAuthenticationFilter;    // add this import
import com.quickplate.model.*;
import com.quickplate.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean UserRepository userRepo;
    @MockBean ReservationRepository reservationRepo;
    @MockBean MenuItemRepository menuItemRepo;
    @MockBean OrderRepository orderRepo;

    private UUID orderId;
    private Order sample;
    private Reservation reservation;
    private MenuItem menuItem;

    @BeforeEach
    void setup() {
        orderId = UUID.randomUUID();
        reservation = new Reservation();
        reservation.setId(UUID.randomUUID());
        reservation.setReservationTime(LocalDateTime.now());
        reservation.setTableNumber(3);

        menuItem = new MenuItem();
        menuItem.setId(UUID.randomUUID());
        menuItem.setName("TestDish");
        menuItem.setPrice(new BigDecimal("10.0"));

        sample = new Order();
        sample.setId(orderId);
        sample.setStatus(OrderStatus.PENDING.toString());
        sample.setReservation(reservation);
        OrderItem orderItem = new OrderItem();
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(2);
        // initialize and add orderItem via correct property 'items'
        if (sample.getItems() == null) {
            sample.setItems(new ArrayList<>());
        }
        sample.getItems().add(orderItem);

        // stub
        given(orderRepo.findAll()).willReturn(List.of(sample));
        given(orderRepo.findById(orderId)).willReturn(Optional.of(sample));
        given(reservationRepo.findById(reservation.getId())).willReturn(Optional.of(reservation));
        given(menuItemRepo.findById(menuItem.getId())).willReturn(Optional.of(menuItem));
        given(orderRepo.existsById(orderId)).willReturn(true);
        // stub user lookup for createWithItems (principal → userRepo)
        User dummyUser = new User();
        dummyUser.setId(reservation.getId());
        given(userRepo.findById(reservation.getId()))
            .willReturn(Optional.of(dummyUser));
    }

    @Test @DisplayName("POST /api/orders/with-items → 201 + IdResp")
    void createWithItems_ok() throws Exception {
        OrderController.OrderItemReq itemReq =
            new OrderController.OrderItemReq(menuItem.getId(), 2);
        OrderController.OrderWithItemsReq req =
            new OrderController.OrderWithItemsReq(reservation.getId(), List.of(itemReq));

        given(orderRepo.save(any(Order.class)))
            .willAnswer(inv -> { Order o = inv.getArgument(0); o.setId(orderId); return o; });

        mvc.perform(post("/api/orders/with-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(req))
            .principal((Principal) () -> reservation.getId().toString()))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test @DisplayName("GET /api/orders → 200 + list")
    void getAll_ok() throws Exception {
        mvc.perform(get("/api/orders"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].status").value(OrderStatus.PENDING.toString()));
    }

    @Test @DisplayName("GET /api/orders/{id} → 200 + single")
    void getById_ok() throws Exception {
        mvc.perform(get("/api/orders/{id}", orderId))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test @DisplayName("GET /api/orders/{id} → 404 when missing")
    void getById_notFound() throws Exception {
        UUID missing = UUID.randomUUID();
        given(orderRepo.findById(missing)).willReturn(Optional.empty());

        mvc.perform(get("/api/orders/{id}", missing))
           .andExpect(status().isNotFound());
    }

    @Test @DisplayName("POST /api/orders → 201 + Location")
    void create_ok() throws Exception {
        Order in = new Order();
        given(orderRepo.save(any())).willAnswer(inv -> { Order o = inv.getArgument(0); o.setId(orderId); return o; });

        mvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(in)))
           .andExpect(status().isCreated())
           .andExpect(header().string("Location", "/api/orders/" + orderId));
    }

    @Test @DisplayName("PUT /api/orders/{id} → 200 when exists")
    void update_ok() throws Exception {
        sample.setStatus(OrderStatus.ACCEPTED);
        given(orderRepo.save(any())).willReturn(sample);

        mvc.perform(put("/api/orders/{id}", orderId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(sample)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status").value(OrderStatus.ACCEPTED));
    }

    @Test @DisplayName("DELETE /api/orders/{id} → 200")
    void delete_ok() throws Exception {
        doNothing().when(orderRepo).deleteById(orderId);

        mvc.perform(delete("/api/orders/{id}", orderId))
           .andExpect(status().isOk());
    }

    @Test @DisplayName("PATCH /api/orders/{id}/status → 200 valid")
    void updateStatus_ok() throws Exception {
        Map<String,String> body = Map.of("status", OrderStatus.COMPLETED);
        mvc.perform(patch("/api/orders/{id}/status", orderId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(body)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.status").value(OrderStatus.COMPLETED));
    }

    @Test @DisplayName("PATCH /api/orders/{id}/status → 400 invalid")
    void updateStatus_bad() throws Exception {
        Map<String,String> body = Map.of("status", "INVALID");
        mvc.perform(patch("/api/orders/{id}/status", orderId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(body)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.error").exists());
    }

    @Test @DisplayName("PUT /api/orders/{id}/next-status → 200")
    void updateNextStatus_ok() throws Exception {
        // assume next-status logic increments status
        mvc.perform(put("/api/orders/{id}/next-status", orderId))
           .andExpect(status().isOk());
    }

    @Test @DisplayName("GET /api/orders/reservation/{resId} → 200")
    void getByReservation_ok() throws Exception {
        given(orderRepo.findByReservationId(reservation.getId())).willReturn(Optional.of(sample));

        mvc.perform(get("/api/orders/reservation/{resId}", reservation.getId()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.reservation.id").value(reservation.getId().toString()));
    }

    @Test @DisplayName("GET /api/orders/statuses → 200 + list")
    void listAllStatuses_ok() throws Exception {
        mvc.perform(get("/api/orders/statuses"))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$").isArray());
    }
}