package com.project.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.project.enums.ProjectStatus;
import com.project.enums.TaskStatus;

public class Project  implements Serializable {
    private String name;
    private String description;
    private LocalDate from;
    private LocalDate to;
    private final List<Task> tasks = new ArrayList<>();
    private ProjectStatus status;
    private double progression;

    public Project(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = ProjectStatus.IN_PROGRESS;
        this.progression = 0.0;
        this.from = LocalDate.now();
        this.to = null; // Date de fin pas encore définie
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

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public double getProgression() {
        return progression;
    }

    public void setProgression(double progression) {
        this.progression = progression;
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    

    public void addTask(Task task) {
        tasks.add(task);
        updateProgression();
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        updateProgression();
    }

    private void updateProgression() {
        if (tasks.isEmpty()) {
            progression = 0.0;
            return;
        }
        long doneCount = tasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.DONE)
                .count();
        progression = (double) doneCount / tasks.size() * 100;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Project{");
        sb.append("name=").append(name);
        sb.append(", description=").append(description);
        sb.append(", from=").append(from);
        sb.append(", to=").append(to);
        sb.append(", tasks=").append(tasks);
        sb.append(", status=").append(status);
        sb.append(", progression=").append(progression);
        sb.append('}');
        return sb.toString();
    }
}
