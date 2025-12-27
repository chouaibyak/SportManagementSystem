package com.sport.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sport.model.Membre;
import com.sport.model.TypeObjectif;
import com.sport.model.TypePreference;
import com.sport.utils.DBConnection;

public class MembreRepository {

    // CREATE (Reste inchangé car l'INSERT ne nomme pas l'ID utilisateur explicitement)
    public void ajouterMembre(Membre membre) {
        String sqlUser = "INSERT INTO UTILISATEUR (nom, prenom, dateNaissance, email, telephone, adresse, mot_de_passe) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlMembre = "INSERT INTO MEMBRE (id_utilisateur, objectifSportif, preferences) VALUES (?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); 

            int userId = -1;
            try (PreparedStatement stmtUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                stmtUser.setString(1, membre.getNom());
                stmtUser.setString(2, membre.getPrenom());
                stmtUser.setString(3, membre.getDateNaissance()!= null ? membre.getDateNaissance():"2000-01-01"); 
                stmtUser.setString(4, membre.getEmail());
                stmtUser.setString(5, membre.getTelephone());
                stmtUser.setString(6, membre.getAdresse());
                stmtUser.setString(7, membre.getMotDePasse());
                stmtUser.executeUpdate();

                ResultSet rs = stmtUser.getGeneratedKeys();
                if (rs.next()) {
                    userId = rs.getInt(1);
                    membre.setId(userId); 
                }
            }

            if (userId != -1) {
                try (PreparedStatement stmtMembre = conn.prepareStatement(sqlMembre)) {
                    stmtMembre.setInt(1, userId);
                    stmtMembre.setString(2, membre.getObjectifSportif() != null ? membre.getObjectifSportif().name() : null);
                    stmtMembre.setString(3, membre.getPreferences() != null ? membre.getPreferences().name() : null);
                    stmtMembre.executeUpdate();
                }
            }

            conn.commit();
            System.out.println("Membre ajouté avec succès !");

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            System.out.println("Erreur ajout membre : " + e.getMessage());
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    // ➤ READ (Tout) - CORRECTION ICI
    public List<Membre> listerMembres() {
        List<Membre> membres = new ArrayList<>();
        // CORRECTION : u.id au lieu de u.id_utilisateur
        String sql = "SELECT * FROM MEMBRE m JOIN UTILISATEUR u ON m.id_utilisateur = u.id";

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

    // ➤ READ (Par ID) - CORRECTION ICI
    public Membre trouverParId(int id) {
        // CORRECTION : u.id au lieu de u.id_utilisateur
        String sql = "SELECT * FROM MEMBRE m JOIN UTILISATEUR u ON m.id_utilisateur = u.id WHERE m.id_utilisateur = ?";

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

    // ➤ UPDATE - CORRECTION ICI
    public void modifierMembre(Membre membre) {
        // CORRECTION : WHERE id=? (car dans la table utilisateur c'est 'id')
        String sqlUser = "UPDATE UTILISATEUR SET nom=?, prenom=?, email=?, telephone=?, adresse=? WHERE id=?";
        String sqlMembre = "UPDATE MEMBRE SET objectifSportif=?, preferences=? WHERE id_utilisateur=?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtUser = conn.prepareStatement(sqlUser)) {
                stmtUser.setString(1, membre.getNom());
                stmtUser.setString(2, membre.getPrenom());
                stmtUser.setString(3, membre.getEmail());
                stmtUser.setString(4, membre.getTelephone());
                stmtUser.setString(5, membre.getAdresse());
                stmtUser.setInt(6, membre.getId()); // C'est bien l'ID qu'on passe ici
                stmtUser.executeUpdate();
            }

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

    // ➤ DELETE - CORRECTION ICI
    public void supprimerMembre(int membreId) {
        String sqlMembre = "DELETE FROM MEMBRE WHERE id_utilisateur = ?";
        // CORRECTION : WHERE id = ?
        String sqlUser = "DELETE FROM UTILISATEUR WHERE id = ?";

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

    // MAPPING 
    private Membre mapResultSetToMembre(ResultSet rs) throws SQLException {
        Membre m = new Membre();
        m.setId(rs.getInt("id")); 

        m.setNom(rs.getString("nom"));
        m.setPrenom(rs.getString("prenom"));
        m.setEmail(rs.getString("email"));
        m.setTelephone(rs.getString("telephone"));
        m.setAdresse(rs.getString("adresse"));
        m.setDateNaissance(String.valueOf(rs.getDate("dateNaissance"))); // Conversion Date -> String

        String objectif = rs.getString("objectifSportif");
        if (objectif != null) m.setObjectifSportif(TypeObjectif.valueOf(objectif));

        String pref = rs.getString("preferences");
        if (pref != null) m.setPreferences(TypePreference.valueOf(pref));

        return m;
    }
}

