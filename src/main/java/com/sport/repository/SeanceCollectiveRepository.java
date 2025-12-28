package com.sport.repository;

import com.sport.model.*;
import com.sport.utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SeanceCollectiveRepository {

    // GET ALL
  public List<SeanceCollective> getAll() {
    List<SeanceCollective> list = new ArrayList<>();
    String query = "SELECT s.id, s.nom, s.capaciteMax, s.dateHeure, s.type AS typeCours, s.typeSeance, s.duree, s.salle_id, s.entraineur_id, " +
                   "sc.placesDisponibles " +
                   "FROM seance s " +
                   "JOIN seancecollective sc ON s.id = sc.seance_id";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            SeanceCollective sc = new SeanceCollective();

            sc.setId(rs.getInt("id"));
            sc.setNom(rs.getString("nom"));
            sc.setCapaciteMax(rs.getInt("capaciteMax"));
            sc.setDateHeure(rs.getTimestamp("dateHeure").toLocalDateTime());
            sc.setDuree(rs.getInt("duree"));
            sc.setTypeCours(TypeCours.valueOf(rs.getString("typeCours")));
            sc.setTypeSeance(TypeSeance.valueOf(rs.getString("typeSeance")));
            sc.setPlacesDisponibles(rs.getInt("placesDisponibles"));

            // Salle
            Salle salle = new Salle();
            salle.setId(rs.getInt("salle_id"));
            sc.setSalle(salle);

            // Coach
            Coach coach = new Coach();
            coach.setId(rs.getInt("entraineur_id"));
            sc.setEntraineur(coach);

            // **⚡ Charger les membres suivis**

            list.add(sc);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}
    public List<Membre> getMembresParSeance(int seanceId) {
    List<Membre> membres = new ArrayList<>();
    String sql = "SELECT u.* " +
                 "FROM seancecollective_membre scm " +
                 "JOIN membre m ON scm.membre_id = m.id_utilisateur " +
                 "JOIN utilisateur u ON m.id_utilisateur = u.id " +
                 "WHERE scm.seance_id = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, seanceId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Membre m = new Membre();
            m.setId(rs.getInt("id"));
            m.setNom(rs.getString("nom"));
            m.setPrenom(rs.getString("prenom"));
            m.setEmail(rs.getString("email"));
            m.setTelephone(rs.getString("telephone"));
            m.setAdresse(rs.getString("adresse"));
            m.setDateNaissance(rs.getString("dateNaissance"));
            membres.add(m);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return membres;
}


    // GET BY ID
    // Dans SeanceCollectiveRepository.java

    public SeanceCollective getById(int id) {
        SeanceCollective sc = null;
        String query = "SELECT s.id, s.nom, s.capaciteMax, s.dateHeure, s.type AS typeCours, s.typeSeance, s.duree, s.salle_id, s.entraineur_id, " +
                    "sc.placesDisponibles " +
                    "FROM seance s " +
                    "JOIN seancecollective sc ON s.id = sc.seance_id " +
                    "WHERE s.id = ?"; // <--- On filtre directement ici

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    sc = new SeanceCollective();
                    sc.setId(rs.getInt("id"));
                    sc.setNom(rs.getString("nom"));
                    sc.setCapaciteMax(rs.getInt("capaciteMax"));
                    sc.setDateHeure(rs.getTimestamp("dateHeure").toLocalDateTime());
                    sc.setDuree(rs.getInt("duree"));
                    
                    // Gestion sécurisée des Enums
                    try {
                        sc.setTypeCours(TypeCours.valueOf(rs.getString("typeCours")));
                        sc.setTypeSeance(TypeSeance.valueOf(rs.getString("typeSeance")));
                    } catch (Exception e) {
                        System.err.println("Erreur conversion Enum pour ID " + id);
                    }
                    
                    sc.setPlacesDisponibles(rs.getInt("placesDisponibles"));

                    // Objets partiels pour éviter NullPointerException
                    Salle salle = new Salle();
                    salle.setId(rs.getInt("salle_id"));
                    sc.setSalle(salle);

                    Coach coach = new Coach();
                    coach.setId(rs.getInt("entraineur_id"));
                    sc.setEntraineur(coach);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL dans getById : " + e.getMessage());
            e.printStackTrace();
        }

        if (sc == null) {
            System.err.println("ATTENTION: Aucune séance collective trouvée avec l'ID " + id + " dans la BDD.");
        }
        
        return sc;
    }

    // ADD
    public int ajouter(SeanceCollective sc) {
        String insertSeance = "INSERT INTO seance (nom, capaciteMax, salle_id, dateHeure, entraineur_id, type, typeSeance, duree) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertCollective = "INSERT INTO seancecollective (seance_id, placesDisponibles) VALUES (?, ?)";
        int generatedId = -1;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(insertSeance, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement stmt2 = conn.prepareStatement(insertCollective)) {

                stmt1.setString(1, sc.getNom());
                stmt1.setInt(2, sc.getCapaciteMax());
                stmt1.setInt(3, sc.getSalle().getId());
                stmt1.setTimestamp(4, Timestamp.valueOf(sc.getDateHeure()));
                stmt1.setInt(5, sc.getEntraineur().getId());
                stmt1.setString(6, sc.getTypeCours().toString()); // YOGA, MUSCULATION...
                stmt1.setString(7, sc.getTypeSeance().toString()); // COLLECTIVE
                stmt1.setInt(8, sc.getDuree());

                stmt1.executeUpdate();

                ResultSet rs = stmt1.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    sc.setId(generatedId);
                }

                stmt2.setInt(1, generatedId);
                stmt2.setInt(2, sc.getPlacesDisponibles());
                stmt2.executeUpdate();

                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedId;
    }

    // UPDATE
    public boolean update(SeanceCollective sc) {
        String updateSeance = "UPDATE seance SET nom = ?, capaciteMax = ?, salle_id = ?, dateHeure = ?, type = ?, typeSeance = ?, duree = ? WHERE id = ?";
        String updateCollective = "UPDATE seancecollective SET placesDisponibles = ? WHERE seance_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(updateSeance);
                 PreparedStatement stmt2 = conn.prepareStatement(updateCollective)) {

                stmt1.setString(1, sc.getNom());
                stmt1.setInt(2, sc.getCapaciteMax());
                stmt1.setInt(3, sc.getSalle().getId());
                stmt1.setTimestamp(4, Timestamp.valueOf(sc.getDateHeure()));
                stmt1.setString(5, sc.getTypeCours().toString());
                stmt1.setString(6, sc.getTypeSeance().toString());
                stmt1.setInt(7, sc.getDuree());
                stmt1.setInt(8, sc.getId());

                stmt1.executeUpdate();

                stmt2.setInt(1, sc.getPlacesDisponibles());
                stmt2.setInt(2, sc.getId());
                stmt2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE
    public boolean delete(int seanceId) {
        String deleteCollective = "DELETE FROM seancecollective WHERE seance_id = ?";
        String deleteSeance = "DELETE FROM seance WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(deleteCollective);
                 PreparedStatement stmt2 = conn.prepareStatement(deleteSeance)) {

                stmt1.setInt(1, seanceId);
                stmt1.executeUpdate();

                stmt2.setInt(1, seanceId);
                stmt2.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //reserver place
    public boolean reserverPlace(int idSeance, Membre membre) {
        Connection conn = null;
        PreparedStatement stmtUpdate = null;
        PreparedStatement stmtInsert = null;

        System.out.println("--- DÉBUT RÉSERVATION ---");
        System.out.println("Seance ID: " + idSeance);
        System.out.println("Membre ID: " + membre.getId());

        // Vérification ID Membre
        if (membre.getId() == 0) {
            System.err.println("ERREUR: L'ID du membre est 0 ! Vérifiez UserSession.");
            return false;
        }

        String sqlUpdatePlaces = "UPDATE seancecollective SET placesDisponibles = placesDisponibles - 1 WHERE seance_id = ? AND placesDisponibles > 0";
        String sqlInsertLink = "INSERT INTO seancecollective_membre (seance_id, membre_id) VALUES (?, ?)";

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); 

            // 1. Décrémenter place
            stmtUpdate = conn.prepareStatement(sqlUpdatePlaces);
            stmtUpdate.setInt(1, idSeance);
            int rowsUpdated = stmtUpdate.executeUpdate();
            System.out.println("Places mises à jour : " + rowsUpdated);

            if (rowsUpdated == 0) {
                System.err.println("ERREUR: Plus de places disponibles ou ID séance incorrect.");
                conn.rollback();
                return false; 
            }

            // 2. Lier membre
            stmtInsert = conn.prepareStatement(sqlInsertLink);
            stmtInsert.setInt(1, idSeance);
            stmtInsert.setInt(2, membre.getId());
            stmtInsert.executeUpdate();
            System.out.println("Membre ajouté à la table de liaison.");

            conn.commit();
            System.out.println("--- SUCCÈS RÉSERVATION ---");
            return true;

        } catch (SQLException e) {
            System.err.println("!!! ERREUR SQL CRITIQUE !!!");
            e.printStackTrace(); // REGARDEZ CETTE LIGNE DANS LA CONSOLE
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            try { if (stmtUpdate != null) stmtUpdate.close(); } catch (Exception e) {}
            try { if (stmtInsert != null) stmtInsert.close(); } catch (Exception e) {}
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (Exception e) {}
        }
    }
    // ANNULER RESERVATION
    public boolean annulerReservation(int idSeance, Membre membre) {
        SeanceCollective sc = getById(idSeance);
        if (sc == null) return false;

        sc.setPlacesDisponibles(sc.getPlacesDisponibles() + 1);
        // TODO: supprimer membre de table seance_membre
        return update(sc);
    }

    
}
