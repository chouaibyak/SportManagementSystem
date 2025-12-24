package com.sport;

import com.sport.model.Abonnement;
import com.sport.model.Membre;
import com.sport.model.StatutAbonnement;
import com.sport.model.TypeAbonnement;
import com.sport.model.TypeObjectif;
import com.sport.model.TypePreference;
import com.sport.service.AbonnementService;
import com.sport.service.MembreService;

public class TestAbonnement {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("      TEST MODULE ABONNEMENT");
        System.out.println("==========================================");

        MembreService membreService = new MembreService();
        AbonnementService aboService = new AbonnementService();

        // ---------------------------------------------------------------
        // 0. PRÉPARATION : Création d'un Membre (Obligatoire)
        // ---------------------------------------------------------------
        System.out.println("\n--- 0. PRÉPARATION (Création Membre) ---");
        Membre membre = new Membre(
            "Testeur", "Abo", "1999-01-01", 
            "@test.com" , "0600000000", 
            "Paris", TypeObjectif.PERTE_POIDS, TypePreference.CARDIO
        );
        membreService.creerMembre(membre);

        if (membre.getId() == 0) {
            System.out.println("❌ Erreur critique : Impossible de créer le membre.");
            return;
        }
        System.out.println("✅ Membre temporaire créé ID : " + membre.getId());

        // ---------------------------------------------------------------
        // 1. TEST SOUSCRIPTION (Create)
        // ---------------------------------------------------------------
        System.out.println("\n--- 1. TEST SOUSCRIPTION ---");
        
        Abonnement nouvelAbo = new Abonnement();
        nouvelAbo.setMembre(membre);
        nouvelAbo.setTypeAbonnement(TypeAbonnement.MENSUEL);
        nouvelAbo.setAutorenouvellement(true);
        // Le statut sera mis à ACTIF par le service automatiquement

        aboService.souscrireAbonnement(nouvelAbo);

        if (nouvelAbo.getId() > 0) {
            System.out.println("✅ Abonnement créé avec succès ! ID : " + nouvelAbo.getId());
        } else {
            System.out.println("❌ Erreur : L'ID de l'abonnement n'a pas été généré.");
            return;
        }

        // ---------------------------------------------------------------
        // 2. TEST LECTURE ET VALIDITÉ (Read)
        // ---------------------------------------------------------------
        System.out.println("\n--- 2. TEST LECTURE ---");
        
        Abonnement aboRecupere = aboService.recupererParId(nouvelAbo.getId());

        if (aboRecupere != null) {
            System.out.println("✅ Abonnement récupéré en BDD.");
            System.out.println("   - Type : " + aboRecupere.getTypeAbonnement());
            System.out.println("   - Statut : " + aboRecupere.getStatutAbonnement());
            System.out.println("   - Renouvellement auto : " + aboRecupere.isAutorenouvellement());
            
            // Test de la date de fin
            System.out.println("   - Date de fin théorique : " + aboRecupere.calculerProchaineDateFin());
        } else {
            System.out.println("❌ Erreur : Abonnement introuvable.");
        }

        // ---------------------------------------------------------------
        // 3. TEST RÉSILIATION (Update)
        // ---------------------------------------------------------------
        System.out.println("\n--- 3. TEST RÉSILIATION ---");
        
        aboService.resilierAbonnement(nouvelAbo.getId());

        // On recharge depuis la base pour être sûr que la modif est sauvegardée
        Abonnement aboApresResil = aboService.recupererParId(nouvelAbo.getId());
        
        if (aboApresResil.getStatutAbonnement() == StatutAbonnement.RESILIE) {
            System.out.println("✅ L'abonnement est bien passé en statut RESILIE en BDD.");
        } else {
            System.out.println("❌ Erreur : Le statut est toujours " + aboApresResil.getStatutAbonnement());
        }

        // ---------------------------------------------------------------
        // 4. TEST SUPPRESSION (Delete)
        // ---------------------------------------------------------------
        System.out.println("\n--- 4. TEST SUPPRESSION ---");
        
        aboService.supprimerAbonnement(nouvelAbo.getId());
        
        Abonnement checkSuppression = aboService.recupererParId(nouvelAbo.getId());
        if (checkSuppression == null) {
            System.out.println("✅ Abonnement supprimé avec succès.");
        } else {
            System.out.println("❌ Erreur : L'abonnement existe toujours.");
        }

        // Nettoyage du membre
        membreService.supprimerMembre(membre.getId());
        System.out.println("   (Membre temporaire nettoyé)");

        System.out.println("\n==========================================");
        System.out.println("       FIN DU TEST ABONNEMENT");
        System.out.println("==========================================");
    }
}