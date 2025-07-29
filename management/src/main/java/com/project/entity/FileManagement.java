package com.project.entity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.List;

public class FileManagement {
    private static final String FINAL_PATH = "C:\\Users\\Riad\\Desktop\\Project Management\\management\\reports";
    private Path path;
    private List<Project> projects;

    public FileManagement(List<Project> projects) {
        this.path = Path.of(FINAL_PATH);
        this.projects = projects;
    }

    public void writeToFile(){
        try {
            ObjectOutputStream out  = new ObjectOutputStream(new FileOutputStream(FINAL_PATH));
            out.writeObject(projects);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
