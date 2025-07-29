package com.project.designpatterns;

import java.sql.SQLException;

import com.project.entity.DataBaseConnectivity;

public class Database {

    private static Database instance; // Singleton
    private DataBaseConnectivity db;  // Ton objet de connexion

    // Constructeur privé pour empêcher l'instanciation directe
    private Database() {
        db = new DataBaseConnectivity();
        try {
            db.connect();
        } catch (SQLException e) {
            System.err.println("❌ Impossible de se connecter : " + e.getMessage());
        }
    }

    // Méthode d'accès à l'instance unique
    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    // Exposer l'objet de connexion pour exécuter des requêtes
    public DataBaseConnectivity getDb() {
        return db;
    }
}
