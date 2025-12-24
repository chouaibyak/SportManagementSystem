package com.sport;

import com.sport.model.Membre;
import com.sport.model.Performance;
import com.sport.model.TypeObjectif;
import com.sport.model.TypePreference;
import com.sport.service.MembreService;
import com.sport.service.PerformanceService;

import java.time.LocalDate;
import java.util.List;

public class TestPerformance {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("      TEST MODULE PERFORMANCE");
        System.out.println("==========================================");

        MembreService membreService = new MembreService();
        PerformanceService perfService = new PerformanceService();

        // ---------------------------------------------------------------
        // 0. PRÉPARATION : Création d'un membre pour le test
        // ---------------------------------------------------------------
        System.out.println("\n--- 0. PRÉPARATION ---");
        long timestamp = System.currentTimeMillis();
        Membre membre = new Membre(
            "Testeur",
            "Perf",
            "1995-05-20",
            "perf." + timestamp + "@test.com",
            "0102030405",
            "Gym Street",
            TypeObjectif.RENFORCEMENT,
            TypePreference.CARDIO 
        );
        membreService.creerMembre(membre);
        
        if (membre.getId() == 0) {
            System.out.println("❌ Erreur critique : Impossible de créer le membre.");
            return;
        }
        System.out.println("✅ Membre créé avec ID : " + membre.getId());

        // ---------------------------------------------------------------
        // 1. TEST ENREGISTREMENT (Create)
        // ---------------------------------------------------------------
        System.out.println("\n--- 1. TEST ENREGISTREMENT PERFORMANCE ---");
        
        // Perf 1 : Il y a un mois (85 kg)
        Performance p1 = new Performance(membre, 85.0, 90.0, 50.0, 10.0, LocalDate.now().minusMonths(1));
        p1.calculerIMC(1.80); // On calcule l'IMC avant l'enregistrement
        
        // Perf 2 : Aujourd'hui (82 kg)
        Performance p2 = new Performance(membre, 82.0, 88.0, 55.0, 12.0, LocalDate.now());
        p2.calculerIMC(1.80);

        try {
            perfService.enregistrerPerformance(p1);
            perfService.enregistrerPerformance(p2);
            System.out.println("✅ Deux performances enregistrées.");
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'enregistrement : " + e.getMessage());
        }

        // ---------------------------------------------------------------
        // 2. TEST LECTURE HISTORIQUE (Read)
        // ---------------------------------------------------------------
        System.out.println("\n--- 2. TEST LECTURE HISTORIQUE ---");
        
        List<Performance> historique = perfService.recupererHistoriqueMembre(membre.getId());
        
        if (historique.size() >= 2) {
            System.out.println("✅ Historique récupéré (" + historique.size() + " entrées).");
            for (Performance p : historique) {
                System.out.println("   - Date: " + p.getDateMesure() + " | Poids: " + p.getPoids() + "kg | IMC: " + p.getImc());
            }
        } else {
            System.out.println("❌ Erreur : Historique incomplet.");
        }

        // ---------------------------------------------------------------
        // 3. TEST LOGIQUE MÉTIER (Calcul Progression)
        // ---------------------------------------------------------------
        System.out.println("\n--- 3. TEST LOGIQUE PROGRESSION ---");
        
        // On récupère la dernière perf et l'avant-dernière
        Performance derniere = perfService.recupererDernierePerformance(membre.getId());
        
        // Pour le test, on sait qu'on a p1 et p2.
        // Poids P1 = 85, Poids P2 = 82. Progression = 82 - 85 = -3.
        
        double progressionPoids = derniere.calculerProgression(p1);
        System.out.println("Progression Poids (Actuel - Ancien) : " + progressionPoids + " kg");
        
        if (progressionPoids == -3.0) {
            System.out.println("✅ Le calcul de progression est correct (Perte de 3kg).");
        } else {
            System.out.println("⚠️ Calcul inattendu (peut dépendre de l'ordre de récupération).");
        }

        // ---------------------------------------------------------------
        // 4. TEST MODIFICATION (Update)
        // ---------------------------------------------------------------
        System.out.println("\n--- 4. TEST MODIFICATION ---");
        
        // Imaginons qu'on s'est trompé, il fait 81kg en fait
        derniere.setPoids(81.0);
        derniere.calculerIMC(1.80); // Recalcul IMC obligatoire si poids change
        
        // Le service n'a pas de méthode "mettreAJour", on appelle le repo directement pour le test
        // Ou mieux, on ajoute la méthode dans le service (voir note plus bas)
        com.sport.repository.PerformanceRepository repoTemp = new com.sport.repository.PerformanceRepository();
        repoTemp.modifierPerformance(derniere);
        
        Performance checkUpdate = repoTemp.trouverPerformanceParId(derniere.getId());
        if (checkUpdate.getPoids() == 81.0) {
            System.out.println("✅ Mise à jour confirmée en BDD.");
        } else {
            System.out.println("❌ Erreur lors de la mise à jour.");
        }

        // ---------------------------------------------------------------
        // 5. TEST SUPPRESSION (Delete)
        // ---------------------------------------------------------------
        System.out.println("\n--- 5. TEST SUPPRESSION ---");
        
        perfService.supprimerPerformance(p1.getId()); // On supprime la plus vieille
        
        List<Performance> histoApresSuppression = perfService.recupererHistoriqueMembre(membre.getId());
        if (histoApresSuppression.size() == 1) {
            System.out.println("✅ Suppression réussie, il reste 1 performance.");
        } else {
            System.out.println("❌ Erreur : La suppression a échoué.");
        }

        // Nettoyage final
        membreService.supprimerMembre(membre.getId());
        System.out.println("   (Membre de test nettoyé)");

        System.out.println("\n==========================================");
        System.out.println("       FIN DU TEST PERFORMANCE");
        System.out.println("==========================================");
    }
}