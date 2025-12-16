package com.sport.repository;

import com.sport.model.Coach;
import com.sport.model.Seance;
import com.sport.model.Salle;
import com.sport.utils.DBConnection;

import java.sql.*;

public class SeanceRepository {

    // Créer une séance
    // Dans SeanceRepository.java

    public void creerSeance(Coach coach, Seance seance) {
        // 1. Ajouter RETURN_GENERATED_KEYS
        String query = "INSERT INTO seance (nom, capaciteMax, salle_id, dateHeure, entraineur_id, type, duree) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) { // <--- ICI

            stmt.setString(1, seance.getNom());
            stmt.setInt(2, seance.getCapaciteMax());
            stmt.setInt(3, seance.getSalle().getId());
            stmt.setTimestamp(4, Timestamp.valueOf(seance.getDateHeure()));
            stmt.setInt(5, coach.getId());
            stmt.setString(6, seance.getTypeCours().toString());
            stmt.setInt(7, seance.getDuree());

            stmt.executeUpdate();

            // 2. RECUPERER L'ID GÉNÉRÉ (C'est la partie manquante)
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                seance.setId(rs.getInt(1)); // Met à jour l'objet Java
            }
            System.out.println("Séance créée en BDD avec ID: " + seance.getId());

        } catch (SQLException e) {
            System.out.println("Erreur creation seance : " + e.getMessage());
        }
    }

    // Modifier une séance
    public void modifierSeance(Seance seance, int coachId) {
        String query = "UPDATE seance SET nom = ?, capaciteMax = ?, salle_id = ?, dateHeure = ?, type = ?, duree = ? WHERE id = ? AND entraineur_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, seance.getNom());
            stmt.setInt(2, seance.getCapaciteMax());
            stmt.setInt(3, seance.getSalle().getId());

            // --- CORRECTION : LocalDateTime -> Timestamp ---
            stmt.setTimestamp(4, Timestamp.valueOf(seance.getDateHeure()));
            // -----------------------------------------------

            stmt.setString(5, seance.getTypeCours().toString());
            stmt.setInt(6, seance.getDuree());
            stmt.setInt(7, seance.getId());
            stmt.setInt(8, coachId);

            int result = stmt.executeUpdate();
            if (result == 0) {
                System.out.println("Aucune modification : Séance introuvable ou mauvais coach.");
            } else {
                System.out.println("Séance modifiée avec succès.");
            }

        } catch (SQLException e) {
            System.out.println("Erreur modification seance : " + e.getMessage());
        }
    }

    // Supprimer une séance (Inchangé car pas de date utilisée)
    public void supprimerSeance(int seanceId, int coachId) {
        String query = "DELETE FROM seance WHERE id = ? AND entraineur_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, seanceId);
            stmt.setInt(2, coachId);

            int result = stmt.executeUpdate();
            System.out.println(result > 0 ? "Séance supprimée." : "Echec suppression.");

        } catch (SQLException e) {
            System.out.println("Erreur suppression seance : " + e.getMessage());
        }
    }

    // Vérifier disponibilité
    // On change le paramètre String -> LocalDateTime
    public boolean verifierDisponibiliteSalle(Salle salle, java.time.LocalDateTime dateHeure) {
        
        String query = "SELECT COUNT(*) FROM seance WHERE salle_id = ? AND dateHeure = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, salle.getId());
            
            // --- CORRECTION : Utilisation de setTimestamp ---
            stmt.setTimestamp(2, Timestamp.valueOf(dateHeure));
            // ------------------------------------------------

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Si 0, c'est libre
            }
        } catch (SQLException e) {
            System.out.println("Erreur verification salle : " + e.getMessage());
        }
        return false;
    }
}
