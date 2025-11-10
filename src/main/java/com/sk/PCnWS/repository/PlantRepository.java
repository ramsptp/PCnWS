package com.sk.PCnWS.repository;

import com.sk.PCnWS.model.Plant; // Import your Plant model
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlantRepository extends JpaRepository<Plant, Long> {

    // Automatically creates a query: "Find all Plants for a specific user ID"

    List<Plant> findByUser_UserId(Long userId);
}