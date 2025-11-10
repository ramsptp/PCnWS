package com.sk.PCnWS.controller;

import com.sk.PCnWS.model.Plant;
import com.sk.PCnWS.model.CareTask;
import com.sk.PCnWS.model.User; // <-- 1. IMPORT USER MODEL
import com.sk.PCnWS.repository.UserRepository; // <-- 2. IMPORT USER REPOSITORY
import com.sk.PCnWS.service.PlantService;
import com.sk.PCnWS.service.CareTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal; // <-- 3. IMPORT PRINCIPAL (THE LOGGED-IN USER)
import java.util.List;

@Controller
public class AppController {

    @Autowired
    private PlantService plantService;

    @Autowired
    private CareTaskService careTaskService;

    @Autowired
    private UserRepository userRepository; // <-- 4. INJECT USER REPOSITORY

    /**
     * Helper method to get the User object of the currently logged-in user
     */
    private User getLoggedInUser(Principal principal) {
        String username = principal.getName(); // Get username from Spring Security
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    // --- Dashboard (Your Main Page) ---

    @GetMapping("/")
    public String showDashboard(Model model, Principal principal) { // <-- 5. ADD PRINCIPAL
        
        // 6. Get the real logged-in user's ID
        Long currentUserId = getLoggedInUser(principal).getUserId();

        // All the code below is the same, but now uses the REAL ID
        List<Plant> plants = plantService.getPlantsForUser(currentUserId);
        List<CareTask> tasks = careTaskService.getTasksForUser(currentUserId);
        List<CareTask> overdueTasks = careTaskService.getOverdueTasksForUser(currentUserId);

        model.addAttribute("plants", plants);
        model.addAttribute("tasks", tasks);
        model.addAttribute("overdueTasks", overdueTasks);

        return "dashboard";
    }

    // --- Add Plant ---

    @GetMapping("/add-plant")
    public String showAddPlantForm() {
        return "add_plant"; // Using your "add_plant.html" file
    }

    @PostMapping("/add-plant")
    public String processAddPlant(@RequestParam String plantName,
                                  @RequestParam String plantType,
                                  @RequestParam int wateringFrequency,
                                  @RequestParam int fertilizingFrequency,
                                  Principal principal) { // <-- 7. ADD PRINCIPAL
        
        // 8. Get the real logged-in user's ID
        Long currentUserId = getLoggedInUser(principal).getUserId();

        plantService.addPlant(plantName, plantType, wateringFrequency, fertilizingFrequency, currentUserId);

        return "redirect:/";
    }

    // --- Complete Task ---

    @GetMapping("/complete-task/{taskId}")
    public String completeTask(@PathVariable Long taskId) {
        careTaskService.completeTask(taskId);
        return "redirect:/";
    }

     // --- Delete Plant ---


    @GetMapping("/delete-plant/{plantId}")
    public String deletePlant(@PathVariable Long plantId) {

        // Use the service to delete the plant
        plantService.deletePlant(plantId);

        // Send the user back to the dashboard
        return "redirect:/";
    }
}