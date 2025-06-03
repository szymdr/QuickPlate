package com.quickplate.controller;

import com.quickplate.model.AccountType;
import com.quickplate.repository.AccountTypeRepository;
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

@WebMvcTest(AccountTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountTypeControllerTest {
    @Autowired MockMvc mvc;
    @MockBean JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockBean AccountTypeRepository repo;

    @Test void getAllAccountTypes_ok() throws Exception {
        AccountType at = new AccountType();
        at.setId(UUID.randomUUID());
        at.setName("USER");
        given(repo.findAll()).willReturn(List.of(at));

        mvc.perform(get("/api/account-types"))
           .andExpect(status().isOk())
           .andExpect(content().contentType("application/json"))
           .andExpect(jsonPath("$[0].name").value("USER"));
    }
}