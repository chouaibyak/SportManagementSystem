package com.sport.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sport.model.Equipement;
import com.sport.model.EtatEquipement;
import com.sport.model.TypeEquipement;

public class EquipementRepository {

    private Connection connection;

    // --- Constructeur (Connexion JDBC MySQL) ---
    public EquipementRepository() {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/sportdb",
                    "root",
                    "password"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Ajouter un équipement ---
    public void ajouter(Equipement equipement) {
        String query = "INSERT INTO equipements (nom, type, etat, date_achat) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, equipement.getNom());
            stmt.setString(2, equipement.getType().name());
            stmt.setString(3, equipement.getEtat().name());
            stmt.setDate(4, new java.sql.Date(equipement.getDateAchat().getTime()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Trouver par ID ---
    public Equipement getById(int id) {
        String query = "SELECT * FROM equipements WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                String nom = rs.getString("nom");
                TypeEquipement type = TypeEquipement.valueOf(rs.getString("type"));
                EtatEquipement etat = EtatEquipement.valueOf(rs.getString("etat"));
                Date dateAchat = new Date(rs.getDate("date_achat").getTime());

                Equipement eq = new Equipement();
                eq.setId(id);

                return eq;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- Lister tous les équipements ---
    public List<Equipement> getAll() {
        List<Equipement> list = new ArrayList<>();

        String query = "SELECT * FROM equipements";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                TypeEquipement type = TypeEquipement.valueOf(rs.getString("type"));
                EtatEquipement etat = EtatEquipement.valueOf(rs.getString("etat"));
                Date dateAchat = new Date(rs.getDate("date_achat").getTime());

                Equipement eq = new Equipement();
                eq.setId(id);

                list.add(eq);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // --- Supprimer un équipement ---
    public boolean delete(int id) {
        String query = "DELETE FROM equipements WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Mettre à jour un équipement ---
    public boolean update(Equipement equipement) {
        String query = "UPDATE equipements SET nom = ?, type = ?, etat = ?, date_achat = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, equipement.getNom());
            stmt.setString(2, equipement.getType().name());
            stmt.setString(3, equipement.getEtat().name());
            stmt.setDate(4, new java.sql.Date(equipement.getDateAchat().getTime()));
            stmt.setInt(5, equipement.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
