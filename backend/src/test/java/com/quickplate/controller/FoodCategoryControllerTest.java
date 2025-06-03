package com.quickplate.controller;

import com.quickplate.model.FoodCategory;
import com.quickplate.repository.FoodCategoryRepository;
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

@WebMvcTest(FoodCategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class FoodCategoryControllerTest {
    @Autowired MockMvc mvc;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean FoodCategoryRepository repo;

    @Test void getAllCategories_ok() throws Exception {
        FoodCategory c = new FoodCategory();
        c.setId(UUID.randomUUID());
        c.setName("A");
        given(repo.findAll()).willReturn(List.of(c));

        mvc.perform(get("/api/food-categories"))
           .andExpect(status().isOk())
           .andExpect(content().contentType("application/json"))
           .andExpect(jsonPath("$[0].name").value("A"));
    }
}