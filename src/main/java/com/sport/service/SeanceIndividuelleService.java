package com.sport.service;

import java.util.List;

import com.sport.model.Membre;
import com.sport.model.SeanceIndividuelle;
import com.sport.repository.SeanceIndividuelleRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class SeanceIndividuelleService {

    private final SeanceIndividuelleRepository repository;

    public SeanceIndividuelleService(SeanceIndividuelleRepository repository) {
        this.repository = repository;
    }

    public int getMembreInscrit(int seanceId) {
        return repository.getMembreInscrit(seanceId);
    }

    public boolean reserverSession(int seanceId, Membre membre) {
        return repository.reserverSession(seanceId, membre.getId());
    }

    // Ajouter une séance
    public void ajouterSeance(SeanceIndividuelle seance) {
        repository.ajouter(seance);
    }

    // Obtenir toutes les séances individuelles
    public List<SeanceIndividuelle> getAll() {
        return repository.getAll();
    }

    // Trouver par ID
    public SeanceIndividuelle getById(int id) {
        return repository.getById(id);
    }

    // Modifier une séance
    public boolean update(SeanceIndividuelle seance) {
        return repository.update(seance);
    }

    // Supprimer
    public boolean delete(int id) {
        return repository.delete(id);
    }

    // Associer un membre
    public boolean assignerMembre(int idSeance, Membre membre) {
        SeanceIndividuelle seance = repository.getById(idSeance);
        if (seance == null) return false;

        seance.setMembre(membre);
        return repository.update(seance);
    }

    // Ajouter une note de coach
    public boolean ajouterNoteCoach(int idSeance, String note) {
        SeanceIndividuelle seance = repository.getById(idSeance);
        if (seance == null) return false;

        seance.setNotesCoach(note);
        return repository.update(seance);
    }

    // Calcul du tarif final (ex: promotions plus tard)
    public double calculerTarifFinal(int idSeance) {
        SeanceIndividuelle seance = repository.getById(idSeance);
        if (seance == null) return 0;

        return seance.getTarif();
    }


// --- MÉTHODES POUR DASHBOARD COACH ---

// Retourne toutes les séances individuelles du coach donné
public List<SeanceIndividuelle> getSeancesByCoach(int coachId) {
    return getAll().stream()
            .filter(s -> s.getEntraineur().getId() == coachId)
            .collect(Collectors.toList());
}

// Nombre de séances individuelles aujourd'hui pour un coach
public long getNbSeancesToday(int coachId) {
    LocalDate today = LocalDate.now();
    return getSeancesByCoach(coachId).stream()
            .filter(s -> s.getDateHeure().toLocalDate().isEqual(today))
            .count();
}

// Prochaine séance individuelle du coach
public SeanceIndividuelle getNextSeance(int coachId) {
    LocalDateTime now = LocalDateTime.now();
    return getSeancesByCoach(coachId).stream()
            .filter(s -> s.getDateHeure().isAfter(now))
            .sorted((s1, s2) -> s1.getDateHeure().compareTo(s2.getDateHeure()))
            .findFirst()
            .orElse(null);
}

// Nombre total de membres suivis en séances individuelles par le coach
public long getNbMembresSuivis(int coachId) {
    return getSeancesByCoach(coachId).stream()
            .map(s -> s.getMembre())
            .distinct()
            .count();
}



}
