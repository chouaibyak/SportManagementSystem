package com.sport.service;

import java.util.List;

import com.sport.model.Equipement;
import com.sport.repository.EquipementRepository;

public class EquipementService {

    private EquipementRepository equipementRepository = new EquipementRepository();


    public EquipementService(EquipementRepository equipementRepository) {
        this.equipementRepository = equipementRepository;
    }

    // Ajouter un équipement
    public void ajouterEquipement(Equipement equipement) {
        equipementRepository.ajouterEquipement(equipement);
    }

    // Modifier
    public boolean modifierEquipement(Equipement equipement) {
        return equipementRepository.modifierEquipement(equipement);
    }

    // Supprimer
    public boolean supprimerEquipement(int id) {
        return equipementRepository.supprimerEquipement(id);
    }

    // Liste
    public List<Equipement> listerEquipements() {
        return equipementRepository.listerEquipements();
    }

    // Par ID
    public Equipement getById(int id) {
        return equipementRepository.getEquipementById(id);
    }

    // Appel logique du modèle
    public void planifierMaintenance(int id) {
        Equipement eq = equipementRepository.getEquipementById(id);
        if (eq != null) {
            eq.planifierMaintenance();
            equipementRepository.modifierEquipement(eq);
        }
    }

    public void marquerDisponible(int id) {
        Equipement eq = equipementRepository.getEquipementById(id);
        if (eq != null) {
            eq.marquerDisponible();
            equipementRepository.modifierEquipement(eq);
        }
    }

    public void marquerHorsService(int id) {
        Equipement eq = equipementRepository.getEquipementById(id);
        if (eq != null) {
            eq.marquerHorsService();
            equipementRepository.modifierEquipement(eq);
        }
    }
}
