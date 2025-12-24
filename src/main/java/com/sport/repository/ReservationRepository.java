package com.sport.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.sport.model.Membre;
import com.sport.model.Reservation;
import com.sport.model.Seance;
import com.sport.model.StatutReservation;
import com.sport.model.TypeCours;
import com.sport.model.TypeSeance;
import com.sport.utils.DBConnection;

public class ReservationRepository {

    // --- CREATE : Ajouter une réservation ---
    public void ajouterReservation(Reservation reservation) {
        // On suppose que la table s'appelle RESERVATION
        // et qu'elle a des clés étrangères : membre_id et seance_id
        String sql = "INSERT INTO RESERVATION (dateReservation, statut, membre_id, seance_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1. Date de la réservation (Date actuelle ou celle de l'objet)
            if (reservation.getDateReservation() != null) {
                stmt.setTimestamp(1, new Timestamp(reservation.getDateReservation().getTime()));
            } else {
                stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            }

            // 2. Statut (Enum -> String)
            stmt.setString(2, reservation.getStatut() != null ? reservation.getStatut().name() : "EN_ATTENTE");

            // 3. ID du Membre (Clé étrangère)
            // Attention : on vérifie que le membre n'est pas null
            if (reservation.getMembre() != null) {
                stmt.setInt(3, reservation.getMembre().getId());
            } else {
                throw new SQLException("Impossible de réserver sans Membre associé.");
            }

            // 4. ID de la Séance (Clé étrangère)
            if (reservation.getSeance() != null) {
                stmt.setInt(4, reservation.getSeance().getId());
            } else {
                throw new SQLException("Impossible de réserver sans Séance associée.");
            }

            stmt.executeUpdate();
            System.out.println("Réservation ajoutée avec succès !");

            // Récupérer l'ID généré (Auto-increment)
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur ajout réservation : " + e.getMessage());
        }
    }

    // --- READ : Lister toutes les réservations ---
    public List<Reservation> listerReservations() {
        List<Reservation> list = new ArrayList<>();
        // Idéalement, on ferait des JOIN ici pour récupérer les noms des membres et des séances
        // Mais pour rester simple, on récupère les IDs et on crée des objets "stub" (vides avec juste l'ID)
        String sql = "SELECT * FROM RESERVATION";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToReservation(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur listing réservations : " + e.getMessage());
        }
        return list;
    }

    // --- READ : Trouver par ID ---
    public Reservation trouverReservationParId(int id) {
        String sql = "SELECT * FROM RESERVATION WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReservation(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur recherche réservation : " + e.getMessage());
        }
        return null;
    }

    // --- READ : Trouver les réservations d'un membre ---
    public List<Reservation> trouverParMembre(int membreId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATION WHERE membre_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, membreId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapResultSetToReservation(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erreur recherche réservations membre : " + e.getMessage());
        }
        return list;
    }

    // --- UPDATE : Modifier une réservation (ex: Changer le statut) ---
    public void modifierReservation(Reservation reservation) {
        String sql = "UPDATE RESERVATION SET statut = ?, dateReservation = ?, seance_id = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, reservation.getStatut().name());
            stmt.setTimestamp(2, new Timestamp(reservation.getDateReservation().getTime()));
            stmt.setInt(3, reservation.getSeance().getId());
            stmt.setInt(4, reservation.getId());

            stmt.executeUpdate();
            System.out.println("Réservation modifiée !");
        } catch (SQLException e) {
            System.out.println("Erreur modification réservation : " + e.getMessage());
        }
    }

    // --- DELETE : Supprimer une réservation ---
    public void supprimerReservation(int id) {
        String sql = "DELETE FROM RESERVATION WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("Réservation supprimée !");

        } catch (SQLException e) {
            System.out.println("Erreur suppression réservation : " + e.getMessage());
        }
    }

    // --- Méthode utilitaire de Mapping (ResultSet -> Objet) ---
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));

        // Conversion Timestamp SQL -> Date Java
        Timestamp ts = rs.getTimestamp("dateReservation");
        if (ts != null) {
            r.setDateReservation(new java.util.Date(ts.getTime()));
        }

    // Conversion String -> Enum StatutReservation
    String statutStr = rs.getString("statut");
    if (statutStr != null) {
        try {
            r.setStatut(StatutReservation.valueOf(statutStr));
        } catch (IllegalArgumentException e) {
            System.out.println("Statut inconnu : " + statutStr);
        }
    }

    // --- GESTION DES RELATIONS (Membre et Séance) ---
    // Ici, on crée des objets temporaires contenant seulement l'ID.
    // Si vous avez besoin de toutes les infos du membre (nom, email...), 
    // il faudrait utiliser MembreRepository.trouverParId() ici.
    
    int membreId = rs.getInt("membre_id");
    if (membreId > 0) {
        Membre m = new Membre();
        m.setId(membreId);
        r.setMembre(m);
    }

    int seanceId = rs.getInt("seance_id");
    if (seanceId > 0) {
        Seance s = new Seance();
        s.setId(seanceId);
        r.setSeance(s);
    }

    return r;
}

// --- READ : Récupérer les réservations par période ---
public List<Reservation> getReservationsParPeriode(LocalDate debut, LocalDate fin) {
    List<Reservation> reservations = new ArrayList<>();
    
    // CORRECTION : utiliser s.type au lieu de s.typeCours
    String sql = "SELECT r.*, s.id as seance_id, s.nom, s.type, s.typeSeance, " +
                 "s.capaciteMax, s.dateHeure " +
                 "FROM reservation r " +
                 "JOIN seance s ON r.seance_id = s.id " +
                 "WHERE DATE(s.dateHeure) BETWEEN ? AND ?";
    
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setDate(1, java.sql.Date.valueOf(debut));
        stmt.setDate(2, java.sql.Date.valueOf(fin));
        
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            Reservation reservation = new Reservation();
            reservation.setId(rs.getInt("id"));
            // ... autres champs de réservation
            
            // Créer l'objet Seance associé
            Seance seance = new Seance();
            seance.setId(rs.getInt("seance_id"));
            seance.setNom(rs.getString("nom"));
            seance.setCapaciteMax(rs.getInt("capaciteMax"));
            seance.setDateHeure(rs.getTimestamp("dateHeure").toLocalDateTime());
            
            // CORRECTION : utiliser 'type' au lieu de 'typeCours'
            String typeStr = rs.getString("type");
            if (typeStr != null) {
                seance.setTypeCours(TypeCours.valueOf(typeStr));
            }
            
            String typeSeanceStr = rs.getString("typeSeance");
            if (typeSeanceStr != null) {
                seance.setTypeSeance(TypeSeance.valueOf(typeSeanceStr));
            }
            
            reservation.setSeance(seance);
            reservations.add(reservation);
        }
        
    } catch (SQLException e) {
        System.out.println("Erreur getReservationsParPeriode : " + e.getMessage());
    }
    
    return reservations;
}

}