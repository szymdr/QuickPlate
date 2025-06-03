package com.quickplate.controller;

import com.quickplate.model.OrderItem;
import com.quickplate.repository.OrderItemRepository;
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

@WebMvcTest(OrderItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderItemControllerTest {
    @Autowired MockMvc mvc;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean OrderItemRepository repo;

    @Test void getAllOrderItems_ok() throws Exception {
        OrderItem oi = new OrderItem();
        oi.setId(UUID.randomUUID());
        oi.setQuantity(2);
        given(repo.findAll()).willReturn(List.of(oi));

        mvc.perform(get("/api/order-items"))
           .andExpect(status().isOk())
           .andExpect(content().contentType("application/json"))
           .andExpect(jsonPath("$[0].quantity").value(2));
    }
}