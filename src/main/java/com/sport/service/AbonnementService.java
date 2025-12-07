package com.sport.service;

import com.sport.model.Abonnement;
import com.sport.model.StatutAbonnement;
import com.sport.repository.AbonnementRepository;

import java.util.Date;
import java.util.List;

public class AbonnementService {

    private AbonnementRepository abonnementRepository;

    public AbonnementService() {
        this.abonnementRepository = new AbonnementRepository();
    }

    /**
     * Crée un nouvel abonnement, le met en statut ACTIF par défaut et le sauvegarde.
     */
    public void souscrireAbonnement(Abonnement abonnement) {
        // Validation
        if (abonnement.getTypeAbonnement() == null) {
            throw new IllegalArgumentException("Le type d'abonnement est obligatoire.");
        }

        // Logique métier : Un nouvel abonnement commence souvent "ACTIF" ou "EN_ATTENTE"
        // Utilisation de votre méthode modèle
        abonnement.activerAbonnement(); 
        
        // Sauvegarde
        abonnementRepository.ajouter(abonnement);
        
        System.out.println("Abonnement " + abonnement.getTypeAbonnement() + " créé avec succès.");
        System.out.println("Date de fin prévue : " + abonnement.calculerProchaineDateFin());
    }

    /**
     * Résilie un abonnement existant via son ID.
     */
    public void resilierAbonnement(int id) {
        Abonnement ab = abonnementRepository.trouverParId(id);
        if (ab != null) {
            ab.resilierAbonnement(); // Méthode du modèle
            // En BDD réelle, on ferait un repository.update(ab), ici la référence suffit en mémoire
            System.out.println("L'abonnement a été résilié.");
        } else {
            System.out.println("Abonnement introuvable.");
        }
    }

    /**
     * Vérifie si un abonnement est toujours valide par rapport à la date du jour.
     */
    public boolean verifierValidite(Abonnement abonnement) {
        if (abonnement.getStatutAbonnement() != StatutAbonnement.ACTIF) {
            return false;
        }
        
        Date dateFin = abonnement.calculerProchaineDateFin();
        Date aujourdhui = new Date();

        // Si la date de fin est avant aujourd'hui -> Expiré
        if (dateFin.before(aujourdhui)) {
            abonnement.setStatutAbonnement(StatutAbonnement.EXPIRE);
            return false;
        }
        return true;
    }

    public List<Abonnement> recupererTousLesAbonnements() {
        return abonnementRepository.listerTout();
    }
}