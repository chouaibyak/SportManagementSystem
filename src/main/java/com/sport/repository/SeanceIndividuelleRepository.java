package com.sport.repository;

import com.sport.model.*;
import com.sport.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeanceIndividuelleRepository {

    // GET ALL
    public List<SeanceIndividuelle> getAll() {
        List<SeanceIndividuelle> list = new ArrayList<>();
        String query = "SELECT s.id, s.nom, s.capaciteMax, s.dateHeure, s.type AS typeCours, s.typeSeance, s.duree, s.salle_id, s.entraineur_id, " +
                       "si.membre_id, si.tarif, si.notesCoach " +
                       "FROM seance s " +
                       "JOIN seanceindividuelle si ON s.id = si.seance_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                SeanceIndividuelle si = new SeanceIndividuelle();

                si.setId(rs.getInt("id"));
                si.setNom(rs.getString("nom"));
                si.setCapaciteMax(rs.getInt("capaciteMax"));
                si.setDateHeure(rs.getTimestamp("dateHeure").toLocalDateTime());
                si.setDuree(rs.getInt("duree"));
                si.setTypeSeance(TypeSeance.valueOf(rs.getString("typeSeance")));
                si.setTypeCours(TypeCours.valueOf(rs.getString("typeCours")));
                si.setTarif(rs.getDouble("tarif"));
                si.setNotesCoach(rs.getString("notesCoach"));

                Salle salle = new Salle();
                salle.setId(rs.getInt("salle_id"));
                si.setSalle(salle);

                Coach coach = new Coach();
                coach.setId(rs.getInt("entraineur_id"));
                si.setEntraineur(coach);

                Membre membre = new Membre();
                membre.setId(rs.getInt("membre_id"));
                si.setMembre(membre);

                list.add(si);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // GET BY ID
    public SeanceIndividuelle getById(int seanceId) {
        return getAll().stream().filter(s -> s.getId() == seanceId).findFirst().orElse(null);
    }

    // ADD
    public int ajouter(SeanceIndividuelle si) {
        String insertSeance = "INSERT INTO seance (nom, capaciteMax, salle_id, dateHeure, entraineur_id, type, typeSeance, duree) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String insertIndiv = "INSERT INTO seanceindividuelle (seance_id, membre_id, tarif, notesCoach) VALUES (?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(insertSeance, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement stmt2 = conn.prepareStatement(insertIndiv)) {

                stmt1.setString(1, si.getNom());
                stmt1.setInt(2, si.getCapaciteMax());
                stmt1.setInt(3, si.getSalle().getId());
                stmt1.setTimestamp(4, Timestamp.valueOf(si.getDateHeure()));
                stmt1.setInt(5, si.getEntraineur().getId());
                stmt1.setString(6, si.getTypeCours().toString()); // YOGA, MUSCULATION, etc.
                stmt1.setString(7, si.getTypeSeance().toString()); // INDIVIDUELLE
                stmt1.setInt(8, si.getDuree());

                stmt1.executeUpdate();

                ResultSet rs = stmt1.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    si.setId(generatedId);
                }

                stmt2.setInt(1, generatedId);
                stmt2.setInt(2, si.getMembre().getId());
                stmt2.setDouble(3, si.getTarif());
                stmt2.setString(4, si.getNotesCoach());
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
    public boolean update(SeanceIndividuelle si) {
        String updateSeance = "UPDATE seance SET nom = ?, capaciteMax = ?, salle_id = ?, dateHeure = ?, type = ?, typeSeance = ?, duree = ? WHERE id = ?";
        String updateIndiv = "UPDATE seanceindividuelle SET membre_id = ?, tarif = ?, notesCoach = ? WHERE seance_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(updateSeance);
                 PreparedStatement stmt2 = conn.prepareStatement(updateIndiv)) {

                stmt1.setString(1, si.getNom());
                stmt1.setInt(2, si.getCapaciteMax());
                stmt1.setInt(3, si.getSalle().getId());
                stmt1.setTimestamp(4, Timestamp.valueOf(si.getDateHeure()));
                stmt1.setString(5, si.getTypeCours().toString());
                stmt1.setString(6, si.getTypeSeance().toString());
                stmt1.setInt(7, si.getDuree());
                stmt1.setInt(8, si.getId());
                stmt1.executeUpdate();

                stmt2.setInt(1, si.getMembre().getId());
                stmt2.setDouble(2, si.getTarif());
                stmt2.setString(3, si.getNotesCoach());
                stmt2.setInt(4, si.getId());
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
        String deleteIndiv = "DELETE FROM seanceindividuelle WHERE seance_id = ?";
        String deleteSeance = "DELETE FROM seance WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(deleteIndiv);
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
}
