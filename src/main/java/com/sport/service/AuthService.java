package com.sport.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sport.model.Administrateur;
import com.sport.model.Coach;
import com.sport.model.Membre;
import com.sport.model.Utilisateur;
import com.sport.utils.DBConnection;

public class AuthService {

    /**
     * Authentifie un utilisateur et renvoie l'OBJET complet.
     */
    public Utilisateur login(String email, String password) {
        String sql = "SELECT id, nom, prenom, mot_de_passe FROM utilisateur WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String dbPass = rs.getString("mot_de_passe");
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");

                if (password.equals(dbPass)) {
                    return recupereUtilisateurSelonRole(conn, id, nom, prenom, email);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }

    private Utilisateur recupereUtilisateurSelonRole(Connection conn, int id, String nom, String prenom, String email) throws SQLException {
        if (checkTable(conn, "membre", id)) {
            Membre m = new Membre();
            m.setId(id); m.setNom(nom); m.setPrenom(prenom); m.setEmail(email);
            return m;
        }
        if (checkTable(conn, "coach", id)) {
            Coach c = new Coach();
            c.setId(id); c.setNom(nom); c.setPrenom(prenom); c.setEmail(email);
            return c;
        }
        if (checkTable(conn, "administrateur", id)) {
            Administrateur a = new Administrateur();
            a.setId(id); a.setNom(nom); a.setPrenom(prenom); a.setEmail(email);
            return a;
        }
        return null;
    }

    private boolean checkTable(Connection conn, String tableName, int userId) throws SQLException {
        String sql = "SELECT 1 FROM " + tableName + " WHERE id_utilisateur = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeQuery().next();
        }
    }
}