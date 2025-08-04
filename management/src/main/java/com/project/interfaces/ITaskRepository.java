package com.project.interfaces;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.project.entity.Task;
import com.project.enums.TaskPriority;
import com.project.enums.TaskStatus;
import com.project.exceptions.DuplicateTaskException;

public interface ITaskRepository {
    Task create(String name,String description,LocalDate dueDate,TaskStatus taskStatus,TaskPriority taskPriority);

    void add(Task task,int projectId) throws DuplicateTaskException, SQLException;

    boolean delete(Task task) throws SQLException;

    boolean update(Task task) throws SQLException;

    List<Task> getAll() throws SQLException;

    Task findByName(String name) throws SQLException;

    List<Task> findByStatus(TaskStatus status) throws SQLException;
    




}
