package com.project.interfaces;

import java.time.LocalDate;
import java.util.List;

import com.project.entity.Task;
import com.project.enums.TaskPriority;
import com.project.enums.TaskStatus;
import com.project.exceptions.DuplicateTaskException;

public interface ITaskRepository {
    Task create(String name,String description,LocalDate dueDate,TaskStatus taskStatus,TaskPriority taskPriority);

    void add(Task task) throws DuplicateTaskException;

    boolean delete(Task task);

    boolean update(Task task);

    List<Task> getAll();

    Task findByName(String name);

    List<Task> findByStatus(TaskStatus status);
    




}
