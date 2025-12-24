package com.sport.service;

import java.util.List;

import com.sport.model.Membre;
import com.sport.model.SeanceCollective;
import com.sport.repository.SeanceCollectiveRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;


public class SeanceCollectiveService {

    private final SeanceCollectiveRepository repository;

    public SeanceCollectiveService(SeanceCollectiveRepository repository) {
        this.repository = repository;
    }

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

    // Modifier une séance
    public boolean update(SeanceCollective seance) {
        return repository.update(seance);
    }

    // Supprimer
    public boolean delete(int id) {
        return repository.delete(id);
    }

    // Réserver une place
    public boolean reserverPlace(int idSeance, Membre membre) {

        SeanceCollective seance = repository.getById(idSeance);
        if (seance == null) return false;

        if (seance.getPlacesDisponibles() <= 0) return false;

        // Ajouter membre
        seance.getListeMembers().add(membre);

        // Réduire places
        seance.setPlacesDisponibles(seance.getPlacesDisponibles() - 1);

        return repository.update(seance);
    }

    // Annuler réservation
    public boolean annulerReservation(int idSeance, Membre membre) {

        SeanceCollective seance = repository.getById(idSeance);
        if (seance == null) return false;

        boolean removed = seance.getListeMembers().remove(membre);
        if (!removed) return false;

        // Libérer une place
        seance.setPlacesDisponibles(seance.getPlacesDisponibles() + 1);

        return repository.update(seance);
    }

    // Notifier les membres (placeholder)
    public void notifierParticipants(int idSeance, String message) {

        SeanceCollective seance = repository.getById(idSeance);
        if (seance == null) return;

        for (Membre membre : seance.getListeMembers()) {
            System.out.println("[Notification] " + membre.getNom() + ": " + message);
        }
    }


// --- MÉTHODES POUR DASHBOARD COACH ---

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
    return getSeancesByCoach(coachId).stream()
            .flatMap(s -> s.getListeMembers().stream())
            .distinct()
            .count();
}


}
