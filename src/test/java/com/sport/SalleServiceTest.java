package com.sport;

import java.util.List;
import java.util.Date;

import com.sport.model.Salle;
import com.sport.model.TypeSalle;
import com.sport.repository.SalleRepository;
import com.sport.service.SalleService;

public class SalleServiceTest {

    public static void main(String[] args) {

        SalleRepository repository = new SalleRepository();
        SalleService service = new SalleService(repository);

        // ⚠️ Ajuste l’ID si tu utilises une salle déjà existante
        Salle salle = new Salle("Salle Test Main", 40, TypeSalle.MUSCULATION);

        // 1️⃣ CREATE
        service.ajouterSalle(salle);
        System.out.println("✔ Salle ajoutée ID = " + salle.getId());

        // 2️⃣ READ BY ID
        Salle fetched = service.getSalleById(salle.getId());
        if (fetched != null) {
            System.out.println("✔ Salle récupérée : " + fetched.getNom() + " [" + fetched.getType() + "] - Capacité: " + fetched.getCapacite());
        }

        // 3️⃣ UPDATE
        fetched.setCapacite(60);
        boolean updated = service.modifierSalle(fetched);
        System.out.println("✔ Salle modifiée = " + updated);

        // 4️⃣ LIST ALL
        List<Salle> all = service.getToutesLesSalles();
        System.out.println("✔ Nombre total de salles = " + all.size());
        for (Salle s : all) {
            System.out.println("- " + s.getId() + " | " + s.getNom() + " | " + s.getCapacite() + " | " + s.getType());
        }

        // 5️⃣ CHECK AVAILABILITY (logic-only)
        boolean disponible = service.salleDisponible(fetched.getId(), new Date());
        System.out.println("✔ Salle disponible = " + disponible);

        // 6️⃣ DELETE
        boolean deleted = service.supprimerSalle(fetched.getId());
        System.out.println("✔ Salle supprimée = " + deleted);
    }
}
