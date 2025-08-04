package com.project.services;

import java.sql.SQLException;
import java.util.List;

import com.project.entity.Project;
import com.project.exceptions.BusinessException;
import com.project.exceptions.DuplicateProjectException;
import com.project.exceptions.DuplicateTaskException;
import com.project.interfaces.IProjectRepository;

public class ProjectService {
    private final IProjectRepository projectRepo;

    public ProjectService(IProjectRepository projectRepo) {
        this.projectRepo = projectRepo;
    }

    public Project createProject(String name, String description)
            throws DuplicateProjectException, SQLException {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du projet ne peut pas être vide");
        }

        Project project = projectRepo.create(name, description);
        projectRepo.add(project);

        return project;
    }

    public List<Project> getAllProjects() throws SQLException {
        return projectRepo.getAll();
    }

    public void displayProjectsSummary() throws SQLException {
        List<Project> projects = projectRepo.getAll();
        if (projects.isEmpty()) {
            System.out.println("Aucun projet existant");
            return;
        }

        System.out.println("\nListe des projets:");
        projects.forEach(p -> System.out.printf("- %s (Statut: %s)\n",
                p.getName(),
                p.getStatus()));
    }

    public Project findByName(String name) throws BusinessException {
        Project project = projectRepo.findByName(name);
        if (project == null) {
            throw new BusinessException("Projet non trouvé: " + name);
        }
        return project;
    }

    public boolean updateProject(Project project) throws BusinessException {
        try {
            // Validation avant mise à jour
            if (project.getName() == null || project.getName().trim().isEmpty()) {
                throw new BusinessException("Le nom du projet ne peut pas être vide");
            }

            return projectRepo.update(project);
        } catch (SQLException e) {
            throw new BusinessException("Erreur lors de la mise à jour du projet: " + e.getMessage());
        }
    }

    public boolean deleteProject(Project project) throws BusinessException, DuplicateTaskException, SQLException {
        // Vérifier s'il y a des tâches associées
        if (!project.getTasks().isEmpty()) {
            throw new BusinessException("Impossible de supprimer - le projet contient des tâches");
        }
        return projectRepo.delete(project);
    }
}