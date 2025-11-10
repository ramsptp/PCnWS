package com.sk.PCnWS.service;

import com.sk.PCnWS.model.Plant;
import com.sk.PCnWS.model.User;
import com.sk.PCnWS.repository.PlantRepository;
import com.sk.PCnWS.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile; // <-- IMPORT THIS

import java.io.IOException; // <-- IMPORT THIS
import java.util.List;

@Service
public class PlantService {

    @Autowired
    private PlantRepository plantRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CareTaskService careTaskService;
    @Autowired
    private FileStorageService fileStorageService; // <-- INJECT OUR NEW SERVICE

    public List<Plant> getPlantsForUser(Long userId) {
        return plantRepository.findByUser_UserId(userId);
    }

    @Transactional
    public Plant addPlant(String plantName, String plantType, int wateringFreq, int fertilizingFreq, Long userId, MultipartFile imageFile) {
        
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Plant newPlant = new Plant();
        newPlant.setPlantName(plantName);
        newPlant.setPlantType(plantType);
        newPlant.setWateringFrequencyDays(wateringFreq);
        newPlant.setFertilizingFrequencyDays(fertilizingFreq);
        newPlant.setUser(user);

        // NEW FILE UPLOAD LOGIC
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // This calls our new API-based uploader
                String imageUrl = fileStorageService.uploadFile(imageFile);
                newPlant.setImageUrl(imageUrl); // Save the URL to the plant
            } catch (IOException | InterruptedException e) {
                // Handle the error
                System.err.println("Error uploading file: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        Plant savedPlant = plantRepository.save(newPlant);
        careTaskService.createInitialTasks(savedPlant);
        return savedPlant;
    }

    public Plant findPlantById(Long plantId) {
        return plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found: " + plantId));
    }

    @Transactional
    public void updatePlant(Long plantId, String plantName, String plantType, int wateringFreq, int fertilizingFreq, MultipartFile imageFile) {
        
        Plant plant = findPlantById(plantId);
        plant.setPlantName(plantName);
        plant.setPlantType(plantType);
        plant.setWateringFrequencyDays(wateringFreq);
        plant.setFertilizingFrequencyDays(fertilizingFreq);

        // NEW FILE UPLOAD LOGIC (for update)
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = fileStorageService.uploadFile(imageFile);
                plant.setImageUrl(imageUrl);
            } catch (IOException | InterruptedException e) {
                System.err.println("Error uploading file: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        plantRepository.save(plant);
        careTaskService.resetSchedulesForPlant(plant);
    }

    public void deletePlant(Long plantId) {
        plantRepository.deleteById(plantId);
    }
}