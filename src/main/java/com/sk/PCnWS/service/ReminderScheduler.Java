package com.sk.PCnWS.service;

import com.sk.PCnWS.model.CareTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Component
public class ReminderScheduler {

    @Autowired
    private CareTaskService careTaskService;

    private List<CareTask> tasksDueToday = Collections.emptyList();

    public List<CareTask> getTasksDueToday() {
        return tasksDueToday;
    }

    /**
     * Runs every day at 7:30 AM (local time) to fetch due tasks.
     * The cron expression: Seconds Minutes Hour Day-of-Month Month Day-of-Week
     */
    @Scheduled(cron = "0 30 7 * * *")
    public void updateDueTasksList() {
        // Fetch the tasks for today
        tasksDueToday = careTaskService.getTasksDueToday();

        System.out.println("\n--- Scheduler Run: Fetched " + tasksDueToday.size() + " tasks due today. ---\n");
    }
}