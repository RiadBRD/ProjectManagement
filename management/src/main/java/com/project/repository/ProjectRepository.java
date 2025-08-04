package com.project.repository;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.project.entity.DataBaseConnectivity;
import com.project.entity.Project;
import com.project.enums.ProjectStatus;
import com.project.exceptions.DuplicateProjectException;
import com.project.exceptions.DuplicateTaskException;
import com.project.interfaces.IProjectRepository;

public class ProjectRepository implements IProjectRepository {

    private final List<Project> projects = new ArrayList<>();
    private final DataBaseConnectivity db;

    public ProjectRepository(DataBaseConnectivity db) {
        this.db = db;
    }

    @Override
    public Project create(String name, String description) {
        return new Project(name, description);
    }

    @Override
    public void add(Project project) throws DuplicateProjectException, SQLException {
        String sql = "INSERT INTO projets (nom, description, date_debut, statut, progression) VALUES (?, ?, ?, ?, ?)";

        try {
            db.executeUpdate(sql,
                    project.getName(),
                    project.getDescription(),
                    Date.valueOf(project.getFrom()),
                    project.getStatus().name(),
                    project.getProgression());
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Code d'erreur MySQL pour duplicate entry
                throw new DuplicateProjectException("Projet existe déjà: " + project.getName());
            }
            throw e;
        }
    }

    @Override
    public boolean delete(Project project) throws DuplicateTaskException, SQLException{
        final String sql = "DELETE FROM projets WHERE nom = ?";

        // Use the existing executeUpdate method from DataBaseConnectivity
        int rowsAffected = db.executeUpdate(sql, project.getName());

        return rowsAffected > 0;
    }

    @Override
    public boolean update(Project project) throws SQLException {
        String sql = "UPDATE projets SET description = ?,statut=? WHERE nom=?";
        int rows = db.executeUpdate(sql, 
        project.getDescription(),
        project.getStatus().name(),
        project.getName());

        return rows>0;
    }

    @Override
    public List<Project> getAll() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projets";
        try(ResultSet rs = db.executeQuery(sql)){
            while(rs.next()){
                Project p = new Project(
                    rs.getString("nom"),
                    rs.getString("description"));
                
                p.setFrom(rs.getDate("date_debut").toLocalDate());
                if (rs.getDate("date_fin") != null) {
                    p.setTo(rs.getDate("date_fin").toLocalDate());
                }
                p.setStatus(ProjectStatus.valueOf(rs.getString("statut")));
                p.setProgression(rs.getDouble("progression"));

                 projects.add(p);
            }
        }
        return projects;
    }

    @Override
    public Project findByName(String name) {
        String sql = "SELECT * FROM projets WHERE name = ?";
        try {
            ResultSet rs = db.executeQuery(sql, name);
            Project p = new Project(rs.getNString("name"), rs.getNString("description"));
            return p;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Project> findByStatus(ProjectStatus status) throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projets where status = ?";
        try(ResultSet rs = db.executeQuery(sql,status.name())){
            while(rs.next()){
                Project p = new Project(
                    rs.getString("nom"),
                    rs.getString("description"));
                
                p.setFrom(rs.getDate("date_debut").toLocalDate());
                if (rs.getDate("date_fin") != null) {
                    p.setTo(rs.getDate("date_fin").toLocalDate());
                }
                p.setStatus(ProjectStatus.valueOf(rs.getString("statut")));
                p.setProgression(rs.getDouble("progression"));

                 projects.add(p);
            }
        }
        return projects;
    }


    
}
