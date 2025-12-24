package com.sport.repository;

import com.sport.model.*;
import com.sport.utils.DBConnection;

import java.sql.*;
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
                sc.setListeMembers(new ArrayList<>()); // sécurité

                sc.setId(rs.getInt("id"));
                sc.setNom(rs.getString("nom"));
                sc.setCapaciteMax(rs.getInt("capaciteMax"));
                sc.setDateHeure(rs.getTimestamp("dateHeure").toLocalDateTime());
                sc.setDuree(rs.getInt("duree"));

                // TypeCours et TypeSeance
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

                list.add(sc);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // GET BY ID
    public SeanceCollective getById(int seanceId) {
        return getAll().stream().filter(s -> s.getId() == seanceId).findFirst().orElse(null);
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

    // RESERVER PLACE
    public boolean reserverPlace(int idSeance, Membre membre) {
        SeanceCollective sc = getById(idSeance);
        if (sc == null || sc.getPlacesDisponibles() <= 0) return false;

        sc.setPlacesDisponibles(sc.getPlacesDisponibles() - 1);
        // TODO: ajouter membre dans table seance_membre
        return update(sc);
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
