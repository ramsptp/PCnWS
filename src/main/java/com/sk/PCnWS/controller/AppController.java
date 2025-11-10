package com.sk.PCnWS.controller;

import com.sk.PCnWS.model.Plant;
import com.sk.PCnWS.model.CareTask;
import com.sk.PCnWS.service.PlantService;
import com.sk.PCnWS.service.CareTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AppController {

    @Autowired
    private PlantService plantService;

    @Autowired
    private CareTaskService careTaskService;

    // --- Dashboard (Your Main Page) ---

    /**
     * This is the home page, showing the user's dashboard.
     * It maps to: http://localhost:8080/
     */
    @GetMapping("/")
    public String showDashboard(Model model) {
        // For this project, we'll hardcode the user ID to '1'.
        // In a real app, you'd get this from the logged-in session.
        Long currentUserId = 1L;

        // 1. Get all plants for this user
        List<Plant> plants = plantService.getPlantsForUser(currentUserId);

        // 2. Get all upcoming tasks for this user
        List<CareTask> tasks = careTaskService.getTasksForUser(currentUserId);
        
        // 3. Get all overdue tasks for this user (for the alert module)
        List<CareTask> overdueTasks = careTaskService.getOverdueTasksForUser(currentUserId);

        // 4. Add all this data to the "model" so the HTML page can use it
        model.addAttribute("plants", plants);
        model.addAttribute("tasks", tasks);
        model.addAttribute("overdueTasks", overdueTasks);

        // This will return the file "dashboard.html"
        return "dashboard";
    }

    // --- Add Plant ---

    /**
     * This shows the "Add New Plant" form.
     * It maps to: http://localhost:8080/add-plant
     */
    @GetMapping("/add-plant")
    public String showAddPlantForm() {
        return "add-plant"; // Returns add-plant.html
    }

    /**
     * This handles the form submission from the "Add New Plant" page.
     * It maps to a POST request to: http://localhost:8080/add-plant
     */
    @PostMapping("/add-plant")
    public String processAddPlant(@RequestParam String plantName,
                                  @RequestParam String plantType,
                                  @RequestParam int wateringFrequency,
                                  @RequestParam int fertilizingFrequency) {
        
        // We're still using our hardcoded user ID '1'
        Long currentUserId = 1L;

        // Use the service to add the plant (which also creates the first tasks)
        plantService.addPlant(plantName, plantType, wateringFrequency, fertilizingFrequency, currentUserId);

        // Send the user back to the dashboard
        return "redirect:/";
    }

    // --- Complete Task ---

    /**
     * This handles clicking a "Complete" button on a task.
     * It maps to a URL like: http://localhost:8080/complete-task/5 (where 5 is the task ID)
     */
    @GetMapping("/complete-task/{taskId}")
    public String completeTask(@PathVariable Long taskId) {
        
        // Use the service to complete the task (which also schedules the next one)
        careTaskService.completeTask(taskId);

        // Send the user back to the dashboard
        return "redirect:/";
    }
}