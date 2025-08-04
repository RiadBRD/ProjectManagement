package com.project.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.project.entity.DataBaseConnectivity;
import com.project.entity.Project;
import com.project.entity.Task;
import com.project.enums.TaskPriority;
import com.project.enums.TaskStatus;
import com.project.exceptions.DuplicateProjectException;
import com.project.exceptions.DuplicateTaskException;
import com.project.interfaces.ITaskRepository;

public class TaskRepository implements ITaskRepository {
    private final List<Project> projects = new ArrayList<>();
    private final DataBaseConnectivity db;

    public TaskRepository(DataBaseConnectivity db) {
        this.db = db;
    }

    @Override
    public Task create(String name, String description, LocalDate dueDate, TaskStatus taskStatus,
            TaskPriority taskPriority) {
        return new Task(name, description, dueDate, taskStatus, taskPriority);

    }

    @Override
    public void add(Task task, int projectId) throws DuplicateTaskException, SQLException {
        String sql = "INSERT INTO taches(projet_id,nom,description,date_echeance,statut,priorite) VALUES (?,?,?,?,?,?)";
        try {
            db.executeUpdate(sql,
                    projectId,
                    task.getName(),
                    task.getDescription(),
                    task.getDueDate(),
                    task.getStatus(),
                    task.getPriority());

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Code d'erreur MySQL pour duplicate entry
                throw new DuplicateProjectException("Projet existe déjà: " + task.getName());
            }
            throw e;
        }
    }

    @Override
    public boolean delete(Task task) throws SQLException {
        final String sql = "DELETE FROM taches WHERE nom = ?";

        int rowsAffected = db.executeUpdate(sql, task.getName());

        return rowsAffected > 0;
    }

    @Override
    public boolean update(Task task) throws SQLException {
        String sql = "UPDATE taches SET description = ?,statut=? WHERE nom=?";
        int rows = db.executeUpdate(sql,
                task.getDescription(),
                task.getStatus().name(),
                task.getName());

        return rows > 0;
    }

    @Override
    public List<Task> getAll() throws SQLException {
        final String sql = "SELECT * FROM taches";
        List<Task> tasks = new ArrayList<>();

        try (ResultSet rs = db.executeQuery(sql)) {
            while (rs.next()) {
                Task task = new Task(
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDate("due_date").toLocalDate(),
                        TaskStatus.valueOf(rs.getString("status")),
                        TaskPriority.valueOf(rs.getString("priority")));
                tasks.add(task);
            }
        }
        return tasks;
    }

    @Override
    public Task findByName(String name) throws SQLException {
        final String sql = "SELECT * FROM taches WHERE nom = ?";

        try (ResultSet rs = db.executeQuery(sql, name)) {
            if (rs.next()) {
                return new Task(
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDate("due_date").toLocalDate(),
                        TaskStatus.valueOf(rs.getString("status")),
                        TaskPriority.valueOf(rs.getString("priority")));
            }
        }
        return null;
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) throws SQLException {
        final String sql = "SELECT * FROM taches WHERE status = ?";
        List<Task> tasks = new ArrayList<>();

        try (ResultSet rs = db.executeQuery(sql, status.name())) {
            while (rs.next()) {
                Task task = new Task(
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDate("due_date").toLocalDate(),
                        status, // On utilise le statut passé en paramètre
                        TaskPriority.valueOf(rs.getString("priority")));
                tasks.add(task);
            }
        }
        return tasks;
    }

}
