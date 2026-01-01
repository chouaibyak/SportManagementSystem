package com.sport.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.sql.Types;

import com.sport.model.Coach;
import com.sport.model.Membre;
import com.sport.model.SeanceIndividuelle;
import com.sport.model.TypeCours;
import com.sport.model.TypeSeance;
import com.sport.utils.DBConnection;

public class SeanceIndividuelleRepository {

  // GET ALL
  public List<SeanceIndividuelle> getAll() {

    List<SeanceIndividuelle> list = new ArrayList<>();

    String query = """
        SELECT s.id, s.nom, s.capaciteMax, s.dateHeure, s.duree,
               s.type AS typeCours, s.typeSeance,
               s.salle_id, s.entraineur_id,
               si.membre_id, si.tarif, si.notesCoach
        FROM seance s
        LEFT JOIN seanceindividuelle si ON s.id = si.seance_id
        WHERE UPPER(s.typeSeance) = 'INDIVIDUELLE'
    """;

    List<Integer> salleIds = new ArrayList<>();
    List<Integer> coachIds = new ArrayList<>();
    List<Integer> membreIds = new ArrayList<>();

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {

            SeanceIndividuelle si = new SeanceIndividuelle();

            si.setId(rs.getInt("id"));
            si.setNom(rs.getString("nom"));
            si.setCapaciteMax(rs.getInt("capaciteMax"));

            Timestamp ts = rs.getTimestamp("dateHeure");
            if (ts != null) {
                si.setDateHeure(ts.toLocalDateTime());
            }

            si.setDuree(rs.getInt("duree"));
            si.setTypeCours(TypeCours.valueOf(rs.getString("typeCours")));
            si.setTypeSeance(TypeSeance.valueOf(rs.getString("typeSeance")));

            //  membre peut être NULL
            int membreId = rs.getInt("membre_id");
            if (rs.wasNull()) membreId = -1;

            Double tarif = rs.getDouble("tarif");
            if (rs.wasNull()) tarif = null;

            si.setTarif(tarif);
            si.setNotesCoach(rs.getString("notesCoach"));

            salleIds.add(rs.getInt("salle_id"));
            coachIds.add(rs.getInt("entraineur_id"));
            membreIds.add(membreId);

            list.add(si);

            // 🔍 LOG DEBUG
            System.out.println("INDIV LOADED → " + si.getNom() + " | coach=" + rs.getInt("entraineur_id"));
        }

    } catch (SQLException e) {
        e.printStackTrace();
        return list;
    }

    // ====== Chargement des objets complets ======
    SalleRepository salleRepo = new SalleRepository();
    CoachRepository coachRepo = new CoachRepository();
    MembreRepository membreRepo = new MembreRepository();

    for (int i = 0; i < list.size(); i++) {
        SeanceIndividuelle si = list.get(i);

        si.setSalle(salleRepo.getSalleById(salleIds.get(i)));
        si.setEntraineur(coachRepo.getCoachById(coachIds.get(i)));

        int membreId = membreIds.get(i);
        si.setMembre(membreId != -1 ? membreRepo.trouverParId(membreId) : null);
    }

    System.out.println("TOTAL SEANCES INDIV CHARGÉES = " + list.size());
    return list;
}

    // GET BY ID
    public SeanceIndividuelle getById(int seanceId) {
        return getAll().stream().filter(s -> s.getId() == seanceId).findFirst().orElse(null);
    }

    public Membre getMembreInscrit(int idSeance) {
        // Correction : m.id_utilisateur au lieu de m.id
        String sql = "SELECT m.*, u.nom, u.prenom FROM membre m " +
                    "JOIN utilisateur u ON m.id_utilisateur = u.id " +
                    "JOIN seanceindividuelle s ON s.membre_id = m.id_utilisateur " +
                    "WHERE s.seance_id = ?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idSeance);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Membre m = new Membre();
                    m.setId(rs.getInt("id_utilisateur")); // ou "id" selon votre table Utilisateur
                    m.setNom(rs.getString("nom"));
                    m.setPrenom(rs.getString("prenom"));
                    return m;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ADD
   public int ajouter(SeanceIndividuelle si) {

    String insertSeance =
        "INSERT INTO seance (nom, capaciteMax, salle_id, dateHeure, entraineur_id, type, typeSeance, duree) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    String insertIndiv =
        "INSERT INTO seanceindividuelle (seance_id, membre_id, tarif, notesCoach) VALUES (?, ?, ?, ?)";

    int generatedId = -1;

    try (Connection conn = DBConnection.getConnection()) {
        conn.setAutoCommit(false);

        try (PreparedStatement s1 = conn.prepareStatement(insertSeance, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement s2 = conn.prepareStatement(insertIndiv)) {

            // ====== INSERT SEANCE ======
            s1.setString(1, si.getNom());
            s1.setInt(2, 1); // capacité fixe pour individuelle
            s1.setInt(3, si.getSalle().getId());
            s1.setTimestamp(4, Timestamp.valueOf(si.getDateHeure()));
            s1.setInt(5, si.getEntraineur().getId());
            s1.setString(6, si.getTypeCours().toString());
            s1.setString(7, si.getTypeSeance().toString());
            s1.setInt(8, si.getDuree());
            s1.executeUpdate();

            // Récupérer ID généré
            ResultSet rs = s1.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
                si.setId(generatedId);
            }

            // ====== INSERT SEANCE INDIVIDUELLE ======
            s2.setInt(1, generatedId);

            // membre_id peut être NULL
            if (si.getMembre() != null) {
                s2.setInt(2, si.getMembre().getId());
            } else {
                s2.setNull(2, java.sql.Types.INTEGER);
            }

            s2.setDouble(3, si.getTarif());
            s2.setString(4, si.getNotesCoach());
            s2.executeUpdate();

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
        String upsertIndiv = """
            INSERT INTO seanceindividuelle (seance_id, membre_id, tarif, notesCoach)
            VALUES (?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                membre_id = VALUES(membre_id),
                tarif = VALUES(tarif),
                notesCoach = VALUES(notesCoach)
        """;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement(updateSeance);
                 PreparedStatement stmt2 = conn.prepareStatement(upsertIndiv)) {

                // Update seance
                stmt1.setString(1, si.getNom());
                stmt1.setInt(2, si.getCapaciteMax());
                stmt1.setInt(3, si.getSalle().getId());
                stmt1.setTimestamp(4, Timestamp.valueOf(si.getDateHeure()));
                stmt1.setString(5, si.getTypeCours().toString());
                stmt1.setString(6, si.getTypeSeance().toString());
                stmt1.setInt(7, si.getDuree());
                stmt1.setInt(8, si.getId());
                stmt1.executeUpdate();

                // Upsert seanceindividuelle
                stmt2.setInt(1, si.getId());
                if (si.getMembre() != null) {
                    stmt2.setInt(2, si.getMembre().getId());
                } else {
                    stmt2.setNull(2, Types.INTEGER);
                }

                if (si.getTarif() != null) {
                    stmt2.setDouble(3, si.getTarif());
                } else {
                    stmt2.setNull(3, Types.DOUBLE);
                }

                stmt2.setString(4, si.getNotesCoach() != null ? si.getNotesCoach() : "");
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

    // Méthode à ajouter impérativement pour corriger l'erreur
    // Dans SeanceIndividuelleRepository.java
    public boolean reserverSession(int seanceId, int membreId) {
        // Si membreId est 0 ou moins, on considère qu'on veut libérer la place (NULL)
        String sql = "UPDATE seanceindividuelle SET membre_id = ? WHERE seance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (membreId <= 0) {
                stmt.setNull(1, java.sql.Types.INTEGER); // Met NULL en BDD
            } else {
                stmt.setInt(1, membreId);
            }
            
            stmt.setInt(2, seanceId);
            
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la séance individuelle : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
