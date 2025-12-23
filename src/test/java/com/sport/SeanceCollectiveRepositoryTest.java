package com.sport;

import java.time.LocalDateTime;
import java.util.List;

import com.sport.model.Coach;
import com.sport.model.Salle;
import com.sport.model.SeanceCollective;
import com.sport.model.TypeCours;
import com.sport.model.TypeSalle;
import com.sport.repository.SeanceCollectiveRepository;

public class SeanceCollectiveRepositoryTest {

    public static void main(String[] args) {

        SeanceCollectiveRepository repo = new SeanceCollectiveRepository();

        // ------------------------------
        // PREPARE TEST DATA
        // ------------------------------
        Salle salle = new Salle("Salle A", 30, TypeSalle.MUSCULATION);
        salle.setId(1); // ⚠ must exist in DB

        Coach coach = new Coach();

        SeanceCollective seance = new SeanceCollective(
                0,
                "CrossFit",
                25,
                salle,
                LocalDateTime.now().plusDays(1),
                coach,
                TypeCours.MUSCULATION,
                60,
                25
        );

        // ------------------------------
        // TEST CREATE
        // ------------------------------
        repo.ajouter(seance);
        System.out.println("✔ INSERTED seance ID = " + seance.getId());

        // ------------------------------
        // TEST GET BY ID
        // ------------------------------
        SeanceCollective fromDb = repo.getById(seance.getId());
        System.out.println("✔ FETCHED: " + fromDb.getNom());

        // ------------------------------
        // TEST UPDATE
        // ------------------------------
        fromDb.setPlacesDisponibles(20);
        boolean updated = repo.update(fromDb);
        System.out.println("✔ UPDATED = " + updated);

        // ------------------------------
        // TEST GET ALL
        // ------------------------------
        List<SeanceCollective> list = repo.getAll();
        System.out.println("✔ TOTAL SEANCES COLLECTIVES = " + list.size());

        // ------------------------------
        // TEST DELETE
        // ------------------------------
        boolean deleted = repo.delete(seance.getId());
        System.out.println("✔ DELETED = " + deleted);
    }
}
