package com.sport.service;

import java.util.List;

import com.sport.model.Membre;
import com.sport.model.SeanceIndividuelle;
import com.sport.repository.SeanceIndividuelleRepository;

public class SeanceIndividuelleService {

    private final SeanceIndividuelleRepository repository;

    public SeanceIndividuelleService(SeanceIndividuelleRepository repository) {
        this.repository = repository;
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
}
