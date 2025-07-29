package com.project.interfaces;

import java.util.List;

import com.project.entity.Project;
import com.project.enums.ProjectStatus;
import com.project.exceptions.DuplicateProjectException;

public interface IProjectRepository {
    public Project create(String name,String description);
    
    public void add(Project project) throws DuplicateProjectException;

    boolean delete(Project project);

    boolean update(Project project);

    List<Project> getAll();

    Project findByName(String name);

    List<Project> findByStatus(ProjectStatus status);
}
