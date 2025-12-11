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

    /**
     * Enregistre une nouvelle performance avec validation.
     */
    public void enregistrerPerformance(Performance perf) {
        // 1. Validation des données physiques
        if (perf.getPoids() <= 0 || perf.getTourTaille() <= 0) {
            throw new IllegalArgumentException("Le poids et le tour de taille doivent être positifs.");
        }

        // 2. Validation de la date
        if (perf.getDateMesure() == null) {
            perf.setDateMesure(LocalDate.now()); // Date par défaut = aujourd'hui
        }

        // 3. Validation du membre
        if (perf.getMembre() == null) {
            throw new IllegalArgumentException("La performance doit être associée à un membre.");
        }

        performanceRepository.ajouterPerformance(perf);
        System.out.println("Performance enregistrée pour " + perf.getMembre().getNom());
    }

    //Récupère l'historique complet d'un membre.
    public List<Performance> recupererHistoriqueMembre(int membreId) {
        return performanceRepository.trouverPerformanceParMembreId(membreId);
    }

    //Récupère la toute dernière performance enregistrée pour un membre (pour voir l'état actuel).
    public Performance recupererDernierePerformance(int membreId) {
        List<Performance> historique = performanceRepository.trouverPerformanceParMembreId(membreId);
        if (historique.isEmpty()) {
            return null;
        }
        // Comme la liste est triée par date dans le repo, on prend le dernier élément
        return historique.get(historique.size() - 1);
    }

    /**
     * Supprime une performance (erreur de saisie).
     */
    public void supprimerPerformance(int id) {
        performanceRepository.supprimerPerformance(id);
    }
}