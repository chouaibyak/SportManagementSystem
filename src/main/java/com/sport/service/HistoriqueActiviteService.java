package com.sport.service;

import com.sport.model.HistoriqueActivite;
import com.sport.repository.HistoriqueActiviteRepository;
import java.time.LocalDate;
import java.util.List;

public class HistoriqueActiviteService {

    private HistoriqueActiviteRepository historiqueRepository;

    public HistoriqueActiviteService() {
        this.historiqueRepository = new HistoriqueActiviteRepository();
    }

    // Méthode pour ajouter une activité avec validation
    public void creerActivite(HistoriqueActivite activite) {
        // 1. Validation de la durée
        if (activite.getDuree() <= 0) {
            throw new IllegalArgumentException("La durée de l'activité doit être positive.");
        }

        // 2. Validation de la date
        if (activite.getDate() == null) {
            throw new IllegalArgumentException("La date de l'activité est obligatoire.");
        }
        
        // 3. Validation du membre
        if (activite.getMembre() == null) {
            throw new IllegalArgumentException("L'activité doit être liée à un membre existant.");
        }
        
        // Si tout est bon, on sauvegarde
        historiqueRepository.ajouter(activite);
    }

    // Récupérer l'historique d'un membre précis (pour l'affichage dans l'app)
    public List<HistoriqueActivite> recupererHistoriqueParMembre(int membreId) {
        return historiqueRepository.trouverParMembreId(membreId);
    }

    // Supprimer une activité
    public void supprimerActivite(int id) {
        historiqueRepository.supprimer(id);
    }
}