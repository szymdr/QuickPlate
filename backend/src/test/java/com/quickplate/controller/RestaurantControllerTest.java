package com.quickplate.controller;

import com.quickplate.model.Restaurant;
import com.quickplate.repository.RestaurantRepository;
import com.quickplate.repository.MenuItemRepository;
import com.quickplate.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RestaurantController.class)
@AutoConfigureMockMvc(addFilters = false)
class RestaurantControllerTest {
    @Autowired MockMvc mvc;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean RestaurantRepository repo;
    @MockBean MenuItemRepository menuItemRepository;

    @Test void getRestaurants_ok() throws Exception {
        Restaurant r = new Restaurant();
        r.setId(UUID.randomUUID());
        r.setName("X");
        given(repo.findAll()).willReturn(List.of(r));

        mvc.perform(get("/api/restaurants"))
           .andExpect(status().isOk())
           .andExpect(content().contentType("application/json"))
           .andExpect(jsonPath("$[0].name").value("X"));
    }
}