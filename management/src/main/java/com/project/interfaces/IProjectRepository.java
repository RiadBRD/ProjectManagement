package com.project.interfaces;

import java.sql.SQLException;
import java.util.List;

import com.project.entity.Project;
import com.project.enums.ProjectStatus;
import com.project.exceptions.DuplicateProjectException;
import com.project.exceptions.DuplicateTaskException;

public interface IProjectRepository {
    public Project create(String name,String description);
    
    public void add(Project project) throws DuplicateProjectException,SQLException;

    public boolean delete(Project project) throws DuplicateTaskException, SQLException;

    boolean update(Project project) throws SQLException;

    List<Project> getAll() throws SQLException;

    Project findByName(String name);

    List<Project> findByStatus(ProjectStatus status) throws SQLException;
}
