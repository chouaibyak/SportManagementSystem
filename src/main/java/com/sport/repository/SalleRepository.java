package com.sport.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sport.model.Salle;
import com.sport.model.TypeSalle;

public class SalleRepository {

    private final Connection connection;

    public SalleRepository(Connection connection) {
        this.connection = connection;
    }

    // ➤ Ajouter une salle
    public void ajouter(Salle salle) {
        String sql = "INSERT INTO salle (nom, capacite, type) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
    public List<Salle> getAll() {
        List<Salle> salles = new ArrayList<>();
        String sql = "SELECT * FROM salle";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
    public Salle getById(int id) {
        String sql = "SELECT * FROM salle WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
    public boolean update(Salle salle) {
        String sql = "UPDATE salle SET nom = ?, capacite = ?, type = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
    public boolean delete(int id) {
        String sql = "DELETE FROM salle WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
