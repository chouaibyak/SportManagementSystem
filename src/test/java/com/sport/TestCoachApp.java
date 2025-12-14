package com.sport;

import com.sport.model.Coach;
import com.sport.service.CoachService;

import java.util.List;

public class TestCoachApp {

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   DEMARRAGE DU TEST MODULE COACH");
        System.out.println("=========================================");

        CoachService service = new CoachService();

        // ---------------------------------------------------------------
        // 1. TEST CRÉATION (Create)
        // ---------------------------------------------------------------
        System.out.println("\n--- 1. TEST AJOUT D'UN COACH ---");

        long timestamp = System.currentTimeMillis();
        String emailUnique = "coach.test" + timestamp + "@test.com";

        Coach nouveauCoach = new Coach();
        nouveauCoach.setNom("Dupont");
        nouveauCoach.setPrenom("Jean");
        nouveauCoach.setEmail(emailUnique);

        service.ajouterCoach(nouveauCoach);

        int coachId = nouveauCoach.getId();

        if (coachId > 0) {
            System.out.println("Coach ajouté avec succès ! ID généré : " + coachId);
            System.out.println("   Email utilisé : " + emailUnique);
        } else {
            System.out.println("Erreur : L'ID n'a pas été généré.");
            return;
        }

        // ---------------------------------------------------------------
        // 2. TEST LECTURE DE TOUS LES COACHS
        // ---------------------------------------------------------------
        System.out.println("\n--- 2. TEST LISTER TOUS LES COACHS ---");
        List<Coach> tousLesCoachs = service.getAllCoaches();

        boolean trouve = false;
        for (Coach c : tousLesCoachs) {
            if (c.getId() == coachId) {
                System.out.println(" -> TROUVÉ : ID: " + c.getId() + " | " + c.getNom());
                trouve = true;
            }
        }

        if (trouve) System.out.println("Le nouveau coach est bien présent dans la liste.");
        else System.out.println("Le nouveau coach est introuvable.");

        // ---------------------------------------------------------------
        // 3. TEST LECTURE PAR ID
        // ---------------------------------------------------------------
        System.out.println("\n--- 3. TEST RECUPERATION PAR ID ---");
        Coach coachRecupere = service.getCoachById(coachId);

        if (coachRecupere != null) {
            System.out.println("Coach trouvé : " + coachRecupere.getNom());
        } else {
            System.out.println("Erreur : Impossible de récupérer le coach.");
        }

        // ---------------------------------------------------------------
        // 4. TEST MISE A JOUR
        // ---------------------------------------------------------------
        System.out.println("\n--- 4. TEST MODIFICATION COACH ---");

        coachRecupere.setNom("Dupont-Modifié");
        service.modifierCoach(coachRecupere);

        Coach coachApresUpdate = service.getCoachById(coachId);

        if (coachApresUpdate.getNom().equals("Dupont-Modifié")) {
            System.out.println("Modification réussie en BDD !");
        } else {
            System.out.println("Erreur : Les modifications n'ont pas été sauvegardées.");
        }

        // ---------------------------------------------------------------
        // 5. TEST LOGIQUE MÉTIER (In-Memory)
        // ---------------------------------------------------------------
        System.out.println("\n--- 5. TEST LOGIQUE MÉTIER ---");

        // Exemple : Vérifier qu'on peut créer une séance pour le coach
        // Seance seanceTest = new Seance();
        // service.creerSeance(coachRecupere, seanceTest);
        // System.out.println("Séance ajoutée pour le coach.");

        // ---------------------------------------------------------------
        // 6. TEST SUPPRESSION
        // ---------------------------------------------------------------
        System.out.println("\n--- 6. TEST SUPPRESSION COACH ---");

        service.supprimerCoach(coachId);

        Coach coachSupprime = service.getCoachById(coachId);

        if (coachSupprime == null) {
            System.out.println("Suppression confirmée.");
        } else {
            System.out.println("Erreur : Le coach existe toujours.");
        }

        System.out.println("\n=========================================");
        System.out.println("          FIN DU TEST");
        System.out.println("=========================================");
    }
}
