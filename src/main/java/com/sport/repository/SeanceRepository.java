package com.sport.repository;

import com.sport.model.Seance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeanceRepository {

    private Connection connection;

    // Constructeur qui initialise la connexion à la base de données
    public SeanceRepository() {
        try {
            // Remplacez ceci par votre propre chaîne de connexion JDBC
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sportdb", "root", "password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour sauvegarder une Séance
    public void save(Seance seance) {
        String query = "INSERT INTO seances (nom, date, type_cours, coach_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, seance.getNom());
            stmt.setDate(2, new java.sql.Date(seance.getDate().getTime()));
            stmt.setString(3, seance.getTypeCours());
            stmt.setString(4, seance.getCoach().getId());  // Id du coach
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour récupérer une Séance par son ID
    public Seance findById(int id) {
        String query = "SELECT * FROM seances WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String nom = resultSet.getString("nom");
                Date date = resultSet.getDate("date");
                String typeCours = resultSet.getString("type_cours");
                String coachId = resultSet.getString("coach_id");
                // Récupérer le coach depuis le repository
                Coach coach = new CoachRepository().findById(coachId);
                return new Seance(nom, date, typeCours, coach);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Méthode pour récupérer toutes les séances
    public List<Seance> findAll() {
        List<Seance> seances = new ArrayList<>();
        String query = "SELECT * FROM seances";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String nom = resultSet.getString("nom");
                Date date = resultSet.getDate("date");
                String typeCours = resultSet.getString("type_cours");
                String coachId = resultSet.getString("coach_id");
                Coach coach = new CoachRepository().findById(coachId);
                seances.add(new Seance(nom, date, typeCours, coach));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seances;
    }

    // Méthode pour supprimer une Séance par son ID
    public void delete(int id) {
        String query = "DELETE FROM seances WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
