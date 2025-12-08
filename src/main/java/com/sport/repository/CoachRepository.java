package com.sport.repository;

import com.sport.model.Coach;
import com.sport.model.Seance;
import com.sport.model.Membre;
import com.sport.model.Salle;
import com.sport.model.Performance;
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

    // Ajouter une séance
    public void creerSeance(Coach coach, Seance seance) {
        if (coach == null || seance == null) {
            logger.warning("Erreur : Le coach ou la séance est null.");
            return;
        }
        
        String query = "INSERT INTO seance (nom, capaciteMax, salle_id, dateHeure, entraineur_id, type, duree, typeSeance) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setString(1, seance.getNom());
            stmt.setInt(2, seance.getCapaciteMax());
            stmt.setInt(3, seance.getId());  // ID de la salle
            stmt.setTimestamp(4, Timestamp.valueOf(seance.getDateHeure()));  // Date et heure de la séance
            stmt.setInt(5, coach.getId());  // ID du coach
            stmt.setString(6, seance.getType());  // Type de séance
            stmt.setInt(7, seance.getDuree());  // Durée de la séance
            stmt.setString(8, seance.getTypeSeance());  // Type de séance (collective ou individuelle)

            stmt.executeUpdate();
            logger.info("Séance ajoutée avec succès : " + seance.getNom());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de l'ajout de la séance", e);
        }
    }

    // Modifier une séance
    public void modifierSeance(Coach coach, Seance seance) {
        if (coach == null || seance == null) {
            logger.warning("Erreur : Le coach ou la séance est null.");
            return;
        }

        String query = "UPDATE seance SET nom = ?, capaciteMax = ?, salle_id = ?, dateHeure = ?, type = ?, duree = ?, typeSeance = ? WHERE id = ? AND entraineur_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setString(1, seance.getNom());
            stmt.setInt(2, seance.getCapaciteMax());
            stmt.setInt(3, seance.getId());  // ID de la salle
            stmt.setTimestamp(4, Timestamp.valueOf(seance.getDateHeure()));  // Date et heure de la séance
            stmt.setString(5, seance.getType());  // Type de séance
            stmt.setInt(6, seance.getDuree());  // Durée de la séance
            stmt.setString(7, seance.getTypeSeance());  // Type de séance (collective ou individuelle)
            stmt.setInt(8, seance.getId());  // ID de la séance à modifier
            stmt.setInt(9, coach.getId());  // ID du coach qui modifie la séance

            stmt.executeUpdate();
            logger.info("Séance modifiée avec succès : " + seance.getNom());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la modification de la séance", e);
        }
    }

    // Supprimer une séance
    public void supprimerSeance(Coach coach, Seance seance) {
        if (coach == null || seance == null) {
            logger.warning("Erreur : Le coach ou la séance est null.");
            return;
        }

        String query = "DELETE FROM seance WHERE id = ? AND entraineur_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setInt(1, seance.getId());
            stmt.setInt(2, coach.getId());

            stmt.executeUpdate();
            logger.info("Séance supprimée avec succès : " + seance.getNom());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la suppression de la séance", e);
        }
    }

    // Vérifier la disponibilité d'une salle pour une séance
    public boolean verifierDisponibiliteSalle(Salle salle, Date date) {
        String query = "SELECT * FROM seance WHERE salle_id = ? AND dateHeure = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setInt(1, salle.getId());
            stmt.setDate(2, date);

            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return false;  // La salle est déjà occupée à cette date
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la vérification de la disponibilité de la salle", e);
        }
        return true;  // La salle est disponible
    }

    // Récupérer toutes les séances d'un coach
    public List<Seance> getSeancesByCoach(Coach coach) {
        List<Seance> seances = new ArrayList<>();
        String query = "SELECT * FROM seance WHERE entraineur_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, coach.getId());
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                seances.add(new Seance(
                    resultSet.getInt("id"),
                    resultSet.getString("nom"),
                    resultSet.getInt("capaciteMax"),
                    resultSet.getInt("salle_id"),
                    resultSet.getTimestamp("dateHeure").toLocalDateTime(),
                    resultSet.getInt("entraineur_id"),
                    resultSet.getString("type"),
                    resultSet.getInt("duree"),
                    resultSet.getString("typeSeance")
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des séances", e);
        }
        return seances;
    }

    // Consulter la progression d'un membre
    public void consulterProgression(Membre membre) {
        if (membre == null) {
            logger.warning("Erreur : Le membre est null.");
            return;
        }

        String query = "SELECT * FROM performance WHERE membre_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            
            stmt.setInt(1, membre.getId());
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                logger.info("Performance de " + membre.getNom() + ": " + resultSet.getString("mesures"));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la consultation de la progression", e);
        }
    }


}
