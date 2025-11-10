package com.sk.PCnWS.repository;

import com.sk.PCnWS.model.CareTask; // Import your CareTask model
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CareTaskRepository extends JpaRepository<CareTask, Long> {

    // "Find all tasks for a specific plant ID"
    // CORRECT
    List<CareTask> findByPlant_PlantId(Long plantId);

    // This is a custom query to find all tasks for a user that are overdue
    // CORRECT
    @Query("SELECT t FROM CareTask t WHERE t.plant.user.userId = ?1 AND t.dueDate < ?2 AND t.isCompleted = false")
    List<CareTask> findPendingTasksForUser(Long userId, LocalDate today);

    @Query("SELECT t FROM CareTask t WHERE t.plant.user.userId = ?1 ORDER BY t.dueDate ASC")
    List<CareTask> findAllByUserId(Long userId);
    
    // Finds all tasks due on a specific date, regardless of user
    @Query("SELECT t FROM CareTask t WHERE t.dueDate = ?1 AND t.isCompleted = false")
        List<CareTask> findAllDueOn(LocalDate date);

    @Modifying
    @Transactional
    @Query("DELETE FROM CareTask t WHERE t.plant.plantId = ?1 AND t.isCompleted = false")
    void deletePendingTasksByPlantId(Long plantId);
}