package com.sport;
import com.sport.model.*;
import com.sport.service.SeanceCollectiveService;
import com.sport.service.SeanceIndividuelleService;
import com.sport.repository.SeanceCollectiveRepository;
import com.sport.repository.SeanceIndividuelleRepository;

import java.time.LocalDateTime;
import java.util.List;

public class TestSeances {

    public static void main(String[] args) {

        // -----------------------------
        // 1️⃣ Préparer les services
        // -----------------------------
        SeanceCollectiveService scService = new SeanceCollectiveService(new SeanceCollectiveRepository());
        SeanceIndividuelleService siService = new SeanceIndividuelleService(new SeanceIndividuelleRepository());

        Coach coach = new Coach();
        coach.setId(1);

        Salle salle = new Salle();
        salle.setId(1);

        Membre membre1 = new Membre();
        membre1.setId(1);
        membre1.setNom("Alice");

        Membre membre2 = new Membre();
        membre2.setId(2);
        membre2.setNom("Bob");

        // -----------------------------
        // 2️⃣ Créer une séance collective
        // -----------------------------
        SeanceCollective sc = new SeanceCollective();
        sc.setNom("Yoga Collectif Test");
        sc.setCapaciteMax(5);
        sc.setDateHeure(LocalDateTime.now().plusDays(1));
        sc.setDuree(60);
        sc.setSalle(salle);
        sc.setEntraineur(coach);
        sc.setTypeSeance(TypeSeance.COLLECTIVE);
        sc.setPlacesDisponibles(5);

        int scId = scService.getAll().size() + 1; // optionnel si repository gère ID automatiquement
        scService.ajouterSeance(sc);
        System.out.println("Séance Collective créée ID=" + sc.getId());

        // -----------------------------
        // 3️⃣ Réserver des places
        // -----------------------------
        scService.reserverPlace(sc.getId(), membre1);
        scService.reserverPlace(sc.getId(), membre2);

        System.out.println("Places après réservation: " + sc.getPlacesDisponibles());
        System.out.println("Participants: ");
        sc.getListeMembers().forEach(m -> System.out.println(" - " + m.getNom()));

        // -----------------------------
        // 4️⃣ Créer une séance individuelle
        // -----------------------------
        SeanceIndividuelle si1 = new SeanceIndividuelle();
        si1.setNom("Cours Privé Alice");
        si1.setCapaciteMax(1);
        si1.setDateHeure(LocalDateTime.now().plusDays(2));
        si1.setDuree(60);
        si1.setSalle(salle);
        si1.setEntraineur(coach);
        si1.setTypeSeance(TypeSeance.INDIVIDUELLE);
        si1.setMembre(membre1);
        si1.setTarif(100);

        siService.ajouterSeance(si1);
        System.out.println("Séance Individuelle créée ID=" + si1.getId());

        // Ajouter note de coach
        siService.ajouterNoteCoach(si1.getId(), "Travail sur posture");

        // -----------------------------
        // 5️⃣ Afficher toutes les séances
        // -----------------------------
        System.out.println("\nToutes les séances collectives:");
        List<SeanceCollective> collectives = scService.getAll();
        for (SeanceCollective s : collectives) {
            System.out.println(s.getId() + ": " + s.getNom() + " | Places: " + s.getPlacesDisponibles());
        }

        System.out.println("\nToutes les séances individuelles:");
        List<SeanceIndividuelle> individuelles = siService.getAll();
        for (SeanceIndividuelle s : individuelles) {
            System.out.println(s.getId() + ": " + s.getNom() + " | Membre: " + s.getMembre().getNom() + " | Note: " + s.getNotesCoach());
        }

    }
}
