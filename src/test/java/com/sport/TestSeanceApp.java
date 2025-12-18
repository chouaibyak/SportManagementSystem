package com.sport;

import com.sport.model.*;
import com.sport.service.CoachService;

import java.time.LocalDateTime;
import java.util.List;

public class TestSeanceApp {

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("      TEST MODULE SÉANCE");
        System.out.println("=========================================");

        CoachService service = new CoachService();

        // 1️Création d'un coach pour les séances
        Coach coach = new Coach();
        coach.setNom("Durand");
        coach.setPrenom("Marc");
        coach.setId(1); // Assurez-vous que ce coach existe en BDD

        // 2Création d'une salle
        Salle salle = new Salle();
        salle.setId(1); // Assurez-vous que cette salle existe

        // 3️Création d'une séance collective
        SeanceCollective seanceC = new SeanceCollective();
        seanceC.setNom("Yoga Collectif");
        seanceC.setCapaciteMax(20);
        seanceC.setDateHeure(LocalDateTime.now().plusDays(1));
        seanceC.setDuree(60);
        seanceC.setTypeCours(TypeCours.YOGA);
        seanceC.setSalle(salle);
        seanceC.setTypeSeance(TypeSeance.COLLECTIVE);

        service.creerSeance(coach, seanceC);

        // 4️Création d'une séance individuelle
        SeanceIndividuelle seanceI = new SeanceIndividuelle();
        seanceI.setNom("Coaching Individuel");
        seanceI.setCapaciteMax(1);
        seanceI.setDateHeure(LocalDateTime.now().plusDays(2));
        seanceI.setDuree(45);
        seanceI.setTypeCours(TypeCours.MUSCULATION);
        seanceI.setSalle(salle);
        seanceI.setTypeSeance(TypeSeance.INDIVIDUELLE);

        service.creerSeance(coach, seanceI);

        // 5️Lister toutes les séances (exemple)
        System.out.println("\n--- LISTER LES SÉANCES ---");
        List<Seance> toutes = List.of(seanceC, seanceI); // ou appel repository si vous avez getAll
        for (Seance s : toutes) {
            System.out.println("ID: " + s.getId() +
                               " | Nom: " + s.getNom() +
                               " | Type: " + s.getTypeSeance() +
                               " | Date: " + s.getDateHeure());
        }

        // 6️ Modification d'une séance
        System.out.println("\n--- MODIFICATION SÉANCE ---");
        seanceC.setNom("Yoga Collectif Modifié");
        service.modifierSeance(coach, seanceC);

        // 7️Suppression d'une séance
        System.out.println("\n--- SUPPRESSION SÉANCE ---");
        service.supprimerSeance(coach, seanceI);

        System.out.println("\n=========================================");
        System.out.println("          FIN DU TEST SÉANCE");
        System.out.println("=========================================");
    }
}
