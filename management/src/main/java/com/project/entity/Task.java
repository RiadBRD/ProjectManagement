package com.project.entity;

import java.time.LocalDate;

import com.project.enums.TaskPriority;
import com.project.enums.TaskStatus;

public class Task {
    private String name;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
    private TaskPriority priority;

    public Task(String name, String description, LocalDate dueDate, TaskStatus status, TaskPriority priority) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

}
