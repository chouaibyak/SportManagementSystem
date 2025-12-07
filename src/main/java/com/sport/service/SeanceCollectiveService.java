package com.sport.service;

import java.util.List;

import com.sport.model.Membre;
import com.sport.model.SeanceCollective;
import com.sport.repository.SeanceCollectiveRepository;

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
}
