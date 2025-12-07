package com.sport.service;

import java.util.List;

import com.sport.model.Equipement;
import com.sport.repository.EquipementRepository;

public class EquipementService {

    private final EquipementRepository repository;

    public EquipementService(EquipementRepository repository) {
        this.repository = repository;
    }

    // Ajouter un équipement
    public void ajouterEquipement(Equipement equipement) {
        repository.ajouter(equipement);
    }

    // Modifier
    public boolean modifierEquipement(Equipement equipement) {
        return repository.update(equipement);
    }

    // Supprimer
    public boolean supprimerEquipement(int id) {
        return repository.delete(id);
    }

    // Liste
    public List<Equipement> listerEquipements() {
        return repository.getAll();
    }

    // Par ID
    public Equipement getById(int id) {
        return repository.getById(id);
    }

    // Appel logique du modèle
    public void planifierMaintenance(int id) {
        Equipement eq = repository.getById(id);
        if (eq != null) {
            eq.planifierMaintenance();
            repository.update(eq);
        }
    }

    public void marquerDisponible(int id) {
        Equipement eq = repository.getById(id);
        if (eq != null) {
            eq.marquerDisponible();
            repository.update(eq);
        }
    }

    public void marquerHorsService(int id) {
        Equipement eq = repository.getById(id);
        if (eq != null) {
            eq.marquerHorsService();
            repository.update(eq);
        }
    }
}
