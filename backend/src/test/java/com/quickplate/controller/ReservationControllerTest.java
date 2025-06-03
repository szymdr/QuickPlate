package com.quickplate.controller;

import com.quickplate.model.Reservation;
import com.quickplate.repository.ReservationRepository;
import com.quickplate.repository.UserRepository;
import com.quickplate.repository.RestaurantRepository;
import com.quickplate.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReservationControllerTest {
    @Autowired MockMvc mvc;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean ReservationRepository repo;
    @MockBean UserRepository userRepository;
    @MockBean RestaurantRepository restaurantRepository;

    @Test void getAllReservations_ok() throws Exception {
        Reservation r = new Reservation();
        r.setId(UUID.randomUUID());
        r.setTableNumber(5);
        r.setReservationTime(LocalDateTime.now());
        given(repo.findAll()).willReturn(List.of(r));

        mvc.perform(get("/api/reservations"))
           .andExpect(status().isOk())
           .andExpect(content().contentType("application/json"))
           .andExpect(jsonPath("$[0].tableNumber").value(5));
    }
}