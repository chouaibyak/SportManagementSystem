package com.sport.repository;

import com.sport.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SeanceRepository {

    private Connection connection;

    public SeanceRepository() {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/sport_club?useSSL=false&serverTimezone=UTC", 
                    "root", 
                    "password"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sauvegarder une séance (collective ou individuelle)
    public void save(Seance seance) {
        String query = "INSERT INTO seance (nom, capaciteMax, salle_id, dateHeure, entraineur_id, typeCours, duree, places_disponibles, membre_id, tarif, notes_coach, typeSeance) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                stmt.setNull(8, Typecs.INTEGER); // placesDisponibles
                stmt.setInt(9, si.getMembre().getId());
                stmt.setDouble(10, si.getTarif());
                stmt.setString(11, si.getNotesCoach());
                stmt.setString(12, "individuelle");
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Récupérer une séance par ID
    public Seance findById(int id) {
        String query = "SELECT * FROM seance WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSeance(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Récupérer toutes les séances
    public List<Seance> findAll() {
        List<Seance> seances = new ArrayList<>();
        String query = "SELECT * FROM seance";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Seance s = mapResultSetToSeance(rs);
                if (s != null) seances.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seances;
    }

    // Supprimer une séance
    public void delete(int id) {
        String query = "DELETE FROM seance WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     // Crée une instance Seance (collective ou individuelle) à partir du ResultSet
    private Seance mapResultSetToSeance(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nom = rs.getString("nom");
        int capaciteMax = rs.getInt("capaciteMax");
        LocalDateTime dateHeure = rs.getTimestamp("dateHeure").toLocalDateTime();
        TypeCours typeCours = TypeCours.valueOf(rs.getString("typeCours"));
        int duree = rs.getInt("duree");
        String typeSeance = rs.getString("typeSeance");

        // --- Salle ---
        String nomSalle = rs.getString("nom_salle");
        int capaciteSalle = rs.getInt("capacite_salle");
        TypeSalle typeSalle = TypeSalle.valueOf(rs.getString("type_salle"));
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
            TypeObjectif objectif = TypeObjectif.valueOf(rs.getString("objectif_sportif"));
            TypePreference pref = TypePreference.valueOf(rs.getString("preference_sportif"));

            Membre membre = new Membre(membreId, nomMembre, prenomMembre, dateNaissance, email, tel, adresse, objectif, pref);

            double tarif = rs.getDouble("tarif");
            String notesCoach = rs.getString("notes_coach");

            return new SeanceIndividuelle(id, nom, capaciteMax, salle, dateHeure, coach, typeCours, duree, membre, tarif, notesCoach);
        }

        return null;
    }
}
