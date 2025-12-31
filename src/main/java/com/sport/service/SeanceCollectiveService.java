package com.sport.service;

import com.sport.model.Membre;
import com.sport.model.SeanceCollective;
import com.sport.repository.SeanceCollectiveRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SeanceCollectiveService {

    private final SeanceCollectiveRepository repository;

    public SeanceCollectiveService(SeanceCollectiveRepository repository) {
        this.repository = repository;
    }

    // --- CRUD Séance ---
    
    public void ajouterSeance(SeanceCollective seance) {
        repository.ajouter(seance);
    }

    public List<SeanceCollective> getAll() {
        return repository.getAll();
    }

    public SeanceCollective getById(int id) {
        return repository.getById(id);
    }

    public boolean update(SeanceCollective seance) {
        return repository.update(seance);
    }

    public boolean delete(int id) {
        return repository.delete(id);
    }

    // --- Réservation / Annulation ---

    public boolean reserverPlace(int idSeance, Membre membre) {
        return repository.reserverPlace(idSeance, membre);
    }

    public boolean annulerReservation(int idSeance, Membre membre) {
        return repository.annulerReservation(idSeance, membre);
    }

    // --- Notifications (placeholder) ---

    public void notifierParticipants(int idSeance, String message) {
        SeanceCollective seance = repository.getById(idSeance);
        if (seance == null) return;

        for (Membre membre : seance.getListeMembers()) {
            System.out.println("[Notification] " + membre.getNom() + ": " + message);
        }
    }

    // --- Méthodes pour dashboard coach ---

    public List<SeanceCollective> getSeancesByCoach(int coachId) {
        return getAll().stream()
                .filter(s -> s.getEntraineur().getId() == coachId)
                .collect(Collectors.toList());
    }

    public long getNbSeancesToday(int coachId) {
        LocalDate today = LocalDate.now();
        return getSeancesByCoach(coachId).stream()
                .filter(s -> s.getDateHeure().toLocalDate().isEqual(today))
                .count();
    }

    public SeanceCollective getNextSeance(int coachId) {
        LocalDateTime now = LocalDateTime.now();
        return getSeancesByCoach(coachId).stream()
                .filter(s -> s.getDateHeure().isAfter(now))
                .sorted((s1, s2) -> s1.getDateHeure().compareTo(s2.getDateHeure()))
                .findFirst()
                .orElse(null);
    }

    public long getNbMembresSuivis(int coachId) {
        return getSeancesByCoach(coachId).stream()
                .flatMap(s -> s.getListeMembers().stream())
                .distinct()
                .count();
    }
}
