package com.sport.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sport.model.Equipement;
import com.sport.model.EtatEquipement;
import com.sport.model.TypeEquipement;
import com.sport.utils.DBConnection;

public class EquipementRepository {

    // --- Ajouter un équipement ---
    public void ajouterEquipement(Equipement equipement) {
        String query = "INSERT INTO equipements (nom, type, etat, date_achat) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
        
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
    public Equipement getEquipementById(int id) {
        String query = "SELECT * FROM equipements WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {

                String nom = rs.getString("nom");
                TypeEquipement type = TypeEquipement.valueOf(rs.getString("type"));
                EtatEquipement etat = EtatEquipement.valueOf(rs.getString("etat"));
                Date dateAchat = new Date(rs.getDate("date_achat").getTime());

                Equipement eq = new Equipement(nom, type, etat, dateAchat);
                eq.setId(id);

                return eq;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- Lister tous les équipements ---
    public List<Equipement> listerEquipements() {
        List<Equipement> list = new ArrayList<>();

        String query = "SELECT * FROM equipements";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                TypeEquipement type = TypeEquipement.valueOf(rs.getString("type"));
                EtatEquipement etat = EtatEquipement.valueOf(rs.getString("etat"));
                Date dateAchat = new Date(rs.getDate("date_achat").getTime());

                Equipement eq = new Equipement(nom, type, etat, dateAchat);
                eq.setId(id);

                list.add(eq);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // --- Supprimer un équipement ---
    public boolean supprimerEquipement(int id) {
        String query = "DELETE FROM equipements WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Mettre à jour un équipement ---
    public boolean modifierEquipement(Equipement equipement) {
        String query = "UPDATE equipements SET nom = ?, type = ?, etat = ?, date_achat = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
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
    // --- Lister les équipements par état (ex: EN_MAINTENANCE) ---
    public List<Equipement> listerEquipementsParEtat(EtatEquipement etatRecherche) {
        List<Equipement> list = new ArrayList<>();
        String query = "SELECT * FROM equipements WHERE etat = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Conversion de l'Enum en String pour la requête SQL
            stmt.setString(1, etatRecherche.name());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                
                // Conversion String BDD -> Enum Java
                TypeEquipement type = TypeEquipement.valueOf(rs.getString("type"));
                EtatEquipement etat = EtatEquipement.valueOf(rs.getString("etat"));
                
                Date dateAchat = new Date(rs.getDate("date_achat").getTime());

                Equipement eq = new Equipement(nom, type, etat, dateAchat);
                eq.setId(id);

                list.add(eq);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ou logger l'erreur
        }

        return list;
    }
}
