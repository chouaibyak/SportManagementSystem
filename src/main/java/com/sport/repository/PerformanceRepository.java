package com.sport.repository;

import com.sport.model.Performance;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PerformanceRepository {

    // Simulation BDD
    private static List<Performance> performancesBDD = new ArrayList<>();
    private static int compteurId = 1;

    // CREATE
    public void ajouter(Performance perf) {
        perf.setId(compteurId++);
        performancesBDD.add(perf);
    }

    // READ (Tout)
    public List<Performance> listerTout() {
        return new ArrayList<>(performancesBDD);
    }

    // READ (Par ID)
    public Performance trouverParId(int id) {
        return performancesBDD.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // READ (Spécifique à un Membre)
    // Utile pour tracer des graphiques de progression
    public List<Performance> trouverParMembreId(int membreId) {
        return performancesBDD.stream()
                .filter(p -> p.getMembre().getId() == membreId)
                // On trie par date pour avoir l'ordre chronologique
                .sorted(Comparator.comparing(Performance::getDateMesure))
                .collect(Collectors.toList());
    }

    // UPDATE
    public void modifier(Performance perfModifiee) {
        for (int i = 0; i < performancesBDD.size(); i++) {
            if (performancesBDD.get(i).getId() == perfModifiee.getId()) {
                performancesBDD.set(i, perfModifiee);
                return;
            }
        }
    }

    // DELETE
    public void supprimer(int id) {
        performancesBDD.removeIf(p -> p.getId() == id);
    }
}