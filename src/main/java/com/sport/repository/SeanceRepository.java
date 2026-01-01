package com.sport.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.sport.model.Coach;
import com.sport.model.Salle;
import com.sport.model.Seance;
import com.sport.model.TypeCours;
import com.sport.model.TypeSeance;
import com.sport.utils.DBConnection;

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

   // Dans SeanceRepository.java
    public List<Seance> getSeancesParPeriode(LocalDate debut, LocalDate fin) {
        List<Seance> seances = new ArrayList<>();

        // Remplacer JOIN par LEFT JOIN pour ne pas perdre de données si un coach/salle manque
        String sql = "SELECT s.*, " +
                    "sa.id as salle_id_r, sa.nom as nom_salle, sa.capacite as cap_salle, " +
                    "u.nom as nom_coach, u.prenom as prenom_coach " +
                    "FROM seance s " + // Vérifiez que le nom de la table est en minuscules ou majuscules selon votre BDD
                    "LEFT JOIN salle sa ON s.salle_id = sa.id " +
                    "LEFT JOIN coach c ON s.entraineur_id = c.id_utilisateur " + 
                    "LEFT JOIN utilisateur u ON c.id_utilisateur = u.id " +
                    "WHERE s.dateHeure >= ? AND s.dateHeure < ?";

        LocalDateTime start = debut.atStartOfDay();
        LocalDateTime endExclusive = fin.plusDays(1).atStartOfDay();

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(endExclusive));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Seance seance = new Seance();
                    seance.setId(rs.getInt("id"));
                    seance.setNom(rs.getString("nom"));
                    seance.setCapaciteMax(rs.getInt("capaciteMax"));
                    seance.setDateHeure(rs.getTimestamp("dateHeure").toLocalDateTime());
                    seance.setDuree(rs.getInt("duree"));

                    // IMPORTANT : Vérifier la valeur exacte dans la BDD
                    String typeSeanceStr = rs.getString("typeSeance");
                    if (typeSeanceStr != null) {
                        seance.setTypeSeance(TypeSeance.valueOf(typeSeanceStr.toUpperCase()));
                    }

                    String typeCoursStr = rs.getString("type"); 
                    if (typeCoursStr != null) {
                        seance.setTypeCours(TypeCours.valueOf(typeCoursStr.toUpperCase()));
                    }
                    
                    // Remplissage Salle (Vérification si NULL)
                    if (rs.getInt("salle_id_r") != 0) {
                        Salle salle = new Salle();
                        salle.setId(rs.getInt("salle_id_r"));
                        salle.setNom(rs.getString("nom_salle"));
                        seance.setSalle(salle);
                    }

                    // Remplissage Coach (Vérification si NULL)
                    if (rs.getInt("entraineur_id") != 0) {
                        Coach coach = new Coach();
                        coach.setId(rs.getInt("entraineur_id"));
                        coach.setNom(rs.getString("nom_coach"));
                        coach.setPrenom(rs.getString("prenom_coach"));
                        seance.setEntraineur(coach);
                    }

                    seances.add(seance);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seances;
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

    // Récupérer toutes les séances d'un coach
    public List<Seance> getSeancesByCoach(int coachId) {
        List<Seance> liste = new ArrayList<>();
        String query = "SELECT * FROM seance WHERE entraineur_id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, coachId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Seance s = new Seance();
                s.setId(rs.getInt("id"));
                s.setNom(rs.getString("nom"));
                s.setCapaciteMax(rs.getInt("capaciteMax"));
                s.setDuree(rs.getInt("duree"));
                s.setDateHeure(rs.getTimestamp("dateHeure").toLocalDateTime());

                // Salle
                Salle salle = new Salle();
                salle.setId(rs.getInt("salle_id"));
                s.setSalle(salle);

                // Coach
                Coach coach = new Coach();
                coach.setId(rs.getInt("entraineur_id"));
                s.setEntraineur(coach);

                liste.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return liste;
    }


}
