package com.sk.PCnWS.controller;

import com.sk.PCnWS.model.Plant;
import com.sk.PCnWS.model.CareTask;
import com.sk.PCnWS.model.User;
import com.sk.PCnWS.repository.UserRepository;
import com.sk.PCnWS.service.CareTaskService;
import com.sk.PCnWS.service.PlantService;
import com.sk.PCnWS.service.TipService; // We'll keep the TipService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @Autowired
    private TipService tipService; // Keep the TipService

    // Helper method to get the logged-in user
    private User getLoggedInUser(Principal principal) {
        String username = principal.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    // --- Dashboard ---
    @GetMapping("/")
    public String showDashboard(Model model, Principal principal) {
        Long currentUserId = getLoggedInUser(principal).getUserId();
        List<Plant> plants = plantService.getPlantsForUser(currentUserId);
        List<CareTask> overdueTasks = careTaskService.getOverdueTasksForUser(currentUserId);
        List<CareTask> upcomingTasks = careTaskService.getUpcomingTasksForUser(currentUserId);
        String dailyTip = tipService.getDailyTip(); // Get the random tip

        model.addAttribute("plants", plants);
        model.addAttribute("overdueTasks", overdueTasks);
        model.addAttribute("upcomingTasks", upcomingTasks);
        model.addAttribute("dailyTip", dailyTip); // Add the tip

        return "dashboard";
    }

    // --- Show All Plants Page ---
    @GetMapping("/plants")
    public String showAllPlants(Model model, Principal principal) {
        Long currentUserId = getLoggedInUser(principal).getUserId();
        List<Plant> plants = plantService.getPlantsForUser(currentUserId);
        model.addAttribute("plants", plants);
        return "plants";
    }

    // --- Add Plant ---
    @GetMapping("/add-plant")
    public String showAddPlantForm() {
        return "add_plant";
    }

    @PostMapping("/add-plant")
    public String processAddPlant(@RequestParam String plantName,
                                  @RequestParam String plantType,
                                  @RequestParam int wateringFrequency,
                                  @RequestParam int fertilizingFrequency,
                                  @RequestParam("imageFile") MultipartFile imageFile,
                                  Principal principal) {
        Long currentUserId = getLoggedInUser(principal).getUserId();
        plantService.addPlant(plantName, plantType, wateringFrequency, fertilizingFrequency, currentUserId, imageFile);
        return "redirect:/plants"; // Redirect to the "All Plants" page
    }

    // --- Edit Plant ---
    @GetMapping("/edit-plant/{plantId}")
    public String showEditPlantForm(@PathVariable Long plantId, Model model) {
        Plant plant = plantService.findPlantById(plantId);
        model.addAttribute("plant", plant);
        return "edit-plant";
    }

    @PostMapping("/edit-plant")
    public String processEditPlant(@RequestParam Long plantId,
                                   @RequestParam String plantName,
                                   @RequestParam String plantType,
                                   @RequestParam int wateringFrequency,
                                   @RequestParam int fertilizingFrequency,
                                   @RequestParam("imageFile") MultipartFile imageFile) {
        plantService.updatePlant(plantId, plantName, plantType, wateringFrequency, fertilizingFrequency, imageFile);
        return "redirect:/plants"; // Redirect to the "All Plants" page
    }

    // --- Delete & Complete Methods ---
    @GetMapping("/delete-plant/{plantId}")
    public String deletePlant(@PathVariable Long plantId) {
        plantService.deletePlant(plantId);
        return "redirect:/plants"; // Redirect to the "All Plants" page
    }

    @GetMapping("/complete-task/{taskId}")
public String completeTask(@PathVariable Long taskId, RedirectAttributes redirectAttributes) {

    // 1. Complete the task and get the object back
    CareTask completedTask = careTaskService.completeTask(taskId);

    // 2. Build the success message
    if (completedTask != null) {
        String plantName = completedTask.getPlant().getPlantName();
        String taskType = completedTask.getTaskType().toLowerCase() + "ed"; // "WATER" -> "watered"

        // 3. Add the message as a Flash Attribute (survives the redirect)
        redirectAttributes.addFlashAttribute("toastMessage", 
            plantName + " has been " + taskType + "!");
    }

    // 4. Redirect back to the dashboard
    return "redirect:/";
}

    // --- Profile Page ---

/**
 * This method SHOWS the user's profile page.
 * It maps to the URL: http://localhost:8080/profile
 */
    @GetMapping("/profile")
    public String showProfilePage(Model model, Principal principal) {
        // 1. Get the currently logged-in user object
        User user = getLoggedInUser(principal);

        // 2. Add the user object to the model so the HTML can access it
        model.addAttribute("user", user);

        // 3. Return the new 'profile.html' template we'll create next
        return "profile";
    }
}