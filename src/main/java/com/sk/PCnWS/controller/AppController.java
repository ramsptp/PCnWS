package com.sk.PCnWS.controller;

import com.sk.PCnWS.model.Plant;
import com.sk.PCnWS.model.CareTask;
import com.sk.PCnWS.model.User;
import com.sk.PCnWS.repository.UserRepository;
import com.sk.PCnWS.service.PlantService;
import com.sk.PCnWS.service.CareTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile; // <-- 1. IMPORT MultipartFile

import java.security.Principal;
import java.util.List;

@Controller
public class AppController {

    @Autowired
    private PlantService plantService;
    @Autowired
    private CareTaskService careTaskService;
    @Autowired
    private UserRepository userRepository;

    private User getLoggedInUser(Principal principal) {
        String username = principal.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    // --- Dashboard ---
   // --- Dashboard ---
    @GetMapping("/")
    public String showDashboard(Model model, Principal principal) { 
        Long currentUserId = getLoggedInUser(principal).getUserId();

        // 1. Get all plants for the user (for the Quick View)
        List<Plant> plants = plantService.getPlantsForUser(currentUserId);

        // 2. Get ONLY tasks due in the past (for the alert box)
        List<CareTask> overdueTasks = careTaskService.getOverdueTasksForUser(currentUserId);

        // 3. (THE FIX) Get ONLY tasks due today or in the future
        List<CareTask> upcomingTasks = careTaskService.getUpcomingTasksForUser(currentUserId);

        // 4. Add the correct lists to the model
        model.addAttribute("plants", plants);
        model.addAttribute("overdueTasks", overdueTasks);
        model.addAttribute("upcomingTasks", upcomingTasks); // We pass this new list

        return "dashboard";
    }

    // --- Add Plant ---
    @GetMapping("/add-plant")
    public String showAddPlantForm() {
        return "add_plant";
    }

    /**
     * This now accepts "@RequestParam("imageFile") MultipartFile imageFile".
     */
    @PostMapping("/add-plant")
    public String processAddPlant(@RequestParam String plantName,
                                  @RequestParam String plantType,
                                  @RequestParam int wateringFrequency,
                                  @RequestParam int fertilizingFrequency,
                                  @RequestParam("imageFile") MultipartFile imageFile, // <-- CHANGED
                                  Principal principal) {
        
        Long currentUserId = getLoggedInUser(principal).getUserId();
        plantService.addPlant(plantName, plantType, wateringFrequency, fertilizingFrequency, currentUserId, imageFile); // <-- CHANGED

        return "redirect:/";
    }

    // --- Edit Plant ---
    @GetMapping("/edit-plant/{plantId}")
    public String showEditPlantForm(@PathVariable Long plantId, Model model) {
        Plant plant = plantService.findPlantById(plantId);
        model.addAttribute("plant", plant);
        return "edit-plant"; 
    }

    /**
     * This now accepts "@RequestParam("imageFile") MultipartFile imageFile".
     */
    @PostMapping("/edit-plant")
    public String processEditPlant(@RequestParam Long plantId,
                                   @RequestParam String plantName,
                                   @RequestParam String plantType,
                                   @RequestParam int wateringFrequency,
                                   @RequestParam int fertilizingFrequency,
                                   @RequestParam("imageFile") MultipartFile imageFile) { // <-- CHANGED
        
        plantService.updatePlant(plantId, plantName, plantType, wateringFrequency, fertilizingFrequency, imageFile); // <-- CHANGED

        return "redirect:/";
    }

    // --- Delete & Complete Methods (no changes) ---
    @GetMapping("/delete-plant/{plantId}")
    public String deletePlant(@PathVariable Long plantId) {
        plantService.deletePlant(plantId);
        return "redirect:/";
    }

    @GetMapping("/complete-task/{taskId}")
    public String completeTask(@PathVariable Long taskId) {
        careTaskService.completeTask(taskId);
        return "redirect:/";
    }
    // --- Show All Plants Page ---

    @GetMapping("/plants")
    public String showAllPlants(Model model, Principal principal) {
        // 1. Get the logged-in user
        Long currentUserId = getLoggedInUser(principal).getUserId();

        // 2. Get ALL plants for this user
        List<Plant> plants = plantService.getPlantsForUser(currentUserId);

        // 3. Add the list to the model
        model.addAttribute("plants", plants);

        // 4. Return the new 'plants.html' template we're about to create
        return "plants";
    }
}