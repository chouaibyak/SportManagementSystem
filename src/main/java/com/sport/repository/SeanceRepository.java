package com.sport.repository;

import com.sport.model.Seance;
import com.sport.model.SeanceCollective;
import com.sport.model.SeanceIndividuelle;
import com.sport.model.Coach;
import com.sport.model.Membre;
import com.sport.model.TypeCours;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SeanceRepository {

    private Connection connection;

    // Constructeur qui initialise la connexion à la base de données
    public SeanceRepository() {
        try {
            // Remplacez ceci par votre propre chaîne de connexion JDBC
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/sportdb", "root", "password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour sauvegarder une Séance
    public void save(Seance seance) {
        String query = "INSERT INTO seance (nom, date, type_cours, coach_id, places_disponibles, tarif, notes_coach) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, seance.getNom());
            stmt.setTimestamp(2, Timestamp.valueOf(seance.getDateHeure()));  // Conversion de LocalDateTime à Timestamp pour la base de données
            stmt.setString(3, seance.getTypeCours().toString());  // Utilisation de toString() pour TypeCours

            // Si c'est une SeanceCollective ou SeanceIndividuelle, on ajoute les données spécifiques
            if (seance instanceof SeanceCollective) {
                SeanceCollective sc = (SeanceCollective) seance;
                stmt.setLong(4, sc.getEntraineur().getId());  // Utilisation de getEntraineur() pour récupérer l'ID du coach
                stmt.setInt(5, sc.getPlacesDisponibles());  // placesDisponibles pour SeanceCollective
                stmt.setNull(6, java.sql.Types.NULL);  // Tarif n'est pas utilisé dans SeanceCollective
                stmt.setNull(7, java.sql.Types.NULL);  // notesCoach n'est pas utilisé dans SeanceCollective
            } else if (seance instanceof SeanceIndividuelle) {
                SeanceIndividuelle si = (SeanceIndividuelle) seance;
                stmt.setLong(4, si.getEntraineur().getId());  // Utilisation de getEntraineur() pour récupérer l'ID du coach
                stmt.setNull(5, java.sql.Types.NULL);  // placesDisponibles n'est pas utilisé dans SeanceIndividuelle
                stmt.setDouble(6, si.getTarif());  // Tarif pour SeanceIndividuelle
                stmt.setString(7, si.getNotesCoach());  // notesCoach pour SeanceIndividuelle
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour récupérer une Séance par son ID
    public Seance findById(int id) {
        String query = "SELECT * FROM seance WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String nom = resultSet.getString("nom");
                Timestamp date = resultSet.getTimestamp("date");  // Utilisation de Timestamp pour récupérer la date
                String typeCours = resultSet.getString("type_cours");
                String coachId = resultSet.getString("coach_id");

                // Récupérer le coach depuis le repository
                Coach coach = new CoachRepository().findById(coachId);

                // Convertir la date récupérée en LocalDateTime
                LocalDateTime localDateTime = date.toLocalDateTime();

                // Instancier SeanceCollective ou SeanceIndividuelle en fonction du type de séance
                if (typeCours.equals("YOGA")) {
                    int placesDisponibles = resultSet.getInt("places_disponibles");
                    return new SeanceCollective(nom, localDateTime, typeCours, coach, placesDisponibles);
                } else {
                    double tarif = resultSet.getDouble("tarif");
                    String notesCoach = resultSet.getString("notes_coach");
                    Membre membre = new Membre();  // Exemple, récupérer le membre si nécessaire.
                    return new SeanceIndividuelle(nom, localDateTime, typeCours, coach, membre, tarif, notesCoach);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Méthode pour récupérer toutes les séances
    public List<Seance> findAll() {
        List<Seance> seances = new ArrayList<>();
        String query = "SELECT * FROM seance";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                String nom = resultSet.getString("nom");
                Timestamp date = resultSet.getTimestamp("date");  // Utilisation de Timestamp pour récupérer la date
                String typeCours = resultSet.getString("type_cours");
                String coachId = resultSet.getString("coach_id");

                // Récupérer le coach depuis le repository
                Coach coach = new CoachRepository().findById(coachId);

                // Convertir la date récupérée en LocalDateTime
                LocalDateTime localDateTime = date.toLocalDateTime();

                // Instancier SeanceCollective ou SeanceIndividuelle en fonction du type de séance
                if (typeCours.equals("YOGA")) {
                    int placesDisponibles = resultSet.getInt("places_disponibles");
                    seances.add(new SeanceCollective(nom, localDateTime, typeCours, coach, placesDisponibles));
                } else {
                    double tarif = resultSet.getDouble("tarif");
                    String notesCoach = resultSet.getString("notes_coach");
                    Membre membre = new Membre();  // Exemple, récupérer le membre si nécessaire.
                    seances.add(new SeanceIndividuelle(nom, localDateTime, typeCours, coach, membre, tarif, notesCoach));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seances;
    }

    // Méthode pour supprimer une Séance par son ID
    public void delete(int id) {
        String query = "DELETE FROM seance WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
