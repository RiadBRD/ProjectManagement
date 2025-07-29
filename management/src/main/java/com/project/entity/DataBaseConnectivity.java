package com.project.entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DataBaseConnectivity {
    private static final String URL = "jdbc:mysql://localhost:3306/project?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private Connection connection;

    // Connexion à la base

    public void connect() throws SQLException {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connexion réussie à MySQL !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion : " + e.getMessage());
        }
    }

      public void disconnect() throws SQLException{
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Déconnexion de MySQL.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la fermeture : " + e.getMessage());
        }
    }

    // Exécuter une requête SELECT
    public void executeQuery(String query) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Exemple : affichage des colonnes génériques
                System.out.println(rs.getInt(1) + " - " + rs.getString(2));
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur d'exécution de la requête : " + e.getMessage());
        }
    }

    public boolean isConnected() throws SQLException{
        if(connection.isClosed()){
            return false;
        }
        return true;
    }
}
