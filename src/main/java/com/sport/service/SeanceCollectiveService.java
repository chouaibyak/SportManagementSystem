package com.sport.service;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.sport.model.Membre;
import com.sport.model.SeanceCollective;
import com.sport.repository.SeanceCollectiveRepository;
import com.sport.utils.DBConnection;

public class SeanceCollectiveService {

    private final SeanceCollectiveRepository repository;

    public SeanceCollectiveService(SeanceCollectiveRepository repository) {
        this.repository = repository;
    }

    // --- MÉTHODES CRUD DE BASE ---

    // Ajouter une séance
    public void ajouterSeance(SeanceCollective seance) {
        repository.ajouter(seance);
    }

    public List<SeanceCollective> getAll() {
        return repository.getAll();
    }

    public SeanceCollective getById(int id) {
        return repository.getById(id);
    }

    // Modifier une séance (Nom, date, etc.)
    public boolean update(SeanceCollective seance) {
        return repository.update(seance);
    }

    public boolean delete(int id) {
        return repository.delete(id);
    }

    // --- MÉTHODE CRITIQUE : RÉSERVATION ---

    public boolean reserverPlace(int idSeance, Membre membre) {
        // 1. Vérification Métier : Est-ce que la séance existe ?
        SeanceCollective seance = repository.getById(idSeance);
        
        if (seance == null) {
            System.err.println("[Service] Erreur : Séance collective introuvable (ID: " + idSeance + ")");
            return false;
        }

        // 2. Vérification Métier : Reste-t-il de la place ?
        if (seance.getPlacesDisponibles() <= 0) {
            System.err.println("[Service] Erreur : Séance complète.");
            return false;
        }

        // 3. APPEL AU REPOSITORY (TRANSACTION SQL)
        // C'est ici que la magie opère : on insère dans la table de liaison ET on décrémente le compteur
        boolean succes = repository.reserverPlace(idSeance, membre);
        
        if (succes) {
            System.out.println("[Service] Succès : Place réservée pour " + membre.getNom());
        } else {
            System.err.println("[Service] Echec : Erreur lors de la réservation SQL.");
        }
        
        return succes;
    }

    // Annuler réservation (Logique simplifiée)
    // Dans SeanceCollectiveService.java
    public boolean annulerReservation(int idSeance, Membre membre) {
        System.out.println("[SERVICE] Appel du repository pour libérer la séance " + idSeance);
        return repository.annulerReservation(idSeance, membre);
    }

    // Placeholder pour notification
    public void notifierParticipants(int idSeance, String message) {
        SeanceCollective seance = repository.getById(idSeance);
        if (seance == null) return;
        // Logique d'envoi de mail ou notification ici...
        System.out.println("Notification envoyée pour la séance " + idSeance);
    }

    // --- Méthodes pour dashboard coach ---

    // --- MÉTHODES POUR DASHBOARD COACH (Statistiques) ---

    // Retourne toutes les séances du coach donné
    public List<SeanceCollective> getSeancesByCoach(int coachId) {
        return getAll().stream()
                .filter(s -> s.getEntraineur().getId() == coachId)
                .collect(Collectors.toList());
    }

    // Nombre de séances aujourd'hui pour un coach
    public long getNbSeancesToday(int coachId) {
        LocalDate today = LocalDate.now();
        return getSeancesByCoach(coachId).stream()
                .filter(s -> s.getDateHeure().toLocalDate().isEqual(today))
                .count();
    }

    // Prochaine séance du coach
    public SeanceCollective getNextSeance(int coachId) {
        LocalDateTime now = LocalDateTime.now();
        return getSeancesByCoach(coachId).stream()
                .filter(s -> s.getDateHeure().isAfter(now))
                .sorted((s1, s2) -> s1.getDateHeure().compareTo(s2.getDateHeure()))
                .findFirst()
                .orElse(null);
    }

    // Nombre total de membres suivis par le coach
    public long getNbMembresSuivis(int coachId) {
        // Cette méthode est approximative car elle se base sur les objets chargés en mémoire.
        // Pour plus de précision, il faudrait une requête SQL "COUNT(DISTINCT...)"
        return getSeancesByCoach(coachId).stream()
                .flatMap(s -> s.getListeMembers().stream())
                .distinct()
                .count();
    }

    // À ajouter dans SeanceIndividuelleRepository.java
    public boolean reserverSession(int idSeance, int idMembre) {
        // On met à jour le membre_id dans la table seanceindividuelle
        String sql = "UPDATE seanceindividuelle SET membre_id = ? WHERE seance_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idMembre);
            stmt.setInt(2, idSeance);
            
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
