package com.sport.service;

import com.sport.model.Performance;
import com.sport.repository.PerformanceRepository;

import java.time.LocalDate;
import java.util.List;

public class PerformanceService {

    private PerformanceRepository performanceRepository;

    public PerformanceService() {
        this.performanceRepository = new PerformanceRepository();
    }

    public void enregistrerPerformance(Performance perf) {
        // Validation basique
        if (perf.getPoids() <= 0) {
            System.out.println("Erreur : Poids invalide.");
            return;
        }

        if (perf.getMembre() == null) {
            System.out.println("Erreur : Membre requis.");
            return;
        }

        // Si la date n'est pas définie, on met la date du jour
        if (perf.getDateMesure() == null) {
            perf.setDateMesure(LocalDate.now());
        }

        performanceRepository.ajouterPerformance(perf);
    }

    public List<Performance> recupererHistoriqueMembre(int membreId) {
        return performanceRepository.trouverPerformanceParMembreId(membreId);
    }

    public Performance recupererDernierePerformance(int membreId) {
        List<Performance> historique = performanceRepository.trouverPerformanceParMembreId(membreId);
        if (historique.isEmpty()) {
            return null;
        }
        // Retourne le dernier élément (le plus récent car trié par date)
        return historique.get(historique.size() - 1);
    }

    public void supprimerPerformance(int id) {
        performanceRepository.supprimerPerformance(id);
    }
}