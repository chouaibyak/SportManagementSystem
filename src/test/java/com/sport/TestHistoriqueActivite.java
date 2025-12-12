package com.sport;

import com.sport.model.HistoriqueActivite;
import com.sport.model.Membre;
import com.sport.model.TypeObjectif;
import com.sport.model.TypePreference;
import com.sport.model.TypeSeance;
import com.sport.service.HistoriqueActiviteService;
import com.sport.service.MembreService;

import java.time.LocalDate;
import java.util.List;

public class TestHistoriqueActivite {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("   TEST MODULE HISTORIQUE ACTIVITE");
        System.out.println("==========================================");

        HistoriqueActiviteService historiqueService = new HistoriqueActiviteService();
        MembreService membreService = new MembreService();

        // ---------------------------------------------------------------
        // ÉTAPE 0 : PRÉPARATION (Il faut un membre existant !)
        // ---------------------------------------------------------------
        System.out.println("\n--- 0. PRÉPARATION ---");
        // On crée un membre temporaire pour le test pour éviter les erreurs de clés étrangères
        long timestamp = System.currentTimeMillis();
        Membre membreTest = new Membre(
            "Testeur", "Histo", "2000-01-01", 
            "histo." + timestamp + "@test.com", 
            "0102030405", "Rue du Sport", 
            TypePreference.CARDIO, TypeObjectif.ENDURANCE
        );
        membreService.creerMembre(membreTest);
        
        if (membreTest.getId() == 0) {
            System.out.println("Erreur critique : Impossible de créer le membre de test.");
            return;
        }
        System.out.println("Membre de test créé avec ID : " + membreTest.getId());

        // ---------------------------------------------------------------
        // 1. TEST ENREGISTREMENT (Create)
        // ---------------------------------------------------------------
        System.out.println("\n--- 1. TEST ENREGISTREMENT ACTIVITÉ ---");
        
        HistoriqueActivite activite = historiqueService.enregistrerActivite(
            membreTest,
            TypeSeance.INDIVIDUELLE,
            60, // minutes
            LocalDate.now(),
            "Séance intense de test"
        );

        // ASTUCE : Comme votre repo actuel ne met pas à jour l'ID dans l'objet 'activite',
        // on doit aller le récupérer en BDD pour avoir son vrai ID.
        List<HistoriqueActivite> listeActivites = historiqueService.obtenirHistoriqueMembre(membreTest.getId());
        
        if (!listeActivites.isEmpty()) {
            // On prend la dernière activité ajoutée
            HistoriqueActivite activiteEnBdd = listeActivites.get(0); 
            int activiteId = activiteEnBdd.getId();
            System.out.println("Activité enregistrée et retrouvée. ID : " + activiteId);
            System.out.println("   Détails : " + activiteEnBdd.getTypeSeance() + " | " + activiteEnBdd.getDuree() + "min");
            
            // Mise à jour de notre objet local pour la suite du test
            activite = activiteEnBdd; 
        } else {
            System.out.println("Erreur : L'activité n'a pas été sauvegardée en BDD.");
            return;
        }

        // ---------------------------------------------------------------
        // 2. TEST AJOUT DE NOTE (Logique Métier + Update)
        // ---------------------------------------------------------------
        System.out.println("\n--- 2. TEST AJOUT DE NOTE ---");
        
        System.out.println("Note avant : " + activite.getNotes());
        historiqueService.ajouterNote(activite.getId(), "Douleur légère au genou.");
        
        // Vérification
        String notesApres = historiqueService.consulterNotes(activite.getId());
        System.out.println("Note après : \n" + notesApres);
        
        if (notesApres.contains("Douleur légère")) {
            System.out.println("La note a bien été ajoutée et sauvegardée.");
        } else {
            System.out.println("Erreur : La note n'a pas été mise à jour.");
        }

        // ---------------------------------------------------------------
        // 3. TEST MODIFICATION ACTIVITÉ (Update)
        // ---------------------------------------------------------------
        System.out.println("\n--- 3. TEST MODIFICATION ACTIVITÉ ---");
        
        historiqueService.modifierActivite(
            activite.getId(), 
            TypeSeance.COLLECTIVE, // Changement de type
            90,                    // Changement de durée
            null,                  // Pas de changement de date
            null                   // Pas de changement de note
        );

        HistoriqueActivite activiteModifiee = historiqueService.obtenirHistoriqueMembre(membreTest.getId()).get(0);
        
        if (activiteModifiee.getDuree() == 90 && activiteModifiee.getTypeSeance() == TypeSeance.COLLECTIVE) {
            System.out.println("Modification réussie (Type et Durée mis à jour).");
        } else {
            System.out.println("Erreur lors de la modification.");
        }

        // ---------------------------------------------------------------
        // 4. TEST SUPPRESSION (Delete)
        // ---------------------------------------------------------------
        System.out.println("\n--- 4. TEST SUPPRESSION ---");
        
        // On supprime l'activité via le repo (ou service si méthode exposée, sinon on utilise suppression par membre)
        // Pour le test, on va supprimer via le repo directement ou ajouter la méthode au service.
        // Comme votre service n'a pas "supprimerActiviteParId", on teste "supprimerHistoriquesParMembre"
        // Ou on appelle le repo directement pour le test unitaire :
        com.sport.repository.HistoriqueActiviteRepository repoTemp = new com.sport.repository.HistoriqueActiviteRepository();
        repoTemp.supprimerHistoriqueActivite(activite.getId());

        HistoriqueActivite checkSuppression = repoTemp.trouverParId(activite.getId());
        
        if (checkSuppression == null) {
            System.out.println("Activité supprimée avec succès.");
        } else {
            System.out.println("Erreur : L'activité existe toujours.");
        }

        // Nettoyage du membre de test
        membreService.supprimerMembre(membreTest.getId());
        System.out.println("   (Membre de test nettoyé)");
        
        System.out.println("\n==========================================");
        System.out.println("       FIN DU TEST");
        System.out.println("==========================================");
    }
}