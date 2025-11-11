package com.sk.PCnWS.service;

import com.sk.PCnWS.model.CareTask;
import com.sk.PCnWS.model.Plant;
import com.sk.PCnWS.repository.CareTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CareTaskService {

    @Autowired
    private CareTaskRepository careTaskRepository;

    /**
     * This is the core scheduler logic.
     * When a task is completed, it creates the next one.
     */
    public void completeTask(Long taskId) {
        Optional<CareTask> optionalTask = careTaskRepository.findById(taskId);
        if (!optionalTask.isPresent()) {
            // Handle error - task not found
            return;
        }

        CareTask completedTask = optionalTask.get();
        completedTask.setIsCompleted(true);
        careTaskRepository.save(completedTask); // Save the "completed" status

        // Now, create the NEXT task
        CareTask nextTask = new CareTask();
        nextTask.setPlant(completedTask.getPlant());
        nextTask.setTaskType(completedTask.getTaskType());
        nextTask.setIsCompleted(false);

        // Calculate the next due date based on the plant's frequency
        int frequency = 0;
        if ("WATER".equals(completedTask.getTaskType())) {
            frequency = completedTask.getPlant().getWateringFrequencyDays();
        } else if ("FERTILIZE".equals(completedTask.getTaskType())) {
            frequency = completedTask.getPlant().getFertilizingFrequencyDays();
        }

        nextTask.setDueDate(LocalDate.now().plusDays(frequency));
        careTaskRepository.save(nextTask);
    }

    /**
     * Creates the very first tasks for a new plant.
     */
    public void createInitialTasks(Plant newPlant) {
        // Create initial watering task
        CareTask waterTask = new CareTask();
        waterTask.setPlant(newPlant);
        waterTask.setTaskType("WATER");
        waterTask.setDueDate(LocalDate.now().plusDays(newPlant.getWateringFrequencyDays()));
        waterTask.setIsCompleted(false);
        careTaskRepository.save(waterTask);

        // Create initial fertilizing task
        CareTask fertilizeTask = new CareTask();
        fertilizeTask.setPlant(newPlant);
        fertilizeTask.setTaskType("FERTILIZE");
        fertilizeTask.setDueDate(LocalDate.now().plusDays(newPlant.getFertilizingFrequencyDays()));
        fertilizeTask.setIsCompleted(false);
        careTaskRepository.save(fertilizeTask);
    }

    /**
     * Finds all tasks for a specific user (for the dashboard).
     */
    public List<CareTask> getTasksForUser(Long userId) {
        return careTaskRepository.findAllByUserId(userId);
    }
    
    /**
     * Finds only the overdue tasks (for the "Missed Alerts" module).
     */
    public List<CareTask> getOverdueTasksForUser(Long userId) {
        return careTaskRepository.findPendingTasksForUser(userId, LocalDate.now());
    }
    @Transactional
    public void resetSchedulesForPlant(Plant plant) {
        // 1. Delete all future (pending) tasks for this plant
        careTaskRepository.deletePendingTasksByPlantId(plant.getPlantId());

        // 2. Create new fresh tasks based on the new frequencies
        createInitialTasks(plant);
    }

    /**
     * Finds all tasks due today across all users.
     */
    public List<CareTask> getTasksDueToday() {
        return careTaskRepository.findAllDueOn(LocalDate.now());
}

// ... (inside the class, after your other methods)
    
    /**
     * Finds all pending tasks due today or in the future for a specific user.
     */
    public List<CareTask> getUpcomingTasksForUser(Long userId) {
        return careTaskRepository.findUpcomingTasksForUser(userId, LocalDate.now());
    }
}