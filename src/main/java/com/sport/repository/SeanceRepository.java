package com.sport.repository;

<<<<<<< HEAD
import com.sport.model.*;
=======
import com.sport.model.Coach;
import com.sport.model.Seance;
import com.sport.model.Salle;
>>>>>>> main
import com.sport.utils.DBConnection;

import java.sql.*;

public class SeanceRepository {

<<<<<<< HEAD
    public SeanceRepository() {
        // plus besoin de créer la connexion ici — on utilisera DBConnection.getConnection() dans chaque méthode
    }

    // Sauvegarder une séance (collective ou individuelle)
    public void save(Seance seance) {
        String query = "INSERT INTO seance (nom, capaciteMax, salle_id, dateHeure, entraineur_id, typeCours, duree, places_disponibles, membre_id, tarif, notes_coach, typeSeance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection connection = DBConnection.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, seance.getNom());
                stmt.setInt(2, seance.getCapaciteMax());
                stmt.setInt(3, seance.getSalle().getId());
                stmt.setTimestamp(4, Timestamp.valueOf(seance.getDateHeure()));
                stmt.setInt(5, seance.getEntraineur().getId());
                stmt.setString(6, seance.getTypeCours().toString());
                stmt.setInt(7, seance.getDuree());

                if (seance instanceof SeanceCollective sc) {
                    stmt.setInt(8, sc.getPlacesDisponibles());
                    stmt.setNull(9, Types.INTEGER); // membre_id
                    stmt.setNull(10, Types.DOUBLE); // tarif
                    stmt.setNull(11, Types.VARCHAR); // notesCoach
                    stmt.setString(12, "collective");
                } else if (seance instanceof SeanceIndividuelle si) {
                    stmt.setNull(8, Types.INTEGER); // placesDisponibles
                    stmt.setInt(9, si.getMembre().getId());
                    stmt.setDouble(10, si.getTarif());
                    stmt.setString(11, si.getNotesCoach());
                    stmt.setString(12, "individuelle");
                }

                stmt.executeUpdate();
            }
=======
    // Créer une séance
    // Dans SeanceRepository.java

    public void creerSeance(Coach coach, Seance seance) {
        // 1. Ajouter RETURN_GENERATED_KEYS
        String query = "INSERT INTO seance (nom, capaciteMax, salle_id, dateHeure, entraineur_id, type, duree) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) { // <--- ICI

            stmt.setString(1, seance.getNom());
            stmt.setInt(2, seance.getCapaciteMax());
            stmt.setInt(3, seance.getSalle().getId());
            stmt.setTimestamp(4, Timestamp.valueOf(seance.getDateHeure()));
            stmt.setInt(5, coach.getId());
            stmt.setString(6, seance.getTypeCours().toString());
            stmt.setInt(7, seance.getDuree());

            stmt.executeUpdate();

            // 2. RECUPERER L'ID GÉNÉRÉ (C'est la partie manquante)
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                seance.setId(rs.getInt(1)); // Met à jour l'objet Java
            }
            System.out.println("Séance créée en BDD avec ID: " + seance.getId());

>>>>>>> main
        } catch (SQLException e) {
            System.out.println("Erreur creation seance : " + e.getMessage());
        }
    }

<<<<<<< HEAD
    // Récupérer une séance par ID
    public Seance findById(int id) {
        String query = "SELECT * FROM seance WHERE id = ?";
        try {
            Connection connection = DBConnection.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToSeance(rs);
                    }
                }
=======
    // Modifier une séance
    public void modifierSeance(Seance seance, int coachId) {
        String query = "UPDATE seance SET nom = ?, capaciteMax = ?, salle_id = ?, dateHeure = ?, type = ?, duree = ? WHERE id = ? AND entraineur_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, seance.getNom());
            stmt.setInt(2, seance.getCapaciteMax());
            stmt.setInt(3, seance.getSalle().getId());

            // --- CORRECTION : LocalDateTime -> Timestamp ---
            stmt.setTimestamp(4, Timestamp.valueOf(seance.getDateHeure()));
            // -----------------------------------------------

            stmt.setString(5, seance.getTypeCours().toString());
            stmt.setInt(6, seance.getDuree());
            stmt.setInt(7, seance.getId());
            stmt.setInt(8, coachId);

            int result = stmt.executeUpdate();
            if (result == 0) {
                System.out.println("Aucune modification : Séance introuvable ou mauvais coach.");
            } else {
                System.out.println("Séance modifiée avec succès.");
            }

        } catch (SQLException e) {
            System.out.println("Erreur modification seance : " + e.getMessage());
        }
    }

    // Supprimer une séance (Inchangé car pas de date utilisée)
    public void supprimerSeance(int seanceId, int coachId) {
        String query = "DELETE FROM seance WHERE id = ? AND entraineur_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, seanceId);
            stmt.setInt(2, coachId);

            int result = stmt.executeUpdate();
            System.out.println(result > 0 ? "Séance supprimée." : "Echec suppression.");

        } catch (SQLException e) {
            System.out.println("Erreur suppression seance : " + e.getMessage());
        }
    }

    // Vérifier disponibilité
    // On change le paramètre String -> LocalDateTime
    public boolean verifierDisponibiliteSalle(Salle salle, java.time.LocalDateTime dateHeure) {
        
        String query = "SELECT COUNT(*) FROM seance WHERE salle_id = ? AND dateHeure = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, salle.getId());
            
            // --- CORRECTION : Utilisation de setTimestamp ---
            stmt.setTimestamp(2, Timestamp.valueOf(dateHeure));
            // ------------------------------------------------

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Si 0, c'est libre
>>>>>>> main
            }
        } catch (SQLException e) {
            System.out.println("Erreur verification salle : " + e.getMessage());
        }
        return false;
    }
<<<<<<< HEAD

    // Récupérer toutes les séances
    public List<Seance> findAll() {
        List<Seance> seances = new ArrayList<>();
        String query = "SELECT * FROM seance";
        try {
            Connection connection = DBConnection.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    Seance s = mapResultSetToSeance(rs);
                    if (s != null) seances.add(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seances;
    }

    // Supprimer une séance
    public void delete(int id) {
        String query = "DELETE FROM seance WHERE id = ?";
        try {
            Connection connection = DBConnection.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Crée une instance Seance (collective ou individuelle) à partir du ResultSet
    private Seance mapResultSetToSeance(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nom = rs.getString("nom");
        int capaciteMax = rs.getInt("capaciteMax");
        Timestamp ts = rs.getTimestamp("dateHeure");
        LocalDateTime dateHeure = ts != null ? ts.toLocalDateTime() : null;
        TypeCours typeCours = TypeCours.valueOf(rs.getString("typeCours"));
        int duree = rs.getInt("duree");
        String typeSeance = rs.getString("typeSeance");

        // --- Salle ---
        String nomSalle = rs.getString("nom_salle");
        int capaciteSalle = rs.getInt("capacite_salle");
        TypeSalle typeSalle = null;
        String typeSalleStr = rs.getString("type_salle");
        if (typeSalleStr != null && !typeSalleStr.isEmpty()) {
            typeSalle = TypeSalle.valueOf(typeSalleStr);
        }
        Salle salle = new Salle(nomSalle, capaciteSalle, typeSalle);
        salle.setId(rs.getInt("salle_id"));

        // --- Coach ---
        int coachId = rs.getInt("entraineur_id");
        Coach coach = new CoachRepository().findById(coachId);

        if ("collective".equalsIgnoreCase(typeSeance)) {
            int placesDispo = rs.getInt("places_disponibles");
            return new SeanceCollective(id, nom, capaciteMax, salle, dateHeure, coach, typeCours, duree, placesDispo);
        } else if ("individuelle".equalsIgnoreCase(typeSeance)) {
            // --- Membre ---
            int membreId = rs.getInt("membre_id");
            String nomMembre = rs.getString("nom_membre");
            String prenomMembre = rs.getString("prenom_membre");
            String dateNaissance = rs.getString("date_naissance");
            String email = rs.getString("email_membre");
            String tel = rs.getString("telephone_membre");
            String adresse = rs.getString("adresse_membre");

            // safeguard for nullable enum columns
            TypeObjectif objectif = null;
            String objectifStr = rs.getString("objectif_sportif");
            if (objectifStr != null && !objectifStr.isEmpty()) {
                objectif = TypeObjectif.valueOf(objectifStr);
            }

            TypePreference pref = null;
            String prefStr = rs.getString("preference_sportif");
            if (prefStr != null && !prefStr.isEmpty()) {
                pref = TypePreference.valueOf(prefStr);
            }

            Membre membre = new Membre(membreId, nomMembre, prenomMembre, dateNaissance, email, tel, adresse, objectif, pref);

            double tarif = rs.getDouble("tarif");
            String notesCoach = rs.getString("notes_coach");

            return new SeanceIndividuelle(id, nom, capaciteMax, salle, dateHeure, coach, typeCours, duree, membre, tarif, notesCoach);
        }

        return null;
    }
}
=======
}
>>>>>>> main
