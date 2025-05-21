package com.quickplate.repository;

import com.quickplate.model.RestaurantDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RestaurantDetailsRepository extends JpaRepository<RestaurantDetails, UUID> {
}