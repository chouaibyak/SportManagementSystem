package com.sport.repository;

import com.sport.model.Abonnement;
import com.sport.model.StatutAbonnement;
import com.sport.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbonnementRepository {

    // ➤ CREATE (Correction : Ajout de membre_id)
    public void ajouterAbonnement(Abonnement abonnement) {
        String sql = "INSERT INTO abonnement (membre_id, type, statut, autorenouvellement) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // IMPORTANT : On lie l'abonnement au membre
            stmt.setInt(1, abonnement.getMembre().getId());
            stmt.setString(2, abonnement.getTypeAbonnement().name());
            stmt.setString(3, abonnement.getStatutAbonnement().name());
            stmt.setBoolean(4, abonnement.isAutorenouvellement());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                abonnement.setId(rs.getInt(1));
            }
            System.out.println("Abonnement ajouté avec succès !");

        } catch (SQLException e) {
            System.out.println("Erreur ajout abonnement : " + e.getMessage());
        }
    }

    // ➤ READ (Tout)
    public List<Abonnement> listerTout() {
        List<Abonnement> list = new ArrayList<>();
        String sql = "SELECT * FROM abonnement";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToAbonnement(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur liste abonnements : " + e.getMessage());
        }

        return list;
    }

    // ➤ READ (Par ID)
    public Abonnement trouverParId(int id) {
        String sql = "SELECT * FROM abonnement WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToAbonnement(rs);
            }

        } catch (SQLException e) {
            System.out.println("Erreur recherche abonnement : " + e.getMessage());
        }

        return null;
    }

    // ➤ READ (Par Statut)
    public List<Abonnement> trouverParStatut(StatutAbonnement statut) {
        List<Abonnement> list = new ArrayList<>();
        String sql = "SELECT * FROM abonnement WHERE statut = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToAbonnement(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur recherche par statut : " + e.getMessage());
        }

        return list;
    }

    // ➤ UPDATE
    public void modifierAbonnement(Abonnement ab) {
        String sql = "UPDATE abonnement SET type = ?, statut = ?, autorenouvellement = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ab.getTypeAbonnement().name());
            stmt.setString(2, ab.getStatutAbonnement().name());
            stmt.setBoolean(3, ab.isAutorenouvellement());
            stmt.setInt(4, ab.getId());

            stmt.executeUpdate();
            System.out.println("Abonnement modifié !");

        } catch (SQLException e) {
            System.out.println("Erreur modification abonnement : " + e.getMessage());
        }
    }

    // ➤ DELETE
    public void supprimerAbonnement(int id) {
        String sql = "DELETE FROM abonnement WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Abonnement supprimé !");

        } catch (SQLException e) {
            System.out.println("Erreur suppression abonnement : " + e.getMessage());
        }
    }

    // ➤ MAPPING (Correction : Récupération de l'ID membre)
    private Abonnement mapResultSetToAbonnement(ResultSet rs) throws SQLException {
        Abonnement ab = new Abonnement();
        ab.setId(rs.getInt("id"));

        // On crée un membre "vide" avec juste l'ID pour faire le lien
        com.sport.model.Membre m = new com.sport.model.Membre();
        m.setId(rs.getInt("membre_id"));
        ab.setMembre(m);

        try { ab.setTypeAbonnement(com.sport.model.TypeAbonnement.valueOf(rs.getString("type"))); } catch (Exception e) {}
        try { ab.setStatutAbonnement(com.sport.model.StatutAbonnement.valueOf(rs.getString("statut"))); } catch (Exception e) {}
        
        ab.setAutorenouvellement(rs.getBoolean("autorenouvellement"));

        return ab;
    }
}
