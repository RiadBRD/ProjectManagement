package com.project.services;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.project.entity.Task;
import com.project.enums.TaskStatus;

public class ReminderService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final List<Task> tasks;


    public ReminderService(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void start(){
        scheduler.scheduleAtFixedRate(()-> checkOverdueTasks(),0,1,TimeUnit.MILLISECONDS);
    }

    private void checkOverdueTasks() {
        tasks.stream()
             .filter(t -> t.getDueDate().isBefore(LocalDate.now()) && t.getStatus() != TaskStatus.DONE)
             .forEach(t -> System.out.println("⚠️ Tâche en retard : " + t.getName() + " (échéance " + t.getDueDate() + ")"));
    }

    public void stop(){
        scheduler.shutdown();
    }

    

    

}
