package com.project.services;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.project.entity.Task;
import com.project.enums.TaskPriority;
import com.project.enums.TaskStatus;
import com.project.exceptions.BusinessException;
import com.project.exceptions.DuplicateTaskException;
import com.project.interfaces.ITaskRepository;

public class TaskService {
    private final ITaskRepository taskRepository;

    public TaskService(ITaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Crée une nouvelle tâche avec validation
     */
    public Task createTask(String name, String description, LocalDate dueDate,
            TaskStatus status, TaskPriority priority)
            throws BusinessException, SQLException {

        // Validation
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("Le nom de la tâche ne peut pas être vide");
        }
        if (dueDate.isBefore(LocalDate.now())) {
            throw new BusinessException("La date d'échéance ne peut pas être dans le passé");
        }

        // Création et sauvegarde
        Task task = taskRepository.create(name, description, dueDate, status, priority);
        taskRepository.add(task, 0);

        return task;
    }

    /**
     * Ajoute une tâche à un projet spécifique
     */
    public void addTaskToProject(Task task, String projectName)
            throws BusinessException, SQLException {
        try {
            if (taskRepository.findByName(task.getName()) != null) {
                throw new DuplicateTaskException("Une tâche avec ce nom existe déjà");
            }
            taskRepository.add(task, 0);
        } catch (SQLException e) {
            throw new BusinessException("Erreur lors de l'ajout de la tâche: " + e.getMessage());
        }
    }

    /**
     * Récupère toutes les tâches
     */
    public List<Task> getAllTasks() throws BusinessException {
        try {
            return taskRepository.getAll();
        } catch (SQLException e) {
            throw new BusinessException("Erreur lors de la récupération des tâches");
        }
    }

    /**
     * Trouve une tâche par son nom
     */
    public Task findByName(String name) throws BusinessException {
        try {
            return taskRepository.findByName(name);
        } catch (SQLException e) {
            throw new BusinessException("Erreur lors de la recherche de la tâche");
        }
    }

    /**
     * Met à jour une tâche existante
     */
    public boolean updateTask(Task task) throws BusinessException {
        try {
            if (taskRepository.findByName(task.getName()) == null) {
                throw new BusinessException("Tâche non trouvée");
            }
            return taskRepository.update(task);
        } catch (SQLException e) {
            throw new BusinessException("Erreur lors de la mise à jour");
        }
    }

    /**
     * Supprime une tâche
     */
    public boolean deleteTask(Task task) throws BusinessException {
        try {
            return taskRepository.delete(task);
        } catch (SQLException e) {
            throw new BusinessException("Erreur lors de la suppression");
        }
    }

    /**
     * Récupère les tâches par statut
     */
    public List<Task> getTasksByStatus(TaskStatus status) throws BusinessException {
        try {
            return taskRepository.findByStatus(status);
        } catch (SQLException e) {
            throw new BusinessException("Erreur lors du filtrage par statut");
        }
    }

    
}
