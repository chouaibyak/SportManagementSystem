package com.sport;

import java.util.List;

import com.sport.model.Salle;
import com.sport.model.TypeSalle;
import com.sport.repository.SalleRepository;

public class SalleRepositoryTest {

    public static void main(String[] args) {

        SalleRepository repository = new SalleRepository();

        // ⚠️ Ajuster si tu utilises déjà une salle existante
        Salle salle = new Salle("Salle Test Repo", 35, TypeSalle.CARDIO);

        // 1️⃣ CREATE
        repository.ajouterSalle(salle);
        System.out.println("✔ Salle ajoutée ID = " + salle.getId());

        // 2️⃣ READ BY ID
        Salle fetched = repository.getSalleById(salle.getId());
        if (fetched != null) {
            System.out.println("✔ Salle récupérée : " + fetched.getNom() + " [" + fetched.getType() + "] - Capacité: " + fetched.getCapacite());
        }

        // 3️⃣ UPDATE
        fetched.setCapacite(50);
        boolean updated = repository.modifierSalle(fetched);
        System.out.println("✔ Salle modifiée = " + updated);

        // 4️⃣ LIST ALL
        List<Salle> all = repository.listerSalles();
        System.out.println("✔ Nombre total de salles = " + all.size());
        for (Salle s : all) {
            System.out.println("- " + s.getId() + " | " + s.getNom() + " | " + s.getCapacite() + " | " + s.getType());
        }

        // 5️⃣ CHECK AVAILABILITY (logic-only)
        boolean disponible = repository.verifierDisponibiliteSalle(fetched.getId(), "2025-12-25 10:00:00");
        System.out.println("✔ Salle disponible le 25/12/2025 10:00 = " + disponible);

        // 6️⃣ DELETE
       // boolean deleted = repository.supprimerSalle(fetched.getId());
        //System.out.println("✔ Salle supprimée = " + deleted);
    }
}
