package com.project.entity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class DataBaseConnectivity {
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_projets";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private Connection connection;

    // Connexion à la base

    public void connect() throws SQLException {
        try {
            Properties props = new Properties();
            props.put("user", USER);
            props.put("password", PASSWORD);
            props.put("useSSL", "false");
            props.put("serverTimezone", "UTC");
            this.connection = DriverManager.getConnection(URL, props);
            System.out.println("✅ Connexion réussie à MySQL !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion : " + e.getMessage());
        }
    }

    public void disconnect() throws SQLException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Déconnexion de MySQL.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la fermeture : " + e.getMessage());
        }
    }

    public PreparedStatement preparedStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement stmt = preparedStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate();
        }
    }

    
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = preparedStatement(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeQuery();
    }

    public boolean isConnected() throws SQLException {
        if (connection.isClosed()) {
            return false;
        }
        return true;
    }

}
