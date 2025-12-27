package com.sport;

import com.sport.model.*;
import com.sport.repository.SeanceCollectiveRepository;
import com.sport.utils.DBConnection;

import java.time.LocalDateTime;

public class SeedSeances {

    public static void main(String[] args) {
        System.out.println("Tentative de connexion à la base de données...");
        try {
            DBConnection.getConnection();
            System.out.println("Connexion à la base de données établie avec succès !");
        } catch (Exception e) {
            System.out.println("Erreur connexion : " + e.getMessage());
            return;
        }

        // Créer l'objet Coach avec ID 22
        Coach coach = new Coach();
        coach.setId(22);

        // Créer la Salle (supposons salle ID 1)
        Salle salle = new Salle();
        salle.setId(1);

        // Repository
        SeanceCollectiveRepository repo = new SeanceCollectiveRepository();

        // Séance 1
        SeanceCollective sc1 = new SeanceCollective();
        sc1.setNom("Yoga Collectif");
        sc1.setCapaciteMax(10);
        sc1.setSalle(salle);
        sc1.setDateHeure(LocalDateTime.now().plusHours(2));
        sc1.setEntraineur(coach);
        sc1.setTypeCours(TypeCours.YOGA);           // <-- Important
        sc1.setTypeSeance(TypeSeance.COLLECTIVE);  // <-- Important
        sc1.setDuree(60);
        sc1.setPlacesDisponibles(10);

        // Séance 2
        SeanceCollective sc2 = new SeanceCollective();
        sc2.setNom("Musculation Groupe");
        sc2.setCapaciteMax(8);
        sc2.setSalle(salle);
        sc2.setDateHeure(LocalDateTime.now().plusHours(4));
        sc2.setEntraineur(coach);
        sc2.setTypeCours(TypeCours.MUSCULATION);
        sc2.setTypeSeance(TypeSeance.COLLECTIVE);
        sc2.setDuree(90);
        sc2.setPlacesDisponibles(8);

        // Séance 3
        SeanceCollective sc3 = new SeanceCollective();
        sc3.setNom("Pilates Collectif");
        sc3.setCapaciteMax(12);
        sc3.setSalle(salle);
        sc3.setDateHeure(LocalDateTime.now().plusDays(1));
        sc3.setEntraineur(coach);
        sc3.setTypeCours(TypeCours.YOGA);
        sc3.setTypeSeance(TypeSeance.COLLECTIVE);
        sc3.setDuree(60);
        sc3.setPlacesDisponibles(12);

        // Ajouter les séances à la BDD
        repo.ajouter(sc1);
        repo.ajouter(sc2);
        repo.ajouter(sc3);

        System.out.println("3 séances créées pour le coach ID 22 !");
    }
}
