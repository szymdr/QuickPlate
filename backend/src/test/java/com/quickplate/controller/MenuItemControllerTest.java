package com.quickplate.controller;

import com.quickplate.model.MenuItem;
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

@WebMvcTest(MenuItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class MenuItemControllerTest {
    @Autowired MockMvc mvc;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean MenuItemRepository repo;

    @Test void listByRestaurant_ok() throws Exception {
        UUID rid = UUID.randomUUID();
        MenuItem m = new MenuItem();
        m.setId(UUID.randomUUID());
        m.setName("Dish");
        given(repo.findByRestaurantId(rid)).willReturn(List.of(m));

        mvc.perform(get("/api/menu-items/restaurant/{rid}", rid))
           .andExpect(status().isOk())
           .andExpect(content().contentType("application/json"))
           .andExpect(jsonPath("$[0].name").value("Dish"));
    }
}