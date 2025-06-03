package com.quickplate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickplate.model.User;
import com.quickplate.repository.UserRepository;
import com.quickplate.security.JwtAuthenticationFilter;    // <<– add this
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserRepository repo;

    @Autowired
    private ObjectMapper json;

    private User sample;

    @BeforeEach
    void setup() {
        sample = new User();
        sample.setId(UUID.randomUUID());
        sample.setFirstName("Jan");
        sample.setLastName("Kowalski");
        sample.setEmail("jan@wp.pl");
        sample.setPasswordHash("hashed");
        sample.setPhone("123456789");
        sample.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /api/users → 200 + list of users")
    void listUsers_ok() throws Exception {
        when(repo.findAll()).thenReturn(List.of(sample));

        mvc.perform(get("/api/users"))
           .andExpect(status().isOk())
           .andExpect(content().contentType(MediaType.APPLICATION_JSON))
           .andExpect(jsonPath("$[0].email").value("jan@wp.pl"));

        verify(repo).findAll();
    }

    @Test
    @DisplayName("GET /api/users/{id} → 200 + single user")
    void getUserById_ok() throws Exception {
        when(repo.findById(sample.getId())).thenReturn(Optional.of(sample));

        mvc.perform(get("/api/users/{id}", sample.getId()))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.firstName").value("Jan"));

        verify(repo).findById(sample.getId());
    }

    @Test
    @DisplayName("GET /api/users/{id} → 404 when not found")
    void getUserById_notFound() throws Exception {
        when(repo.findById(any())).thenReturn(Optional.empty());

        mvc.perform(get("/api/users/{id}", UUID.randomUUID()))
           .andExpect(status().isNotFound());

        verify(repo).findById(any());
    }

    @Test
    @DisplayName("POST /api/users → 201 + Location header")
    void createUser_created() throws Exception {
        UUID generatedId = sample.getId();

        User toCreate = new User();
        toCreate.setFirstName(sample.getFirstName());
        toCreate.setLastName(sample.getLastName());
        toCreate.setEmail(sample.getEmail());
        toCreate.setPasswordHash(sample.getPasswordHash());
        toCreate.setPhone(sample.getPhone());

        when(repo.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(generatedId);
            return u;
        });

        mvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(toCreate)))
           .andExpect(status().isCreated())
           .andExpect(header().string("Location", "/api/users/" + generatedId))
           .andExpect(jsonPath("$.id").value(generatedId.toString()));

        verify(repo).save(any());
    }

    @Test
    @DisplayName("PUT /api/users/{id} → 200 when exists")
    void updateUser_ok() throws Exception {
        when(repo.existsById(sample.getId())).thenReturn(true);
        when(repo.save(any(User.class))).thenReturn(sample);

        sample.setFirstName("Anna");

        mvc.perform(put("/api/users/{id}", sample.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(sample)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.firstName").value("Anna"));

        verify(repo).existsById(sample.getId());
        verify(repo).save(any());
    }

    @Test
    @DisplayName("PUT /api/users/{id} → 404 when not exists")
    void updateUser_notFound() throws Exception {
        when(repo.existsById(any())).thenReturn(false);

        mvc.perform(put("/api/users/{id}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(sample)))
           .andExpect(status().isNotFound());

        verify(repo).existsById(any());
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} → 200 when exists")
    void deleteUser_ok() throws Exception {
        when(repo.existsById(sample.getId())).thenReturn(true);
        doNothing().when(repo).deleteById(sample.getId());

        mvc.perform(delete("/api/users/{id}", sample.getId()))
           .andExpect(status().isOk());

        verify(repo).deleteById(sample.getId());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} → 404 when not exists")
    void deleteUser_notFound() throws Exception {
        when(repo.existsById(any())).thenReturn(false);

        mvc.perform(delete("/api/users/{id}", UUID.randomUUID()))
           .andExpect(status().isNotFound());

        verify(repo, never()).deleteById(any());
    }
}
