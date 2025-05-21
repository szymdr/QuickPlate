package com.quickplate.repository;

import com.quickplate.model.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FoodCategoryRepository extends JpaRepository<FoodCategory, UUID> {
}