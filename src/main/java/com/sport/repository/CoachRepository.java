package com.sport.repository;

import com.sport.model.Coach;
import com.sport.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoachRepository {

    // ➤ LECTURE : Trouver par ID (AVEC JOINTURE)
    public Coach getCoachById(int id) {
        // On joint COACH avec UTILISATEUR pour avoir le nom, prenom, etc.
        String sql = "SELECT u.id_utilisateur, u.nom, u.prenom, u.email, u.telephone, u.adresse " +
                     "FROM COACH c " +
                     "JOIN UTILISATEUR u ON c.id_utilisateur = u.id_utilisateur " +
                     "WHERE c.id_utilisateur = ?";
                     
        String sqlSpec = "SELECT specialite FROM COACH_SPECIALITE WHERE coach_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Coach c = new Coach();
                c.setId(rs.getInt("id_utilisateur"));
                c.setNom(rs.getString("nom"));
                c.setPrenom(rs.getString("prenom"));
                c.setEmail(rs.getString("email"));
                c.setTelephone(rs.getString("telephone"));
                c.setAdresse(rs.getString("adresse"));

                // Récupération des spécialités
                try (PreparedStatement stmtSpec = conn.prepareStatement(sqlSpec)) {
                    stmtSpec.setInt(1, id);
                    ResultSet rsSpec = stmtSpec.executeQuery();
                    // On adapte selon si vous avez choisi List ou Set dans votre modèle
                    List<String> specs = new ArrayList<>(); 
                    while (rsSpec.next()) {
                        specs.add(rsSpec.getString("specialite"));
                    }
                    c.setSpecialites(specs); 
                }
                return c;
            }
        } catch (SQLException e) {
            System.out.println("Erreur findById coach : " + e.getMessage());
        }
        return null;
    }

    // ➤ LECTURE : Lister tout (AVEC JOINTURE)
    public List<Coach> listerCoachs() {
        List<Coach> coaches = new ArrayList<>();
        String sql = "SELECT u.id_utilisateur, u.nom, u.prenom, u.email, u.telephone " +
                     "FROM COACH c " +
                     "JOIN UTILISATEUR u ON c.id_utilisateur = u.id_utilisateur";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Coach c = new Coach();
                c.setId(rs.getInt("id_utilisateur"));
                c.setNom(rs.getString("nom"));
                c.setPrenom(rs.getString("prenom"));
                c.setEmail(rs.getString("email"));
                // ... on pourrait charger les spécialités ici aussi si besoin ...
                coaches.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lister coachs : " + e.getMessage());
        }
        return coaches;
    }

    // ➤ AJOUT (Reste similaire, assurez-vous que l'Utilisateur existe déjà)
    public void ajouterCoach(Coach coach) {
        String sqlCoach = "INSERT INTO COACH (id_utilisateur) VALUES (?)";
        // ... (votre code d'ajout existant était correct pour l'insertion) ...
        // Je le remets brièvement pour l'exemple :
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlCoach)) {
            stmt.setInt(1, coach.getId());
            stmt.executeUpdate();
            System.out.println("Coach ajouté (table liaison).");
        } catch (SQLException e) {
            System.out.println("Erreur ajout : " + e.getMessage());
        }
    }
    
    // ➤ MODIFICATION (Transaction complète)
    public void modifierCoach(Coach coach) {
        // ... (Utilisez le code "Simplifié" avec try-with-resources que je vous ai donné précédemment) ...
    }

    // ➤ SUPPRESSION
    public void supprimerCoach(int id) {
        // ... (Votre code existant pour supprimer coach + specialites) ...
    }
}