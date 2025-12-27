package com.sport;

import com.sport.model.*;
import com.sport.repository.SeanceRepository;
import com.sport.service.MembreService;
import com.sport.service.ReservationService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class TestReservation {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("      TEST COMPLET RESERVATION");
        System.out.println("==========================================");

        MembreService membreService = new MembreService();
        ReservationService reservationService = new ReservationService();
        SeanceRepository seanceRepo = new SeanceRepository();

        // -----------------------------------------------------------
        // 1. CRÉATION MEMBRE
        // -----------------------------------------------------------
        System.out.println("\n--- 1. CRÉATION MEMBRE ---");
        Membre membre = new Membre(
        );
        membreService.creerMembre(membre);
        
        if (membre.getId() == 0) return;
        System.out.println("✅ Membre créé ID = " + membre.getId());

        // -----------------------------------------------------------
        // 2. CRÉATION SÉANCE
        // -----------------------------------------------------------
        System.out.println("\n--- 2. CRÉATION SÉANCE ---");
        Coach coach = new Coach(); coach.setId(1); 
        Salle salle = new Salle(); salle.setId(1); 

        Seance seance = new Seance();
        seance.setNom("Cours Test Complet");
        seance.setCapaciteMax(15);
        seance.setDateHeure(LocalDateTime.now().plusDays(2));
        seance.setDuree(60);
        seance.setSalle(salle);
        seance.setEntraineur(coach);
        seance.setTypeCours(TypeCours.YOGA);          
        seance.setTypeSeance(TypeSeance.COLLECTIVE); 

        seanceRepo.creerSeance(coach, seance);
        
        if (seance.getId() == 0) {
            System.out.println("❌ Erreur : ID Séance est 0 (Vérifiez le Repo ou Coach/Salle).");
            return;
        }
        System.out.println("✅ Séance créée ID = " + seance.getId());

        // -----------------------------------------------------------
        // 3. CRÉATION RÉSERVATION
        // -----------------------------------------------------------
        System.out.println("\n--- 3. CRÉATION RÉSERVATION ---");
        Reservation resa = new Reservation();
        resa.setMembre(membre);
        resa.setSeance(seance);
        resa.setDateReservation(new Date());
        
        // CORRECTION CONSEILLÉE : Utilisez EN_ATTENTE (avec E) dans votre Enum et Service
        // Si vous avez gardé la faute, laissez EN_ATTENT
        resa.setStatut(StatutReservation.EN_ATTENTE); 

        reservationService.creerReservation(resa);

        if (resa.getId() > 0) {
            System.out.println("✅ Réservation créée ID : " + resa.getId());
        } else {
            System.out.println("❌ Échec création.");
        }

        // -----------------------------------------------------------
        // 4. TEST MARQUER PRÉSENCE
        // -----------------------------------------------------------
        System.out.println("\n--- 4. TEST MARQUER PRÉSENCE ---");
        reservationService.marquerPresence(resa.getId());
        
        Reservation check = reservationService.trouverReservation(resa.getId());
        if (check != null && check.getStatut() == StatutReservation.PRESENT) {
            System.out.println("✅ Statut modifié à : PRESENT");
        } else {
            System.out.println("❌ Erreur Statut : " + check.getStatut());
        }

        // -----------------------------------------------------------
        // 5. TEST ANNULATION (C'était manquant)
        // -----------------------------------------------------------
        System.out.println("\n--- 5. TEST ANNULATION ---");
        reservationService.annulerReservation(resa.getId());
        
        Reservation checkAnnul = reservationService.trouverReservation(resa.getId());
        if (checkAnnul != null && checkAnnul.getStatut() == StatutReservation.ANNULEE) {
            System.out.println("✅ Statut modifié à : ANNULEE");
        } else {
            System.out.println("❌ Erreur Annulation : " + checkAnnul.getStatut());
        }

        // -----------------------------------------------------------
        // 6. TEST LISTER TOUT (C'était manquant)
        // -----------------------------------------------------------
        System.out.println("\n--- 6. TEST LISTING ---");
        List<Reservation> liste = reservationService.listerToutesLesReservations();
        if (liste != null && !liste.isEmpty()) {
            System.out.println("✅ La liste contient " + liste.size() + " réservations.");
            // Vérifier si la nôtre est dedans
            boolean trouve = false;
            for(Reservation r : liste) {
                if(r.getId() == resa.getId()) trouve = true;
            }
            if(trouve) System.out.println("✅ Notre réservation est bien dans la liste.");
        } else {
            System.out.println("❌ La liste est vide ou nulle.");
        }

        // -----------------------------------------------------------
        // 7. FIN
        // -----------------------------------------------------------
        System.out.println("\n--- 7. FIN DU TEST ---");
        System.out.println("Test terminé.");
    }
}