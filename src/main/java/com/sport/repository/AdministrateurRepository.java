package com.sport.repository;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;


import com.sport.model.Coach;
import com.sport.model.Membre;
import com.sport.model.Salle;
import com.sport.model.Equipement;
import com.sport.utils.DBConnection;


public class AdministrateurRepository {

    // =========================
    // MEMBRES
    // =========================
    public void ajouterMembre(Membre membre) {
        String sql = "INSERT INTO MEMBRE (id_utilisateur, objectifSportif, preferences) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, membre.getId());
            stmt.setString(2, membre.getObjectifSportif() != null ? membre.getObjectifSportif().name() : null);
            stmt.setString(3, membre.getPreferences() != null ? membre.getPreferences().name() : null);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Erreur ajout membre : " + e.getMessage());
        }
    }

    public void supprimerMembre(int membreId) {
        String sql = "DELETE FROM MEMBRE WHERE id_utilisateur = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, membreId);
            int lignesSupprimees = stmt.executeUpdate();
            System.out.println(lignesSupprimees > 0 ? "Membre supprimé !" : "Aucun membre trouvé !");
        } catch (SQLException e) {
            System.out.println("Erreur suppression membre : " + e.getMessage());
        }
    }

    public List<Membre> listerMembres() {
        List<Membre> membres = new ArrayList<>();
        String sql = "SELECT * FROM MEMBRE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Membre m = new Membre();
                m.setId(rs.getInt("id_utilisateur"));

                String objectif = rs.getString("objectifSportif");
                if (objectif != null) m.setObjectifSportif(Membre.Objectif.valueOf(objectif));

                String pref = rs.getString("preferences");
                if (pref != null) m.setPreferences(Membre.Preference.valueOf(pref));

                membres.add(m);
            }

        } catch (SQLException e) {
            System.out.println("Erreur récupération des membres : " + e.getMessage());
        }
        return membres;
    }

    // =========================
    // COACHS
    // =========================
    public void ajouterCoach(Coach coach) {
        String sqlCoach = "INSERT INTO COACH (id_utilisateur) VALUES (?)";
        String sqlSpec = "INSERT INTO COACH_SPECIALITE (coach_id, specialite) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtCoach = conn.prepareStatement(sqlCoach)) {
                stmtCoach.setInt(1, coach.getId());
                stmtCoach.executeUpdate();
            }

            if (coach.getSpecialites() != null) {
                try (PreparedStatement stmtSpec = conn.prepareStatement(sqlSpec)) {
                    for (String s : coach.getSpecialites()) {
                        stmtSpec.setInt(1, coach.getId());
                        stmtSpec.setString(2, s);
                        stmtSpec.addBatch();
                    }
                    stmtSpec.executeBatch();
                }
            }

            conn.commit();

        } catch (SQLException e) {
            System.out.println("Erreur ajout coach : " + e.getMessage());
        }
    }

    public void supprimerCoach(int coachId) {
        String sqlSpec = "DELETE FROM COACH_SPECIALITE WHERE coach_id = ?";
        String sqlCoach = "DELETE FROM COACH WHERE id_utilisateur = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmtSpec = conn.prepareStatement(sqlSpec)) {
                stmtSpec.setInt(1, coachId);
                stmtSpec.executeUpdate();
            }

            try (PreparedStatement stmtCoach = conn.prepareStatement(sqlCoach)) {
                stmtCoach.setInt(1, coachId);
                stmtCoach.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            System.out.println("Erreur suppression coach : " + e.getMessage());
        }
    }

    public List<Coach> listerCoachs() {
        List<Coach> coaches = new ArrayList<>();
        String sqlCoach = "SELECT * FROM COACH";
        String sqlSpec = "SELECT specialite FROM COACH_SPECIALITE WHERE coach_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmtCoach = conn.prepareStatement(sqlCoach);
             ResultSet rsCoach = stmtCoach.executeQuery()) {

            while (rsCoach.next()) {
                Coach c = new Coach();
                int id = rsCoach.getInt("id_utilisateur");
                c.setId(id);

                // récupérer les spécialités
                try (PreparedStatement stmtSpec = conn.prepareStatement(sqlSpec)) {
                    stmtSpec.setInt(1, id);
                    try (ResultSet rsSpec = stmtSpec.executeQuery()) {
                        List<String> specs = new ArrayList<>();
                        while (rsSpec.next()) specs.add(rsSpec.getString("specialite"));
                        c.setSpecialites(specs);
                    }
                }
                coaches.add(c);
            }

        } catch (SQLException e) {
            System.out.println("Erreur liste coachs : " + e.getMessage());
        }
        return coaches;
    }

    // =========================
    // EQUIPEMENTS
    // =========================
    public void ajouterEquipement(Equipement e) {
        String sql = "INSERT INTO EQUIPEMENT (nom, type, etat, date_achat) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, e.getNom());
            stmt.setString(2, e.getType().name());
            stmt.setString(3, e.getEtat().name());
            stmt.setDate(4, new java.sql.Date(e.getDateAchat().getTime()));
            stmt.executeUpdate();

        } catch (SQLException ex) {
            // System.out.println("Erreur ajout équipement : " + ex.getMe;ssage());
        }
    }

    public void supprimerEquipement(int id) {
        String sql = "DELETE FROM EQUIPEMENT WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("Erreur suppression équipement : " + ex.getMessage());
        }
    }

    public List<Equipement> listerEquipements() {
        List<Equipement> list = new ArrayList<>();
        String sql = "SELECT * FROM EQUIPEMENT";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Equipement e = new Equipement();
                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                e.setType(Equipement.TypeEquipement.valueOf(rs.getString("type")));
                e.setEtat(Equipement.EtatEquipement.valueOf(rs.getString("etat")));
                e.setDateAchat(rs.getDate("date_achat"));
                list.add(e);
            }

        } catch (SQLException e) {
            System.out.println("Erreur liste équipements : " + e.getMessage());
        }
        return list;
    }

     /**
     * Ajoute une nouvelle salle dans la base de données
     */
    public void ajouterSalle(Salle salle) {
        String sql = "INSERT INTO Salle (idSalle, nom, capacite, type) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salle.getId());
            stmt.setString(2, salle.getNom());
            stmt.setInt(3, salle.getCapacite());
            stmt.setString(4, salle.getType() != null ? salle.getType().name() : null);
            stmt.executeUpdate();
            System.out.println("Salle ajoutée avec succès !");

        } catch (SQLException e) {
            System.out.println("Erreur ajout salle : " + e.getMessage());
        }
    }

    /**
     * Supprime une salle de la base de données
     */
    public void supprimerSalle(int salleId) {
        String sql = "DELETE FROM Salle WHERE idSalle = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, salleId);
            int lignesSupprimees = stmt.executeUpdate();
            System.out.println(lignesSupprimees > 0 ? "Salle supprimée !" : "Aucune salle trouvée !");
            
        } catch (SQLException e) {
            System.out.println("Erreur suppression salle : " + e.getMessage());
        }
    }

    /**
     * Met à jour les informations d'une salle
     */
    public void modifierSalle(Salle salle) {
        String sql = "UPDATE Salle SET nom = ?, capacite = ?, type = ? WHERE idSalle = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, salle.getNom());
            stmt.setInt(2, salle.getCapacite());
            stmt.setString(3, salle.getType() != null ? salle.getType().name() : null);
            stmt.setInt(4, salle.getId());
            
            int lignesModifiees = stmt.executeUpdate();
            System.out.println(lignesModifiees > 0 ? "Salle modifiée !" : "Aucune salle trouvée !");

        } catch (SQLException e) {
            System.out.println("Erreur modification salle : " + e.getMessage());
        }
    }
    public void ajouterSalle(Salle salle) {
        String sql = "INSERT INTO Salle (idSalle, nom, capacite, type) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salle.getId());
            stmt.setString(2, salle.getNom());
            stmt.setInt(3, salle.getCapacite());
            stmt.setString(4, salle.getType() != null ? salle.getType().name() : null);
            stmt.executeUpdate();
            System.out.println(" Salle ajoutée avec succès !");
        } catch (SQLException e) {
            System.out.println(" Erreur ajout salle : " + e.getMessage());
        }
    }

    public void modifierSalle(Salle salle) {
        String sql = "UPDATE Salle SET nom = ?, capacite = ?, type = ? WHERE idSalle = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, salle.getNom());
            stmt.setInt(2, salle.getCapacite());
            stmt.setString(3, salle.getType() != null ? salle.getType().name() : null);
            stmt.setInt(4, salle.getId());
            int lignes = stmt.executeUpdate();
            System.out.println(lignes > 0 ? " Salle modifiée !" : " Salle introuvable !");
        } catch (SQLException e) {
            System.out.println(" Erreur modification salle : " + e.getMessage());
        }
    }

    public void supprimerSalle(int salleId) {
        String sql = "DELETE FROM Salle WHERE idSalle = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salleId);
            int lignes = stmt.executeUpdate();
            System.out.println(lignes > 0 ? " Salle supprimée !" : " Salle introuvable !");
        } catch (SQLException e) {
            System.out.println(" Erreur suppression salle : " + e.getMessage());
        }
    }

    public Salle getSalle(int salleId) {
        String sql = "SELECT idSalle, nom, capacite, type FROM Salle WHERE idSalle = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Salle s = new Salle();
                    s.setId(rs.getInt("idSalle"));
                    s.setNom(rs.getString("nom"));
                    s.setCapacite(rs.getInt("capacite"));
                    String type = rs.getString("type");
                    if (type != null) s.setType(Salle.TypeSalle.valueOf(type));
                    return s;
                }
            }
        } catch (SQLException e) {
            System.out.println(" Erreur récupération salle : " + e.getMessage());
        }
        return null;
    }

    public List<Salle> listerSalles() {
        List<Salle> salles = new ArrayList<>();
        String sql = "SELECT idSalle, nom, capacite, type FROM Salle";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Salle s = new Salle();
                s.setId(rs.getInt("idSalle"));
                s.setNom(rs.getString("nom"));
                s.setCapacite(rs.getInt("capacite"));
                String type = rs.getString("type");
                if (type != null) s.setType(Salle.TypeSalle.valueOf(type));
                salles.add(s);
            }
        } catch (SQLException e) {
            System.out.println(" Erreur liste salles : " + e.getMessage());
        }
        return salles;
}
public String genererRapport(TypeRapport type) {
    switch (type) {
        case OCCUPATION_COURS:
            return genererRapportOccupationCours();
        case FREQUENTATION_SALLE:
            return genererRapportFrequentationSalle();
        case SATISFACTION_MEMBRES:
            return genererRapportSatisfactionMembres();
        case REVENUS_ABONNEMENTS:
            return genererRapportRevenusAbonnements();
        default:
            return "Type de rapport non reconnu.";
    }
}
<<<<<<< HEAD:src/main/java/com/sport/Repository/AdministrateurRepository.java
}
=======
>>>>>>> origin/main:src/main/java/com/sport/repository/AdministrateurRepository.java
