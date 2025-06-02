package com.quickplate.repository;

import com.quickplate.model.Reservation;
import com.quickplate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findByUser(User user);
}