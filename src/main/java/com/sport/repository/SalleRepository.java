package com.sport.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.sport.model.Salle;
import com.sport.model.TypeSalle;
import com.sport.utils.DBConnection;

public class SalleRepository {

    // ➤ Ajouter une salle
    public void ajouterSalle(Salle salle) {
        String sql = "INSERT INTO salle (nom, capacite, type) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
           
            stmt.setString(1, salle.getNom());
            stmt.setInt(2, salle.getCapacite());
            stmt.setString(3, salle.getType().name());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                salle.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ➤ Obtenir toutes les salles
    public List<Salle> listerSalles() {
        List<Salle> salles = new ArrayList<>();
        String sql = "SELECT * FROM salle";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Salle salle = new Salle(
                        rs.getString("nom"),
                        rs.getInt("capacite"),
                        TypeSalle.valueOf(rs.getString("type"))
                );
                salle.setId(rs.getInt("id"));
                salles.add(salle);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salles;
    }

    // ➤ Obtenir une salle par ID
    public Salle getSalleById(int id) {
        String sql = "SELECT * FROM salle WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Salle salle = new Salle(
                        rs.getString("nom"),
                        rs.getInt("capacite"),
                        TypeSalle.valueOf(rs.getString("type"))
                );
                salle.setId(rs.getInt("id"));
                return salle;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ➤ Mettre à jour une salle
    public boolean modifierSalle(Salle salle) {
        String sql = "UPDATE salle SET nom = ?, capacite = ?, type = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, salle.getNom());
            stmt.setInt(2, salle.getCapacite());
            stmt.setString(3, salle.getType().name());
            stmt.setInt(4, salle.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ➤ Supprimer une salle
    public boolean supprimerSalle(int id) {
        String sql = "DELETE FROM salle WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    //  Lister les salles par Type
    public List<Salle> listerSallesParType(TypeSalle typeRecherche) {
        List<Salle> salles = new ArrayList<>();
        String sql = "SELECT * FROM salle WHERE type = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Conversion de l'Enum en String pour la requête SQL
            stmt.setString(1, typeRecherche.name());
            
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Salle salle = new Salle(
                        rs.getString("nom"),
                        rs.getInt("capacite"),
                        TypeSalle.valueOf(rs.getString("type"))
                );
                salle.setId(rs.getInt("id"));
                salles.add(salle);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salles;
    }

    // Vérifier la disponibilité d'une salle
public boolean verifierDisponibiliteSalle(int salleId, LocalDateTime dateHeure) {
    String sql = "SELECT COUNT(*) FROM seance WHERE salle_id = ? AND dateHeure = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, salleId);
        stmt.setTimestamp(2, java.sql.Timestamp.valueOf(dateHeure));

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getInt(1) == 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

}