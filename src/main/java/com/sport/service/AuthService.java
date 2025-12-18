package com.sport.service;

import com.sport.utils.DBConnection;
import com.sport.model.Membre;
import com.sport.model.Coach;
import com.sport.model.Administrateur;
import com.sport.model.Utilisateur;

import java.sql.*;

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

    // ---------------------------------------------------------
    // SUPPRESSION DE registerMembre() 
    // On l'a supprimé car l'inscription Membre se fait maintenant 
    // via MembreService -> MembreRepository dans l'étape 2.
    // ---------------------------------------------------------

    // --- INSCRIPTION COACH (On le garde car le coach s'inscrit en une seule fois) ---
    public boolean registerCoach(Coach c, String password) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // On insère l'utilisateur
            int userId = insertUtilisateur(conn, c, password);

            // On insère le coach
            String sqlCoach = "INSERT INTO coach (id_utilisateur) VALUES (?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlCoach)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }
            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    // Méthode utilitaire privée (utilisée seulement par registerCoach ici)
    private int insertUtilisateur(Connection conn, Utilisateur u, String password) throws SQLException {
        String sql = "INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, telephone, adresse, dateNaissance) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, u.getNom());
            stmt.setString(2, u.getPrenom());
            stmt.setString(3, u.getEmail());
            stmt.setString(4, password);
            // Pour le Coach à l'inscription rapide, tel et adresse peuvent être null
            stmt.setString(5, u.getTelephone()); 
            stmt.setString(6, u.getAdresse());
            // Valeur par défaut pour le coach si on ne demande pas sa date
            stmt.setString(7, "1990-01-01"); 
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Échec de création utilisateur, pas d'ID obtenu.");
            }
        }
    }
}