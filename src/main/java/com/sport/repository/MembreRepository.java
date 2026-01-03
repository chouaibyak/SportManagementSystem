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

    // ➤ CREATE
    public void ajouterMembre(Membre membre) {
        String sqlUser = "INSERT INTO UTILISATEUR (nom, prenom, dateNaissance, email, telephone, adresse, mot_de_passe) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlMembre = "INSERT INTO MEMBRE (id_utilisateur, objectifSportif, preferences, date_inscription ) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); 

            int userId = -1;
            try (PreparedStatement stmtUser = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                stmtUser.setString(1, membre.getNom());
                stmtUser.setString(2, membre.getPrenom());
                stmtUser.setString(3, membre.getDateNaissance() != null ? membre.getDateNaissance() : "2000-01-01"); 
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
                    stmtMembre.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
                    stmtMembre.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    // ➤ LISTER TOUS LES MEMBRES
    public List<Membre> listerMembres() {
        List<Membre> membres = new ArrayList<>();
        // On sélectionne explicitement u.id pour éviter les confusions
        String sql = "SELECT u.id as user_id, u.*, m.* FROM MEMBRE m JOIN UTILISATEUR u ON m.id_utilisateur = u.id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Membre m = mapResultSetToMembre(rs);
                if (m != null) membres.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return membres;
    }

    // ➤ TROUVER PAR ID (CRUCIAL POUR VOTRE PROBLÈME)
    public Membre trouverParId(int id) {
        String sql = "SELECT u.id as user_id, u.*, m.* FROM MEMBRE m JOIN UTILISATEUR u ON m.id_utilisateur = u.id WHERE m.id_utilisateur = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMembre(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL trouverParId : " + e.getMessage());
        }
        return null;
    }

    // ➤ UPDATE
    public void modifierMembre(Membre membre) {
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
                stmtUser.setInt(6, membre.getId());
                stmtUser.executeUpdate();
            }

            try (PreparedStatement stmtMembre = conn.prepareStatement(sqlMembre)) {
                stmtMembre.setString(1, membre.getObjectifSportif() != null ? membre.getObjectifSportif().name() : null);
                stmtMembre.setString(2, membre.getPreferences() != null ? membre.getPreferences().name() : null);
                stmtMembre.setInt(3, membre.getId());
                stmtMembre.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    // ➤ DELETE
    public void supprimerMembre(int membreId) {
        String sqlMembre = "DELETE FROM MEMBRE WHERE id_utilisateur = ?";
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
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    // ➤ MAPPING SÉCURISÉ (C'EST ICI QUE ÇA PLANTAIT SOUVENT)
    private Membre mapResultSetToMembre(ResultSet rs) throws SQLException {
        Membre m = new Membre();
        
        // Utilisation de l'alias 'user_id' pour être sûr de prendre le bon ID
        try {
            m.setId(rs.getInt("user_id"));
        } catch (SQLException e) {
            // Fallback si l'alias n'existe pas (ex: select *)
            m.setId(rs.getInt("id"));
        }

        m.setNom(rs.getString("nom"));
        m.setPrenom(rs.getString("prenom"));
        m.setEmail(rs.getString("email"));
        m.setTelephone(rs.getString("telephone"));
        m.setAdresse(rs.getString("adresse"));
        
        // Gestion sécurisée de la date
        if (rs.getDate("dateNaissance") != null) {
            m.setDateNaissance(rs.getDate("dateNaissance").toString());
        } else {
            m.setDateNaissance("");
        }
        
        // ➤ AJOUTER CECI : Récupération de la date d'inscription
        try {
            java.sql.Date dateSql = rs.getDate("date_inscription");
            if (dateSql != null) {
                m.setDateInscription(dateSql.toLocalDate());
            }
        } catch (SQLException e) {
            System.out.println("Info : date_inscription non trouvée dans ce ResultSet (ce n'est pas grave)");
        }

        // Gestion sécurisée des Enums (try-catch pour éviter que tout plante si l'enum change)
        try {
            String objectif = rs.getString("objectifSportif");
            if (objectif != null && !objectif.isEmpty()) {
                m.setObjectifSportif(TypeObjectif.valueOf(objectif));
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur Enum Objectif pour le membre ID " + m.getId() + " : " + e.getMessage());
        }

        try {
            String pref = rs.getString("preferences");
            if (pref != null && !pref.isEmpty()) {
                m.setPreferences(TypePreference.valueOf(pref));
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur Enum Preferences pour le membre ID " + m.getId() + " : " + e.getMessage());
        }

        return m;
    }

    public List<Membre> trouverParSeanceCollective(int seanceId) {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT u.id as user_id, u.*, m.* " +
                     "FROM seancecollective_membre scm " +
                     "JOIN membre m ON scm.membre_id = m.id_utilisateur " +
                     "JOIN utilisateur u ON m.id_utilisateur = u.id " +
                     "WHERE scm.seance_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, seanceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    membres.add(mapResultSetToMembre(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return membres;
    }
}