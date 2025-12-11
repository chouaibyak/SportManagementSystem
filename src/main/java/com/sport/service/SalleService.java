package com.sport.service;

import java.util.Date;
import java.util.List;

import com.sport.model.Salle;
import com.sport.repository.SalleRepository;

public class SalleService {

    private SalleRepository salleRepository = new SalleRepository();

    // ➤ Ajouter une salle
    public void ajouterSalle(Salle salle) {
        salleRepository.ajouterSalle(salle);
    }

    // ➤ Modifier une salle
    public boolean modifierSalle(Salle salle) {
        return salleRepository.modifierSalle(salle);
    }

    // ➤ Supprimer une salle
    public boolean supprimerSalle(int id) {
        return salleRepository.supprimerSalle(id);
    }

    // ➤ Lister toutes les salles
    public List<Salle> getToutesLesSalles() {
        return salleRepository.listerSalles();
    }

    // ➤ Obtenir une salle par son ID
    public Salle getSalleById(int id) {
        return salleRepository.getSalleById(id);
    }

    // ➤ Vérifier disponibilité d’une salle
    public boolean salleDisponible(int idSalle, Date date) {
        Salle salle = salleRepository.getSalleById(idSalle);

        if (salle == null) {
            System.out.println("Salle introuvable ID=" + idSalle);
            return false;
        }

        return salle.verifierDisponibilite(date);
    }
}
