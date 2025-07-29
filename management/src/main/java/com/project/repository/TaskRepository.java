package com.project.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.project.entity.Task;
import com.project.enums.TaskPriority;
import com.project.enums.TaskStatus;
import com.project.exceptions.DuplicateTaskException;
import com.project.interfaces.ITaskRepository;

public class TaskRepository implements ITaskRepository {

    private final List<Task> tasks = new ArrayList<>();

    @Override
    public Task create(String name, String description, LocalDate dueDate, TaskStatus status, TaskPriority priority) {
        return new Task(name, description, dueDate, status, priority);
    }

    @Override
    public void add(Task task) throws DuplicateTaskException {
        if (findByName(task.getName()) != null) {
            throw new DuplicateTaskException("Une tâche avec ce nom existe déjà : " + task.getName());
        }
        tasks.add(task);
    }

    @Override
    public boolean delete(Task task) {
        return tasks.remove(task);
    }

    @Override
    public boolean update(Task task) {
        int index = -1;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getName().equalsIgnoreCase(task.getName())) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            tasks.set(index, task);
            return true;
        }
        return false;
    }

    @Override
    public List<Task> getAll() {
        return new ArrayList<>(tasks);
    }

    @Override
    public Task findByName(String name) {
        return tasks.stream()
                .filter(t -> t.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        return tasks.stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }
}
