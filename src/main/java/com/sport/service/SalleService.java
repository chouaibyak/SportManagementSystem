package com.sport.service;

import java.util.Date;
import java.util.List;

import com.sport.model.Salle;
import com.sport.repository.SalleRepository;

public class SalleService {

    private final SalleRepository repository;

    public SalleService(SalleRepository repository) {
        this.repository = repository;
    }

    // ➤ Ajouter une salle
    public void ajouterSalle(Salle salle) {
        repository.ajouter(salle);
    }

    // ➤ Modifier une salle
    public boolean modifierSalle(Salle salle) {
        return repository.update(salle);
    }

    // ➤ Supprimer une salle
    public boolean supprimerSalle(int id) {
        return repository.delete(id);
    }

    // ➤ Lister toutes les salles
    public List<Salle> getToutesLesSalles() {
        return repository.getAll();
    }

    // ➤ Obtenir une salle par son ID
    public Salle getSalleById(int id) {
        return repository.getById(id);
    }

    // ➤ Vérifier disponibilité d’une salle
    public boolean salleDisponible(int idSalle, Date date) {
        Salle salle = repository.getById(idSalle);

        if (salle == null) {
            System.out.println("Salle introuvable ID=" + idSalle);
            return false;
        }

        return salle.verifierDisponibilite(date);
    }
}
