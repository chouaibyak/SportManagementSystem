package com.sport;

import java.time.LocalDateTime;

import com.sport.model.Coach;
import com.sport.model.Membre;
import com.sport.model.Salle;
import com.sport.model.SeanceIndividuelle;
import com.sport.model.TypeCours;
import com.sport.model.TypeSalle;
import com.sport.repository.SeanceIndividuelleRepository;
import com.sport.service.SeanceIndividuelleService;

public class SeanceIndividuelleServiceTest {

    public static void main(String[] args) {

        SeanceIndividuelleRepository repo = new SeanceIndividuelleRepository();
        SeanceIndividuelleService service = new SeanceIndividuelleService(repo);

        // --- Fake dependencies (IDs must exist in DB) ---
        Salle salle = new Salle("Salle Coaching", 1, TypeSalle.CARDIO);
        salle.setId(1);

        Coach coach = new Coach("Dida", "dida", "1980-01-01", "dida@example.com", 
                 "0612345678", "123 Rue de Paris", "Dida"   );
        Membre membre = new Membre(1, "Sara", "Client", "02/22/2002", "sara.client@example.com ", 
        "08004547", null, null, null);

        // --- Create seance ---
        SeanceIndividuelle seance = new SeanceIndividuelle(
                0,
                "Coaching privé",
                1,
                salle,
                LocalDateTime.now(),
                coach,
                TypeCours.YOGA,
                60,
                membre,
                200.0,
                "Bonne séance"
        );

        // --- INSERT ---
        service.ajouterSeance(seance);
        System.out.println("✔ INSERTED ID = " + seance.getId());

        // --- FETCH ---
        SeanceIndividuelle fetched = service.getById(seance.getId());
        if (fetched == null) {
            System.out.println("❌ Fetch failed");
            return;
        }
        System.out.println("✔ FETCHED = " + fetched.getNom());

        // --- UPDATE note ---
        service.ajouterNoteCoach(fetched.getId(), "Excellent progrès");
        System.out.println("✔ NOTE UPDATED");

        // --- UPDATE membre ---
        service.assignerMembre(fetched.getId(), membre);
        System.out.println("✔ MEMBRE ASSIGNED");

        // --- TARIF ---
        System.out.println("✔ TARIF = " + service.calculerTarifFinal(fetched.getId()));

        // --- DELETE ---
       // boolean deleted = service.delete(fetched.getId());
        //System.out.println("✔ DELETED = " + deleted);
    }
}
