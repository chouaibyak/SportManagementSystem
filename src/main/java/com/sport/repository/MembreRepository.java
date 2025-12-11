package com.sport.repository;

import com.sport.model.Membre;
import com.sport.model.TypeObjectif;
import com.sport.model.TypePreference;
import com.sport.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MembreRepository {

    // ➤ CREATE (Transaction : Utilisateur + Membre)
    public void ajouterMembre(Membre membre) {
        String sqlUser = "INSERT INTO UTILISATEUR (nom, prenom, dateNaissance, email, telephone, adresse) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlMembre = "INSERT INTO MEMBRE (id_utilisateur, objectifSportif, preferences) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Début Transaction

            // 1. Insertion dans UTILISATEUR
            int userId = -1;
                   try (PreparedStatement stmtUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                
                // Il faut utiliser "stmtUser" partout !
                stmtUser.setString(1, membre.getNom());
                stmtUser.setString(2, membre.getPrenom());
                
                stmtUser.setString(3, membre.getDateNaissance()); 
                stmtUser.setString(4, membre.getEmail());
                stmtUser.setString(5, membre.getTelephone());
                stmtUser.setString(6, membre.getAdresse());
                
                stmtUser.executeUpdate();

                ResultSet rs = stmtUser.getGeneratedKeys();
                if (rs.next()) {
                    userId = rs.getInt(1);
                    membre.setId(userId); 
                }
            }

            // 2. Insertion dans MEMBRE
            if (userId != -1) {
                try (PreparedStatement stmtMembre = conn.prepareStatement(sqlMembre)) {
                    stmtMembre.setInt(1, userId);
                    stmtMembre.setString(2, membre.getObjectifSportif() != null ? membre.getObjectifSportif().name() : null);
                    stmtMembre.setString(3, membre.getPreferences() != null ? membre.getPreferences().name() : null);
                    stmtMembre.executeUpdate();
                }
            }

            conn.commit(); // Validation
            System.out.println("Membre ajouté avec succès !");

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            System.out.println("Erreur ajout membre : " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    // READ (Tout) - AVEC JOINTURE
    public List<Membre> listerMembres() {
        List<Membre> membres = new ArrayList<>();
        // JOINTURE OBLIGATOIRE pour avoir le nom et le prénom
        String sql = "SELECT * FROM MEMBRE m JOIN UTILISATEUR u ON m.id_utilisateur = u.id_utilisateur";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                membres.add(mapResultSetToMembre(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur récupération des membres : " + e.getMessage());
        }
        return membres;
    }

    // ➤ READ (Par ID) - AVEC JOINTURE
    public Membre trouverParId(int id) {
        String sql = "SELECT * FROM MEMBRE m JOIN UTILISATEUR u ON m.id_utilisateur = u.id_utilisateur WHERE m.id_utilisateur = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMembre(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur recherche membre : " + e.getMessage());
        }
        return null;
    }

    // ➤ UPDATE (Transaction : Utilisateur + Membre)
    public void modifierMembre(Membre membre) {
        String sqlUser = "UPDATE UTILISATEUR SET nom=?, prenom=?, email=?, telephone=?, adresse=? WHERE id_utilisateur=?";
        String sqlMembre = "UPDATE MEMBRE SET objectifSportif=?, preferences=? WHERE id_utilisateur=?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Update infos personnelles
            try (PreparedStatement stmtUser = conn.prepareStatement(sqlUser)) {
                stmtUser.setString(1, membre.getNom());
                stmtUser.setString(2, membre.getPrenom());
                stmtUser.setString(3, membre.getEmail());
                stmtUser.setString(4, membre.getTelephone());
                stmtUser.setString(5, membre.getAdresse());
                stmtUser.setInt(6, membre.getId());
                stmtUser.executeUpdate();
            }

            // 2. Update infos membre
            try (PreparedStatement stmtMembre = conn.prepareStatement(sqlMembre)) {
                stmtMembre.setString(1, membre.getObjectifSportif() != null ? membre.getObjectifSportif().name() : null);
                stmtMembre.setString(2, membre.getPreferences() != null ? membre.getPreferences().name() : null);
                stmtMembre.setInt(3, membre.getId());
                stmtMembre.executeUpdate();
            }

            conn.commit();
            System.out.println("Membre modifié avec succès !");

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            System.out.println("Erreur modification membre : " + e.getMessage());
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    // ➤ DELETE
    public void supprimerMembre(int membreId) {
        // En supprimant l'utilisateur, la contrainte "ON DELETE CASCADE" (si configurée en SQL) 
        // devrait supprimer le membre. Sinon, il faut supprimer Membre d'abord, puis Utilisateur.
        String sqlMembre = "DELETE FROM MEMBRE WHERE id_utilisateur = ?";
        String sqlUser = "DELETE FROM UTILISATEUR WHERE id_utilisateur = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sqlMembre)) {
                stmt.setInt(1, membreId);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = conn.prepareStatement(sqlUser)) {
                stmt.setInt(1, membreId);
                stmt.executeUpdate();
            }

            conn.commit();
            System.out.println("Membre supprimé !");

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            System.out.println("Erreur suppression membre : " + e.getMessage());
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    // ➤ MAPPING (Doit mapper les deux tables)
    private Membre mapResultSetToMembre(ResultSet rs) throws SQLException {
        Membre m = new Membre();
        m.setId(rs.getInt("id_utilisateur"));

        // Données de la table UTILISATEUR
        m.setNom(rs.getString("nom"));
        m.setPrenom(rs.getString("prenom"));
        m.setEmail(rs.getString("email"));
        m.setTelephone(rs.getString("telephone"));
        m.setAdresse(rs.getString("adresse"));
        // m.setDateNaissance(...) si besoin

        // Données de la table MEMBRE
        String objectif = rs.getString("objectifSportif");
        if (objectif != null) m.setObjectifSportif(TypeObjectif.valueOf(objectif));

        String pref = rs.getString("preferences");
        if (pref != null) m.setPreferences(TypePreference.valueOf(pref));

        return m;
    }
}