package com.sport;

import java.sql.Connection;
import java.sql.SQLException;

import com.sport.utils.DBConnection;

public class App {
    public static void main(String[] args) {
        System.out.println("--- Test simple de connexion à la BDD depuis App.java ---");

        try (Connection conn = DBConnection.getConnection()) { // Auto-close
            if (conn != null && conn.isValid(2)) { // timeout 2 secondes
                System.out.println("Connexion réussie et valide !");
            } else {
                System.err.println("La connexion n'est pas valide.");
            }
        } catch (SQLException e) {
            System.err.println("Échec de la connexion : " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Ferme la connexion si jamais elle n'est pas fermée automatiquement
            DBConnection.closeConnection();
        }
    }
}
