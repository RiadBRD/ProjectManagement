package com.project.repository;

import java.util.ArrayList;
import java.util.List;

import com.project.entity.Project;
import com.project.enums.ProjectStatus;
import com.project.exceptions.DuplicateProjectException;
import com.project.interfaces.IProjectRepository;

public class ProjectRepository implements IProjectRepository {

    private final List<Project> projects = new ArrayList<>();

    @Override
    public Project create(String name, String description) {
        return new Project(name, description);
    }

    @Override
    public void add(Project project) throws DuplicateProjectException {
        if (findByName(project.getName()) != null) {
            throw new DuplicateProjectException("Un projet avec ce nom existe déjà : " + project.getName());
        }
        this.projects.add(project);
    }

    @Override
    public boolean delete(Project project) {
        return this.projects.remove(project);
    }

    @Override
    public boolean update(Project project) {
        int index = -1;
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getName().equalsIgnoreCase(project.getName())) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            projects.set(index, project);
            return true;
        }
        return false;
    }

    @Override
    public List<Project> getAll() {
        return new ArrayList<>(this.projects);
    }

    @Override
    public Project findByName(String name) {
        return this.projects.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public List<Project> findByStatus(ProjectStatus status) {
        return this.projects.stream().filter(s -> s.getStatus().equals(status)).toList();
    }

}
