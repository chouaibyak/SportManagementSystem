package com.sport;

import com.sport.model.Membre;
import com.sport.model.TypeObjectif;
import com.sport.model.TypePreference;
import com.sport.model.Seance; 
import com.sport.service.MembreService;

import java.util.List;

public class TestMembreApp {

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   DEMARRAGE DU TEST MODULE MEMBRE");
        System.out.println("=========================================");

        MembreService service = new MembreService();

        // ---------------------------------------------------------------
        // 1. TEST CRÉATION (Create)
        // ---------------------------------------------------------------
        System.out.println("\n--- 1. TEST AJOUT D'UN MEMBRE ---");
        
        // ASTUCE : On génère un email unique basé sur l'heure actuelle
        // Cela évite l'erreur "Duplicate entry" quand on relance le test
        long timestamp = System.currentTimeMillis();
        String emailUnique = "jean.dupont" + timestamp + "@test.com";

        Membre nouveauMembre = new Membre(
            "Dupont",
            "Jean",
            "1990-05-15",
            emailUnique,
            "0601020304",
            "10 Rue de la Paix, Paris",
            "123",
            TypeObjectif.PERTE_POIDS, 
            TypePreference.CARDIO     
        );

        service.creerMembre(nouveauMembre);

        int membreId = nouveauMembre.getId();
        
        if (membreId > 0) {
            System.out.println("Membre ajouté avec succès ! ID généré : " + membreId);
            System.out.println("   Email utilisé : " + emailUnique);
        } else {
            System.out.println("Erreur : L'ID n'a pas été généré.");
            return; 
        }

        // ---------------------------------------------------------------
        // 2. TEST LECTURE DE TOUS LES MEMBRES
        // ---------------------------------------------------------------
        System.out.println("\n--- 2. TEST LISTER TOUS LES MEMBRES ---");
        List<Membre> tousLesMembres = service.recupererTousLesMembres();
        
        boolean trouve = false;
        for (Membre m : tousLesMembres) {
            // Petite optimisation d'affichage pour ne pas inonder la console
            if (m.getId() == membreId) {
                System.out.println(" -> TROUVÉ : ID: " + m.getId() + " | " + m.getNom());
                trouve = true;
            }
        }

        if (trouve) System.out.println("Le nouveau membre est bien présent dans la liste.");
        else System.out.println("Le nouveau membre est introuvable.");

        // ---------------------------------------------------------------
        // 3. TEST LECTURE PAR ID
        // ---------------------------------------------------------------
        System.out.println("\n--- 3. TEST RECUPERATION PAR ID ---");
        Membre membreRecupere = service.recupererMembreParId(membreId);

        if (membreRecupere != null) {
            System.out.println("Membre trouvé : " + membreRecupere.getNom());
        } else {
            System.out.println("Erreur : Impossible de récupérer le membre.");
        }

        // ---------------------------------------------------------------
        // 4. TEST MISE A JOUR
        // ---------------------------------------------------------------
        System.out.println("\n--- 4. TEST MODIFICATION MEMBRE ---");
        
        membreRecupere.setNom("Dupont-Modifié");
        // On change l'objectif pour tester
        membreRecupere.setObjectifSportif(TypeObjectif.RENFORCEMENT); 

        service.mettreAJourMembre(membreRecupere);

        Membre membreApresUpdate = service.recupererMembreParId(membreId);
        
        // ATTENTION : Dans votre code précédent, vous compariez avec ENDURANCE alors que vous aviez set RENFORCEMENT
        // J'ai corrigé la vérification ci-dessous :
        if (membreApresUpdate.getNom().equals("Dupont-Modifié") && 
            membreApresUpdate.getObjectifSportif() == TypeObjectif.RENFORCEMENT) {
            System.out.println("Modification réussie en BDD !");
        } else {
            System.out.println("Erreur : Les modifications n'ont pas été sauvegardées.");
            System.out.println("   Attendu : RENFORCEMENT, Reçu : " + membreApresUpdate.getObjectifSportif());
        }

        // ---------------------------------------------------------------
        // 5. TEST LOGIQUE MÉTIER
        // ---------------------------------------------------------------
        System.out.println("\n--- 5. TEST LOGIQUE MÉTIER (In-Memory) ---");
        
        Seance seanceTest = new Seance(); 
        // seanceTest.setId(1); // Décommenter si Seance a un ID
        
        int tailleAvant = membreRecupere.consulterSeances().size();
        membreRecupere.reserverSeance(seanceTest);
        int tailleApres = membreRecupere.consulterSeances().size();

        if (tailleApres == tailleAvant + 1) {
            System.out.println("Réservation ajoutée à la liste locale.");
        } else {
            System.out.println("Erreur réservation locale.");
        }

        // ---------------------------------------------------------------
        // 6. TEST SUPPRESSION
        // ---------------------------------------------------------------
        System.out.println("\n--- 6. TEST SUPPRESSION MEMBRE ---");
        
        service.supprimerMembre(membreId);

        Membre membreSupprime = service.recupererMembreParId(membreId);
        
        if (membreSupprime == null) {
            System.out.println("Suppression confirmée.");
        } else {
            System.out.println("Erreur : Le membre existe toujours.");
        }

        System.out.println("\n=========================================");
        System.out.println("          FIN DU TEST");
        System.out.println("=========================================");
    }
}