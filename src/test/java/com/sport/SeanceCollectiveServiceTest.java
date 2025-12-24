package com.sport;

import java.time.LocalDateTime;
import java.util.List;

import com.sport.model.Coach;
import com.sport.model.Membre;
import com.sport.model.Salle;
import com.sport.model.SeanceCollective;
import com.sport.model.TypeCours;
import com.sport.model.TypeObjectif;
import com.sport.model.TypePreference;
import com.sport.model.TypeSalle;
import com.sport.repository.SeanceCollectiveRepository;
import com.sport.service.SeanceCollectiveService;

public class SeanceCollectiveServiceTest {

    public static void main(String[] args) {

        SeanceCollectiveRepository repository = new SeanceCollectiveRepository();
        SeanceCollectiveService service = new SeanceCollectiveService(repository);

        // ⚠️ Existing DB objects (ADJUST IDS!)
        Salle salle = new Salle("Salle A", 50, TypeSalle.MUSCULATION);
        salle.setId(1); // existing salle_id

        Coach coach = new Coach("Doe", "John", "1980-01-01", "john.doe@example.com", 
                 "0612345678", "123 Rue de Paris");
                                    
        Membre membre = new Membre("Jhon","Wick","1975-05-05","jhon@email.com","0699877","45 Avenue des Champs",TypePreference.CARDIO,TypeObjectif.PERTE_POIDS);
        

        // 1️⃣ CREATE
        SeanceCollective seance = new SeanceCollective(
                0,
                "CrossFit",
                30,
                salle,
                LocalDateTime.now().plusDays(1),
                coach,
                TypeCours.YOGA,
                60,
                10
        );

        service.ajouterSeance(seance);
        System.out.println("✔ INSERTED seance ID = " + seance.getId());

        // 2️⃣ READ BY ID
        SeanceCollective fetched = service.getById(seance.getId());
        System.out.println("✔ FETCHED = " + fetched.getNom());

        // 3️⃣ UPDATE
        fetched.setPlacesDisponibles(8);
        boolean updated = service.update(fetched);
        System.out.println("✔ UPDATED = " + updated);

        // 4️⃣ LIST ALL
        List<SeanceCollective> all = service.getAll();
        System.out.println("✔ TOTAL SEANCES = " + all.size());

        // 5️⃣ RESERVE PLACE
        boolean reserved = service.reserverPlace(seance.getId(), membre);
        System.out.println("✔ PLACE RESERVED = " + reserved);

        // 6️⃣ CANCEL RESERVATION
        boolean cancelled = service.annulerReservation(seance.getId(), membre);
        System.out.println("✔ RESERVATION CANCELLED = " + cancelled);

        // 7️⃣ DELETE
        boolean deleted = service.delete(seance.getId());
        System.out.println("✔ DELETED = " + deleted);
    }
}
