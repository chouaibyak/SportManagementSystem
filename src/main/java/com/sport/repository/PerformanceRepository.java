package com.sport.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sport.model.Membre;
import com.sport.model.Performance;
import com.sport.utils.DBConnection;

public class PerformanceRepository {

    // ➤ CREATE (Avec date_mesure)
    public void ajouterPerformance(Performance perf) {
        String sql = "INSERT INTO performance (membre_id, date_mesure, poids, imc, tourTaille, `force`, endurance) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, perf.getMembre().getId());
            
            // Conversion LocalDate (Java) -> Date (SQL)
            stmt.setDate(2, Date.valueOf(perf.getDateMesure()));
            
            stmt.setDouble(3, perf.getPoids());
            stmt.setDouble(4, perf.getImc());
            stmt.setDouble(5, perf.getTourTaille());
            stmt.setDouble(6, perf.getForce());
            stmt.setDouble(7, perf.getEndurance());

            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                perf.setId(rs.getInt(1));
            }
            System.out.println("Performance ajoutée en BDD !");

        } catch (SQLException e) {
            System.out.println("Erreur ajout performance : " + e.getMessage());
        }
    }

    // ➤ READ (Par ID)
    public Performance trouverPerformanceParId(int id) {
        String sql = "SELECT * FROM performance WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapResultSetToPerformance(rs);
        } catch (SQLException e) {
            System.out.println("Erreur recherche performance : " + e.getMessage());
        }
        return null;
    }

    // ➤ READ (Par Membre - Trié par date_mesure)
    public List<Performance> trouverPerformanceParMembreId(int membreId) {
        List<Performance> list = new ArrayList<>();
        // On trie par date_mesure pour avoir l'historique chronologique
        String sql = "SELECT * FROM performance WHERE membre_id = ? ORDER BY date_mesure ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, membreId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) list.add(mapResultSetToPerformance(rs));
        } catch (SQLException e) {
            System.out.println("Erreur recherche performances membre : " + e.getMessage());
        }
        return list;
    }

    // ➤ UPDATE
    public void modifierPerformance(Performance perf) {
        String sql = "UPDATE performance SET date_mesure=?, poids=?, imc=?, tourTaille=?, `force`=?, endurance=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(perf.getDateMesure()));
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

    // ➤ DELETE
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

    // ➤ MAPPING
    private Performance mapResultSetToPerformance(ResultSet rs) throws SQLException {
        Performance p = new Performance();
        p.setId(rs.getInt("id"));
        
        // Lecture de la colonne date_mesure
        Date dateSql = rs.getDate("date_mesure");
        if (dateSql != null) {
            p.setDateMesure(dateSql.toLocalDate());
        }

        p.setPoids(rs.getDouble("poids"));
        p.setImc(rs.getDouble("imc"));
        
        try { p.setTourTaille(rs.getDouble("tourTaille")); } catch (SQLException e) {}
        try { p.setForce(rs.getDouble("force")); } catch (SQLException e) {}
        p.setEndurance(rs.getDouble("endurance"));

        Membre m = new Membre();
        m.setId(rs.getInt("membre_id"));
        p.setMembre(m);

        return p;
    }
}