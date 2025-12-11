package com.sport.repository;

import com.sport.model.Membre;
import com.sport.model.Performance;
import com.sport.utils.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PerformanceRepository {

    // CREATE : Ajouter une performance
    public void ajouterPerformance(Performance perf) {
        // Mise à jour de la requête SQL pour correspondre au modèle
        String sql = "INSERT INTO performance (membre_id, date_mesure, poids, imc, tourTaille, force_musculaire, endurance) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1. Membre ID
            if (perf.getMembre() != null) {
                stmt.setInt(1, perf.getMembre().getId());
            } else {
                System.out.println("Erreur : Impossible d'ajouter une performance sans membre.");
                return;
            }

            // 2. Date (Conversion LocalDate -> SQL Date)
            if (perf.getDateMesure() != null) {
                stmt.setDate(2, java.sql.Date.valueOf(perf.getDateMesure()));
            } else {
                stmt.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            }

            // 3. Autres données du modèle
            stmt.setDouble(3, perf.getPoids());
            stmt.setDouble(4, perf.getImc());
            stmt.setDouble(5, perf.getTourTaille());
            stmt.setDouble(6, perf.getForce());
            stmt.setDouble(7, perf.getEndurance());

            stmt.executeUpdate();
            
            // Récupérer l'ID généré
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                perf.setId(rs.getInt(1));
            }
            System.out.println("Performance ajoutée avec succès !");

        } catch (SQLException e) {
            System.out.println("Erreur ajout performance : " + e.getMessage());
        }
    }

    // READ (Tout)
    public List<Performance> listerPerformances() {
        List<Performance> list = new ArrayList<>();
        String sql = "SELECT * FROM performance";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPerformance(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur liste performances : " + e.getMessage());
        }
        return list;
    }

    // READ (Par ID)
    public Performance trouverPerformanceParId(int id) {
        String sql = "SELECT * FROM performance WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPerformance(rs);
            }

        } catch (SQLException e) {
            System.out.println("Erreur recherche performance : " + e.getMessage());
        }
        return null;
    }

    // READ (Spécifique à un Membre) - Trié par date
    public List<Performance> trouverPerformanceParMembreId(int membreId) {
        List<Performance> list = new ArrayList<>();
        String sql = "SELECT * FROM performance WHERE membre_id = ? ORDER BY date_mesure ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, membreId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToPerformance(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur recherche performances membre : " + e.getMessage());
        }
        return list;
    }

    // UPDATE
    public void modifierPerformance(Performance perf) {
        // Mise à jour de la requête SQL
        String sql = "UPDATE performance SET date_mesure = ?, poids = ?, imc = ?, tourTaille = ?, force_musculaire = ?, endurance = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (perf.getDateMesure() != null) {
                stmt.setDate(1, java.sql.Date.valueOf(perf.getDateMesure()));
            } else {
                stmt.setDate(1, null);
            }
            
            stmt.setDouble(2, perf.getPoids());
            stmt.setDouble(3, perf.getImc());
            stmt.setDouble(4, perf.getTourTaille());
            stmt.setDouble(5, perf.getForce());
            stmt.setDouble(6, perf.getEndurance());
            stmt.setInt(7, perf.getId());

            stmt.executeUpdate();
            System.out.println("Performance modifiée !");

        } catch (SQLException e) {
            System.out.println("Erreur modification performance : " + e.getMessage());
        }
    }

    // DELETE
    public void supprimerPerformance(int id) {
        String sql = "DELETE FROM performance WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Performance supprimée !");

        } catch (SQLException e) {
            System.out.println("Erreur suppression performance : " + e.getMessage());
        }
    }

    // --- Méthode utilitaire de Mapping ---
    private Performance mapResultSetToPerformance(ResultSet rs) throws SQLException {
        // Nécessite le constructeur vide public Performance() {} dans le modèle
        Performance p = new Performance();
        p.setId(rs.getInt("id"));
        
        // Conversion SQL Date -> Java LocalDate
        Date dateSql = rs.getDate("date_mesure");
        if (dateSql != null) {
            p.setDateMesure(dateSql.toLocalDate());
        }

        // Mapping des attributs du modèle
        p.setPoids(rs.getDouble("poids"));
        p.setImc(rs.getDouble("imc"));
        
        // Attention aux noms des colonnes dans votre BDD. 
        // J'utilise ici les noms standards, adaptez si nécessaire (ex: "tour_taille" au lieu de "tourTaille")
        try { p.setTourTaille(rs.getDouble("tourTaille")); } catch (SQLException e) { /* ignore si colonne existe pas */ }
        try { p.setForce(rs.getDouble("force_musculaire")); } catch (SQLException e) { /* ignore */ }
        try { p.setEndurance(rs.getDouble("endurance")); } catch (SQLException e) { /* ignore */ }

        // Liaison avec le Membre (via l'ID seulement)
        int membreId = rs.getInt("membre_id");
        if (membreId > 0) {
            Membre m = new Membre();
            m.setId(membreId);
            p.setMembre(m);
        }

        return p;
    }
}