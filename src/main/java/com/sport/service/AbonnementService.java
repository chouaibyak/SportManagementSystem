package com.sport.service;

import java.util.Date;
import java.util.List;

import com.sport.model.Abonnement;
import com.sport.model.StatutAbonnement;
import com.sport.repository.AbonnementRepository;

public class AbonnementService {

    private AbonnementRepository abonnementRepository = new AbonnementRepository();
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
        abonnementRepository.ajouterAbonnement(abonnement);
        
        System.out.println("Abonnement " + abonnement.getTypeAbonnement() + " créé avec succès.");
        System.out.println("Date de fin prévue : " + abonnement.calculerProchaineDateFin());
    }

    /**
     * Résilie un abonnement existant via son ID.
     */
    public void resilierAbonnement(int id) {
        Abonnement ab = abonnementRepository.trouverParId(id);
        if (ab != null) {
            ab.resilierAbonnement(); 
            // CORRECTION : Sauvegarder la modif en BDD !
            abonnementRepository.modifierAbonnement(ab);
            System.out.println("L'abonnement a été résilié et mis à jour en BDD.");
        } else {
            System.out.println("Abonnement introuvable.");
        }
    }

    // Ajout utile pour le test
    public Abonnement recupererParId(int id) {
        return abonnementRepository.trouverParId(id);
    }
    
    public void supprimerAbonnement(int id) {
        abonnementRepository.supprimerAbonnement(id);
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