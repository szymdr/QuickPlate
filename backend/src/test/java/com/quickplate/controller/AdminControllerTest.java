package com.quickplate.controller;

import com.quickplate.repository.RestaurantRepository;
import com.quickplate.repository.ReservationRepository;
import com.quickplate.repository.UserRepository;
import com.quickplate.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {
    @Autowired MockMvc mvc;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean RestaurantRepository rr;
    @MockBean ReservationRepository rsr;
    @MockBean UserRepository ur;

    @Test void allRestaurants_ok() throws Exception {
        mvc.perform(get("/api/admin/restaurants"))
           .andExpect(status().isOk());
    }

    @Test void allReservations_ok() throws Exception {
        mvc.perform(get("/api/admin/reservations"))
           .andExpect(status().isOk());
    }

    @Test void allUsers_ok() throws Exception {
        mvc.perform(get("/api/admin/users"))
           .andExpect(status().isOk());
    }
}