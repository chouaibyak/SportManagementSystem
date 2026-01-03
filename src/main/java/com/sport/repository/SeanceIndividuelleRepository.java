package com.sport.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.sport.model.Membre;
import com.sport.model.SeanceIndividuelle;
import com.sport.model.TypeCours;
import com.sport.model.TypeSeance;
import com.sport.utils.DBConnection;

public class SeanceIndividuelleRepository {

  // GET ALL
  // GET ALL
    public List<SeanceIndividuelle> getAll() {
        List<SeanceIndividuelle> list = new ArrayList<>();

        // CORRECTION ICI : Ajout du mot "SELECT" au début !
        String query = """
            SELECT 
                s.id, s.nom, s.capaciteMax, s.dateHeure, s.duree,
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
                if (ts != null) si.setDateHeure(ts.toLocalDateTime());

                si.setDuree(rs.getInt("duree"));
                
                try {
                    si.setTypeCours(TypeCours.valueOf(rs.getString("typeCours")));
                    si.setTypeSeance(TypeSeance.valueOf(rs.getString("typeSeance")));
                } catch (Exception e) {
                    // ignore
                }

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
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }

        // Hydratation des objets liés
        SalleRepository salleRepo = new SalleRepository();
        CoachRepository coachRepo = new CoachRepository();
        MembreRepository membreRepo = new MembreRepository();

        for (int i = 0; i < list.size(); i++) {
            SeanceIndividuelle si = list.get(i);
            si.setSalle(salleRepo.getSalleById(salleIds.get(i)));
            si.setEntraineur(coachRepo.getCoachById(coachIds.get(i)));
            
            int mId = membreIds.get(i);
            if (mId != -1) {
                si.setMembre(membreRepo.trouverParId(mId));
            } else {
                si.setMembre(null);
            }
        }

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
        String insertSeance = "INSERT INTO seance (nom, capaciteMax, salle_id, dateHeure, entraineur_id, type, typeSeance, duree) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        // C'est cette ligne qui manquait ou n'était pas appelée :
        String insertIndiv = "INSERT INTO seanceindividuelle (seance_id, membre_id, tarif, notesCoach) VALUES (?, ?, ?, ?)";

        int generatedId = -1;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Début de transaction

            try (PreparedStatement s1 = conn.prepareStatement(insertSeance, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement s2 = conn.prepareStatement(insertIndiv)) {

                // 1. Insertion dans la table parent (SEANCE)
                s1.setString(1, si.getNom());
                s1.setInt(2, 1); // Capacité 1 pour individuelle
                s1.setInt(3, si.getSalle().getId());
                s1.setTimestamp(4, Timestamp.valueOf(si.getDateHeure()));
                s1.setInt(5, si.getEntraineur().getId());
                s1.setString(6, si.getTypeCours().toString());
                s1.setString(7, TypeSeance.INDIVIDUELLE.toString());
                s1.setInt(8, si.getDuree());
                s1.executeUpdate();

                // Récupération de l'ID
                ResultSet rs = s1.getGeneratedKeys();
                if (rs.next()) {
                    generatedId = rs.getInt(1);
                    si.setId(generatedId);
                }

                // 2. Insertion dans la table enfant (SEANCEINDIVIDUELLE)
                s2.setInt(1, generatedId);

                // Gestion Membre (NULL possible)
                if (si.getMembre() != null) s2.setInt(2, si.getMembre().getId());
                else s2.setNull(2, java.sql.Types.INTEGER);

                // Gestion Tarif (NULL possible)
                if (si.getTarif() != null) s2.setDouble(3, si.getTarif());
                else s2.setNull(3, java.sql.Types.DOUBLE);

                // Gestion Notes
                s2.setString(4, si.getNotesCoach());

                s2.executeUpdate();
                conn.commit(); // Validation

            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedId;
    }

 // UPDATE
    public boolean update(SeanceIndividuelle si) {
        String updateSeance = "UPDATE seance SET nom = ?, capaciteMax = ?, salle_id = ?, dateHeure = ?, type = ?, duree = ? WHERE id = ?";
        
        // Utilisation de INSERT ... ON DUPLICATE KEY UPDATE pour gérer les cas où la ligne manquerait
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

            try (PreparedStatement s1 = conn.prepareStatement(updateSeance);
                PreparedStatement s2 = conn.prepareStatement(upsertIndiv)) {

                // Update SEANCE
                s1.setString(1, si.getNom());
                s1.setInt(2, 1);
                s1.setInt(3, si.getSalle().getId());
                s1.setTimestamp(4, Timestamp.valueOf(si.getDateHeure()));
                s1.setString(5, si.getTypeCours().toString());
                s1.setInt(6, si.getDuree());
                s1.setInt(7, si.getId());
                s1.executeUpdate();

                // Update SEANCEINDIVIDUELLE
                s2.setInt(1, si.getId());
                
                if (si.getMembre() != null) s2.setInt(2, si.getMembre().getId());
                else s2.setNull(2, java.sql.Types.INTEGER);

                if (si.getTarif() != null) s2.setDouble(3, si.getTarif());
                else s2.setNull(3, java.sql.Types.DOUBLE);

                s2.setString(4, si.getNotesCoach());
                
                s2.executeUpdate();
                conn.commit();
                return true;

            } catch (SQLException ex) {
                conn.rollback();
                ex.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
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
