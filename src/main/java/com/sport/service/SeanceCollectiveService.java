package com.sport.service;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.sport.model.Membre;
import com.sport.model.SeanceCollective;
import com.sport.repository.SeanceCollectiveRepository;

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

    // Obtenir toutes les séances collectives
    public List<SeanceCollective> getAll() {
        return repository.getAll();
    }

    // Obtenir par ID
    public SeanceCollective getById(int id) {
        return repository.getById(id);
    }

    // Modifier une séance (Nom, date, etc.)
    public boolean update(SeanceCollective seance) {
        return repository.update(seance);
    }

    // Supprimer
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
    public boolean annulerReservation(int idSeance, Membre membre) {
        SeanceCollective seance = repository.getById(idSeance);
        if (seance == null) return false;

        // Note: Idéalement, il faudrait aussi une méthode SQL spécifique 'annulerReservation' 
        // dans le repository pour supprimer la ligne dans seancecollective_membre
        seance.setPlacesDisponibles(seance.getPlacesDisponibles() + 1);
        return repository.update(seance);
    }

    // Placeholder pour notification
    public void notifierParticipants(int idSeance, String message) {
        SeanceCollective seance = repository.getById(idSeance);
        if (seance == null) return;
        // Logique d'envoi de mail ou notification ici...
        System.out.println("Notification envoyée pour la séance " + idSeance);
    }


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
}