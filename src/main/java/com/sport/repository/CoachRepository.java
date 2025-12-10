package com.sport.repository;

import com.sport.model.Coach;
import com.sport.model.Seance;
import com.sport.model.Membre;
import com.sport.model.Salle;

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

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Créer une séance
    public void creerSeance(Coach coach, Seance seance) {
        if (coach == null || seance == null) {
            logger.warning("Erreur : Le coach ou la séance est null.");
            return;
        }

        String query = "INSERT INTO seance (nom, capaciteMax, salle_id, dateHeure, entraineur_id, type, duree) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, seance.getNom());
            stmt.setInt(2, seance.getCapaciteMax());
            stmt.setInt(3, seance.getSalle().getId());
            stmt.setTimestamp(4, Timestamp.valueOf(seance.getDateHeure()));
            stmt.setInt(5, coach.getId());
            stmt.setString(6, seance.getTypeCours().toString());
            stmt.setInt(7, seance.getDuree());

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

        String query = "UPDATE seance SET nom = ?, capaciteMax = ?, salle_id = ?, dateHeure = ?, type = ?, duree = ? WHERE id = ? AND entraineur_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, seance.getNom());
            stmt.setInt(2, seance.getCapaciteMax());
            stmt.setInt(3, seance.getSalle().getId());
            stmt.setTimestamp(4, Timestamp.valueOf(seance.getDateHeure()));
            stmt.setString(5, seance.getTypeCours().toString());
            stmt.setInt(6, seance.getDuree());
            stmt.setInt(7, seance.getId());
            stmt.setInt(8, coach.getId());

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

    // Vérifier la disponibilité d'une salle
    public boolean verifierDisponibiliteSalle(Salle salle, Timestamp dateHeure) {
        String query = "SELECT * FROM seance WHERE salle_id = ? AND dateHeure = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setInt(1, salle.getId());
            stmt.setTimestamp(2, dateHeure);

            ResultSet resultSet = stmt.executeQuery();
            return !resultSet.next();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la vérification de la disponibilité de la salle", e);
        }
        return false;
    }

    // Récupérer toutes les séances d'un coach
    public List<Seance> getSeancesByCoach(Coach coach) {
        List<Seance> seances = new ArrayList<>();
        String query = "SELECT * FROM seance WHERE entraineur_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, coach.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Ici, tu devras créer des instances concrètes de Seance selon ton type (ex: CollectiveSeance, IndividuelleSeance)
                // Pour l'instant, on peut juste logger les données
                logger.info("Séance trouvée: " + rs.getString("nom"));
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
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                logger.info("Performance de " + membre.getNom() + ": " + rs.getString("mesures"));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur lors de la consultation de la progression", e);
        }
    }


    // Méthode pour récupérer un coach par son ID
public Coach findById(int id) {
    String query = "SELECT * FROM coach WHERE id = ?";
    try (Connection connection = getConnection();
         PreparedStatement stmt = connection.prepareStatement(query)) {

        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Coach coach = new Coach();
            coach.setId(rs.getInt("id"));
            coach.setNom(rs.getString("nom"));
            coach.setEmail(rs.getString("email"));
            coach.setTelephone(rs.getString("tel"));
            // ajouter d'autres champs si nécessaire
            return coach;
        }
    } catch (SQLException e) {
        logger.log(Level.SEVERE, "Erreur lors de la récupération du coach", e);
    }
    return null; // si aucun coach trouvé
}
public List<Coach> findAll() {
    List<Coach> coaches = new ArrayList<>();
    String query = "SELECT * FROM coach";

    try (Connection connection = getConnection();
         PreparedStatement stmt = connection.prepareStatement(query)) {

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id");
            String nom = rs.getString("nom");
            String prenom = rs.getString("prenom");
            String dateNaissance = rs.getString("date_naissance");
            String email = rs.getString("email");
            String telephone = rs.getString("telephone");
            String adresse = rs.getString("adresse");

            // Attention : ton constructeur Coach complet attend 8 paramètres
            Coach coach = new Coach(id, nom, prenom, dateNaissance, email, telephone, adresse); 
            coaches.add(coach);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return coaches;
}


}
