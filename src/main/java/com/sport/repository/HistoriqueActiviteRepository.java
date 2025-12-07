package com.sport.repository;

import com.sport.model.HistoriqueActivite;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HistoriqueActiviteRepository {

    // Simulation de la BDD en mémoire
    private static List<HistoriqueActivite> historiqueBDD = new ArrayList<>();
    private static int compteurId = 1;

    // CREATE : Sauvegarder une activité
    public void ajouter(HistoriqueActivite activite) {
        activite.setId(compteurId++); // Génération auto de l'ID
        historiqueBDD.add(activite);
    }

    // READ : Tout récupérer
    public List<HistoriqueActivite> listerTout() {
        return new ArrayList<>(historiqueBDD);
    }

    // READ : Récupérer UNIQUEMENT l'historique d'un membre spécifique
    public List<HistoriqueActivite> trouverParMembreId(int membreId) {
        // On filtre la liste pour ne garder que ceux qui appartiennent à ce membre
        return historiqueBDD.stream()
                .filter(act -> act.getMembre().getId() == membreId)
                .collect(Collectors.toList());
    }

    // DELETE : Supprimer une activité par son ID
    public void supprimer(int id) {
        historiqueBDD.removeIf(act -> act.getId() == id);
    }
}