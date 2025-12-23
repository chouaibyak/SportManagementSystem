package com.sport;

import java.util.Date;
import java.util.List;

import com.sport.model.Salle;
import com.sport.model.TypeSalle;
import com.sport.repository.SalleRepository;
import com.sport.service.SalleService;

public class SalleServiceTest {

    public static void main(String[] args) {

        SalleRepository salleRepository = new SalleRepository();
        SalleService salleService = new SalleService(salleRepository);

        // 1️⃣ CREATE
        Salle salle = new Salle("Salle Test Service", 40, TypeSalle.MUSCULATION);
        salleService.ajouterSalle(salle);
        System.out.println("✔ Salle ajoutée ID = " + salle.getId());

        // 2️⃣ READ ALL
        List<Salle> salles = salleService.getToutesLesSalles();
        System.out.println("✔ Nombre de salles = " + salles.size());

        for (Salle s : salles) {
            System.out.println(
                s.getId() + " | " + s.getNom() + " | " + s.getCapacite() + " | " + s.getType()
            );
        }

        // 3️⃣ READ BY ID
        Salle found = salleService.getSalleById(salle.getId());
        System.out.println("✔ Salle trouvée = " + found.getNom());

        // 4️⃣ UPDATE
        found.setCapacite(60);
        boolean updated = salleService.modifierSalle(found);
        System.out.println("✔ Salle modifiée = " + updated);

        // 5️⃣ DISPONIBILITÉ (logic-only test)
        boolean disponible = salleService.salleDisponible(found.getId(), new Date());
        System.out.println("✔ Salle disponible = " + disponible);

        // 6️⃣ DELETE
        boolean deleted = salleService.supprimerSalle(found.getId());
        System.out.println("✔ Salle supprimée = " + deleted);
    }
}
