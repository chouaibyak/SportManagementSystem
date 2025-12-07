package com.sport.repository;

import com.sport.model.Abonnement;
import com.sport.model.StatutAbonnement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AbonnementRepository {

    // Simulation BDD
    private static List<Abonnement> abonnementsBDD = new ArrayList<>();
    private static int compteurId = 1;

    // CREATE
    public void ajouter(Abonnement abonnement) {
        // On suppose que vous ajouterez un setId() dans votre modèle Abonnement
        // abonnement.setId(compteurId++); 
        abonnementsBDD.add(abonnement);
    }

    // READ (Tout)
    public List<Abonnement> listerTout() {
        return new ArrayList<>(abonnementsBDD);
    }

    // READ (Par ID)
    public Abonnement trouverParId(int id) {
        // Note : Il faut ajouter un getter getId() dans votre classe Abonnement
        // Ici je simule la recherche :
        /*
        return abonnementsBDD.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
        */
        // Pour l'instant, sans getter ID accessible dans votre code fourni :
        if (id < abonnementsBDD.size() && id >= 0) {
            return abonnementsBDD.get(id); // Simple simulation par index
        }
        return null;
    }

    // READ (Par Statut - ex: trouver tous les ACTIFS)
    public List<Abonnement> trouverParStatut(StatutAbonnement statut) {
        return abonnementsBDD.stream()
                .filter(a -> a.getStatutAbonnement() == statut)
                .collect(Collectors.toList());
    }

    // DELETE
    public void supprimer(Abonnement abonnement) {
        abonnementsBDD.remove(abonnement);
    }
}