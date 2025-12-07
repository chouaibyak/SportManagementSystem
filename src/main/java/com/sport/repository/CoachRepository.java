package com.sport.repository;

import com.sport.model.Coach;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoachRepository {

    private static final Logger logger = Logger.getLogger(CoachRepository.class.getName());
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/sport_club?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Méthode pour obtenir une connexion à la base de données
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Sauvegarder un Coach
    public void save(Coach coach) {
        if (coach == null || coach.getNom().isEmpty() || coach.getPrenom().isEmpty() || coach.getEmail().isEmpty()) {
            logger.warning("Erreur : Le coach doit avoir un nom, un prénom et un email.");
            return;
        }

        String query = "INSERT INTO coach (id, nom, prenom, email, mot_de_passe) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            // Hachage du mot de passe avant de le sauvegarder
            String hashedPassword = BCrypt.hashpw(coach.getMotDePasse(), BCrypt.gensalt());

            stmt.setLong(1, coach.getId());
            stmt.setString(2, coach.getNom());
            stmt.setString(3, coach.getPrenom());
            stmt.setString(4, coach.getEmail());
            stmt.setString(5, hashedPassword); // Mot de passe haché
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'ajout du coach", e);
        }
    }

    // Récupérer un Coach par son ID
    public Coach findById(String id) {
        String query = "SELECT * FROM coache WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return new Coach(
                        0, resultSet.getString("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        null,  // Vous pouvez remplacer `null` par une vraie date d'inscription si nécessaire
                        resultSet.getString("email"),
                        resultSet.getString("mot_de_passe"), query
                );
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération du coach", e);
        }
        return null;
    }

public List<Coach> findAll() {
    List<Coach> coaches = new ArrayList<>();
    String query = "SELECT * FROM coach";
    try (Connection connection = getConnection();
         PreparedStatement stmt = connection.prepareStatement(query)) {
        ResultSet resultSet = stmt.executeQuery();
        while (resultSet.next()) {
            coaches.add(new Coach(
                resultSet.getInt("id"), // Utilisation de getInt() pour l'ID
                resultSet.getString("nom"),
                resultSet.getString("prenom"),
                resultSet.getString("date_naissance"), // Vous pouvez ajuster cette ligne si nécessaire
                resultSet.getString("email"),
                resultSet.getString("telephone"),
                resultSet.getString("adresse"),
                resultSet.getString("mot_de_passe")
            ));
        }
    } catch (SQLException e) {
        logger.log(Level.SEVERE, "Erreur lors de la récupération des coachs", e);
    }
    return coaches;
}

    // Supprimer un Coach par son ID
    public void delete(String id) {
        String query = "DELETE FROM coach WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la suppression du coach", e);
        }
    }

// Méthode pour mettre à jour un Coach
public void update(Coach coach) {
    if (coach == null || coach.getNom().isEmpty() || coach.getPrenom().isEmpty() || coach.getEmail().isEmpty()) {
        logger.warning("Erreur : Le coach doit avoir un nom, un prénom et un email.");
        return;
    }

    String query = "UPDATE coaches SET nom = ?, prenom = ?, email = ?, mot_de_passe = ? WHERE id = ?";
    try (Connection connection = getConnection();
         PreparedStatement stmt = connection.prepareStatement(query)) {
        // Hachage du mot de passe avant de le sauvegarder
        String hashedPassword = BCrypt.hashpw(coach.getMotDePasse(), BCrypt.gensalt());

        stmt.setString(1, coach.getNom());
        stmt.setString(2, coach.getPrenom());
        stmt.setString(3, coach.getEmail());
        stmt.setString(4, hashedPassword); // Mot de passe haché
        stmt.setInt(5, coach.getId()); // Utilisation de setInt() pour l'ID
        stmt.executeUpdate();
        logger.info("Coach mis à jour avec succès : " + coach.getNom());
    } catch (SQLException e) {
        logger.log(Level.SEVERE, "Erreur lors de la mise à jour du coach", e);
    }
}

}
