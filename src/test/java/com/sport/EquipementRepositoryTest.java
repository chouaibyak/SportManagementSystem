package com.sport;

import java.util.Date;
import java.util.List;

import com.sport.model.Equipement;
import com.sport.model.EtatEquipement;
import com.sport.model.TypeEquipement;
import com.sport.repository.EquipementRepository;

public class EquipementRepositoryTest {

    public static void main(String[] args) {

        EquipementRepository repo = new EquipementRepository();

        // 1️⃣ CREATE
        Equipement eq = new Equipement(
            "Tapis de yoga",
            TypeEquipement.HALTERES,
            EtatEquipement.EN_MAINTENANCE,
            new Date()
        );

        repo.ajouterEquipement(eq);
        System.out.println("✔ Equipement inserted");

        // 2️⃣ READ ALL
        List<Equipement> equipements = repo.listerEquipements();
        System.out.println("✔ Total equipements = " + equipements.size());

        for (Equipement e : equipements) {
            System.out.println(
                e.getId() + " | " + e.getNom() + " | " + e.getType() + " | " + e.getEtat()
            );
        }

        // 3️⃣ READ BY ID
        Equipement found = repo.getEquipementById(equipements.get(0).getId());
        System.out.println("✔ Found equipement = " + found.getNom());

        // 4️⃣ UPDATE
        found.setEtat(EtatEquipement.DISPONIBLE);
        boolean updated = repo.modifierEquipement(found);
        System.out.println("✔ Updated = " + updated);

        // 5️⃣ DELETE
        boolean deleted = repo.supprimerEquipement(found.getId());
        System.out.println("✔ Deleted = " + deleted);
    }
}
