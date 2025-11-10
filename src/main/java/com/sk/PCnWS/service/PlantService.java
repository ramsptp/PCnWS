package com.sk.PCnWS.service;

import com.sk.PCnWS.model.Plant;
import com.sk.PCnWS.model.User;
import com.sk.PCnWS.repository.PlantRepository;
import com.sk.PCnWS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PlantService {

    @Autowired
    private PlantRepository plantRepository;

    @Autowired
    private UserRepository userRepository; // To find the user

    @Autowired
    private CareTaskService careTaskService; // To create initial tasks

    // CORRECT
    public List<Plant> getPlantsForUser(Long userId) {
        return plantRepository.findByUser_UserId(userId); 
}

    /**
     * This method adds a plant AND creates its first tasks.
     * This is the "Plant Management Module" and "Care Scheduler Module" working together.
     */
    public Plant addPlant(String plantName, String plantType, int wateringFreq, int fertilizingFreq, Long userId) {
        
        // 1. Find the user who owns this plant
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Create and save the new plant
        Plant newPlant = new Plant();
        newPlant.setPlantName(plantName);
        newPlant.setPlantType(plantType);
        newPlant.setWateringFrequencyDays(wateringFreq);
        newPlant.setFertilizingFrequencyDays(fertilizingFreq);
        newPlant.setUser(user);
        // We'll handle imageURL later
        
        Plant savedPlant = plantRepository.save(newPlant);

        // 3. Call the CareTaskService to create the initial tasks
        careTaskService.createInitialTasks(savedPlant);

        return savedPlant;
    }


    public void deletePlant(Long plantId) {
        plantRepository.deleteById(plantId);
    }


    public Plant findPlantById(Long plantId) {
        return plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found: " + plantId));
    }

    
      //Updates an existing plant's details and resets its task schedule.
     
    @Transactional // Add this annotation
    public void updatePlant(Long plantId, String plantName, String plantType, int wateringFreq, int fertilizingFreq) {
        
        // 1. Find the plant we need to update
        Plant plant = findPlantById(plantId);

        // 2. Update its details
        plant.setPlantName(plantName);
        plant.setPlantType(plantType);
        plant.setWateringFrequencyDays(wateringFreq);
        plant.setFertilizingFrequencyDays(fertilizingFreq);

        // 3. Save the updated plant
        plantRepository.save(plant);

        // 4. ADD THIS NEW LINE:
        // This will delete all old tasks and create new ones with the new frequencies.
        careTaskService.resetSchedulesForPlant(plant);
    }
}