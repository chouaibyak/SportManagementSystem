package com.sport.repository;

import com.sport.model.Coach;
import com.sport.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoachRepository {

    // ➤ LECTURE : Trouver par ID (AVEC JOINTURE)
  public Coach getCoachById(int id) {
    String sql = "SELECT * FROM COACH c " +
                 "JOIN UTILISATEUR u ON c.id_utilisateur = u.id " +
                 "WHERE c.id_utilisateur = ?";

    String sqlSpec = "SELECT specialite FROM COACH_SPECIALITE WHERE coach_id = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            Coach coach = mapResultSetToCoach(rs); // نستعمل mapper واحد

            // récupération des spécialités
            try (PreparedStatement stmtSpec = conn.prepareStatement(sqlSpec)) {
                stmtSpec.setInt(1, id);
                ResultSet rsSpec = stmtSpec.executeQuery();
                List<String> specs = new ArrayList<>();
                while (rsSpec.next()) {
                    specs.add(rsSpec.getString("specialite"));
                }
                coach.setSpecialites(specs);
            }

            return coach;
        }

    } catch (SQLException e) {
        System.out.println("Erreur findById coach : " + e.getMessage());
    }

    return null;
}

    // ➤ LECTURE : Lister tout (AVEC JOINTURE)
  public List<Coach> listerCoachs() {
    List<Coach> coaches = new ArrayList<>();

    String sql =
        "SELECT * FROM COACH c " +
        "JOIN UTILISATEUR u ON c.id_utilisateur = u.id";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            coaches.add(mapResultSetToCoach(rs));
        }

    } catch (SQLException e) {
        System.out.println("Erreur récupération des coachs : " + e.getMessage());
    }

    return coaches;
}





    // ➤ AJOUT (Reste similaire, assurez-vous que l'Utilisateur existe déjà)
    public void ajouterCoach(Coach coach) {

    String sqlUser = """
        INSERT INTO UTILISATEUR
        (nom, prenom, dateNaissance, email, telephone, adresse)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

    String sqlCoach = "INSERT INTO COACH (id_utilisateur) VALUES (?)";

    Connection conn = null;

    try {
        conn = DBConnection.getConnection();
        conn.setAutoCommit(false); // 🔒 TRANSACTION

        int userId = -1;

        // ------------------------------------------------
        // 1️⃣ INSERT UTILISATEUR
        // ------------------------------------------------
        try (PreparedStatement stmtUser =
                     conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {

            stmtUser.setString(1, coach.getNom());
            stmtUser.setString(2, coach.getPrenom());
            stmtUser.setString(3, coach.getDateNaissance());
            stmtUser.setString(4, coach.getEmail());
            stmtUser.setString(5, coach.getTelephone());
            stmtUser.setString(6, coach.getAdresse());

            stmtUser.executeUpdate();

            ResultSet rs = stmtUser.getGeneratedKeys();
            if (rs.next()) {
                userId = rs.getInt(1);
                coach.setId(userId); // 🔑 héritage
            }
        }

        // ------------------------------------------------
        // 2️⃣ INSERT COACH
        // ------------------------------------------------
        if (userId != -1) {
            try (PreparedStatement stmtCoach =
                         conn.prepareStatement(sqlCoach)) {

                stmtCoach.setInt(1, userId);
                stmtCoach.executeUpdate();
            }
        }

        conn.commit();
        System.out.println("Coach ajouté avec succès !");

    } catch (SQLException e) {
        if (conn != null)
            try { conn.rollback(); } catch (SQLException ignored) {}

        System.out.println("Erreur ajout coach : " + e.getMessage());

    } finally {
        if (conn != null)
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ignored) {}
    }
}



   
   // ➤ MODIFICATION (Transaction complète)
public void modifierCoach(Coach coach) {
    String sqlUpdateUtilisateur = "UPDATE UTILISATEUR SET nom = ?, prenom = ?, dateNaissance = ?, email = ?, telephone = ?, adresse = ? WHERE id = ?";
    String sqlDeleteSpec = "DELETE FROM COACH_SPECIALITE WHERE coach_id = ?";
    String sqlInsertSpec = "INSERT INTO COACH_SPECIALITE (coach_id, specialite) VALUES (?, ?)";

    try (Connection conn = DBConnection.getConnection()) {
        conn.setAutoCommit(false);


        try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateUtilisateur)) {
            stmt.setString(1, coach.getNom());
            stmt.setString(2, coach.getPrenom());
            stmt.setString(3, coach.getDateNaissance());
            stmt.setString(4, coach.getEmail());
            stmt.setString(5, coach.getTelephone());
            stmt.setString(6, coach.getAdresse());
            stmt.setInt(7, coach.getId());
            stmt.executeUpdate();
        }

       
        try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteSpec)) {
            stmt.setInt(1, coach.getId());
            stmt.executeUpdate();
        }

        if (coach.getSpecialites() != null) {
            try (PreparedStatement stmt = conn.prepareStatement(sqlInsertSpec)) {
                for (String spec : coach.getSpecialites()) {
                    stmt.setInt(1, coach.getId());
                    stmt.setString(2, spec);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }

        conn.commit();
        System.out.println("Coach modifié avec succès !");
    } catch (SQLException e) {
        System.out.println("Erreur modification coach : " + e.getMessage());
    }
}

// ➤ SUPPRESSION
public void supprimerCoach(int id) {
    String sqlDeleteSpec = "DELETE FROM COACH_SPECIALITE WHERE coach_id = ?";
    String sqlDeleteCoach = "DELETE FROM COACH WHERE id_utilisateur = ?";
    String sqlDeleteUtilisateur = "DELETE FROM UTILISATEUR WHERE id = ?";

    try (Connection conn = DBConnection.getConnection()) {
        conn.setAutoCommit(false);

     
        try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteSpec)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }

      
        try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteCoach)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }

    
        try (PreparedStatement stmt = conn.prepareStatement(sqlDeleteUtilisateur)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }

        conn.commit();
        System.out.println("Coach supprimé avec succès !");
    } catch (SQLException e) {
        System.out.println("Erreur suppression coach : " + e.getMessage());
    }
}


   


    //Mapping
    private Coach mapResultSetToCoach(ResultSet rs) throws SQLException {
    Coach coach = new Coach();

    coach.setId(rs.getInt("id")); // id dyal UTILISATEUR
    coach.setNom(rs.getString("nom"));
    coach.setPrenom(rs.getString("prenom"));
    coach.setDateNaissance(rs.getString("dateNaissance"));
    coach.setEmail(rs.getString("email"));
    coach.setTelephone(rs.getString("telephone"));
    coach.setAdresse(rs.getString("adresse"));

    return coach;
}

}
