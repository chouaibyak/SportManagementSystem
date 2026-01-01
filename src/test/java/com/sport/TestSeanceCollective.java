package com.sport;

import com.sport.model.*;
import com.sport.repository.*;

import java.time.LocalDateTime;

public class TestSeanceCollective {

    public static void main(String[] args) {

        // Repositories
        MembreRepository membreRepo = new MembreRepository();
        SeanceCollectiveRepository scRepo = new SeanceCollectiveRepository();
        NotificationRepository notifRepo = new NotificationRepository();

        // 1️⃣ Trouver membre et séance
        Membre membre = membreRepo.trouverParId(23);  // Exemple membre
        SeanceCollective sc = scRepo.getById(28); // Exemple séance collective

        if (membre == null || sc == null) {
            System.out.println("❌ Membre ou séance introuvable !");
            return;
        }

        // 2️⃣ Vérifier places disponibles
        System.out.println("Places disponibles avant réservation : " + sc.getPlacesDisponibles());

        // 3️⃣ Réserver place et notifier coach
        boolean ok = scRepo.reserverPlace(sc.getId(), membre);

        if (ok) {
            System.out.println("✅ Réservation réussie ! Notification envoyée au coach.");
        } else {
            System.out.println("❌ Échec réservation (plus de places disponibles ?).");
        }

        // 4️⃣ Vérifier notification pour le coach
        int coachId = sc.getEntraineur().getId();
        System.out.println("\n--- Notifications du coach ---");
        notifRepo.getNotificationsByDestinataire(coachId)
                .forEach(n -> {
                    System.out.println("Message: " + n.getMessage());
                    System.out.println("Type: " + n.getType());
                    System.out.println("Priorité: " + n.getPriorite());
                    System.out.println("Date: " + n.getDateEnvoi());
                    System.out.println("Lue: " + n.isLue());
                    System.out.println("---------------");
                });

        // 5️⃣ Vérifier places après réservation
        SeanceCollective scAfter = scRepo.getById(sc.getId());
        System.out.println("Places disponibles après réservation : " + scAfter.getPlacesDisponibles());
    }
}
