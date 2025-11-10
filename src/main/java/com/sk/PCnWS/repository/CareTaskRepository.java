package com.sk.PCnWS.repository;

import com.sk.PCnWS.model.CareTask; // Import your CareTask model
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CareTaskRepository extends JpaRepository<CareTask, Long> {

    // "Find all tasks for a specific plant ID"
    List<CareTask> findByPlantId(Long plantId);

    // This is a custom query to find all tasks for a user that are overdue
    @Query("SELECT t FROM CareTask t WHERE t.plant.user.id = ?1 AND t.dueDate < ?2 AND t.isCompleted = false")
    List<CareTask> findPendingTasksForUser(Long userId, LocalDate today);
    
    // Finds all tasks for a specific user, ordered by due date
    @Query("SELECT t FROM CareTask t WHERE t.plant.user.id = ?1 ORDER BY t.dueDate ASC")
    List<CareTask> findAllByUserId(Long userId);
}