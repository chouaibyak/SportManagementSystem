package com.sport;

import java.util.Date;
import java.util.List;

import com.sport.model.Equipement;
import com.sport.model.EtatEquipement;
import com.sport.model.TypeEquipement;
import com.sport.repository.EquipementRepository;
import com.sport.service.EquipementService;

public class EquipementServiceTest {

    public static void main(String[] args) {

        EquipementRepository repo = new EquipementRepository();
        EquipementService service = new EquipementService(repo);

        // 1️⃣ Ajouter
        Equipement eq = new Equipement(
                "Tapis de course",
                TypeEquipement.BANC,
                EtatEquipement.DISPONIBLE,
                new Date()
        );

        service.ajouterEquipement(eq);
        System.out.println("✅ Equipement ajouté");

        // 2️⃣ Lister
        List<Equipement> equipements = service.listerEquipements();
        System.out.println("📋 Nombre d’équipements = " + equipements.size());

        Equipement first = equipements.get(equipements.size() - 1);
        int id = first.getId();
        System.out.println("🆔 ID testé = " + id);

        // 3️⃣ Maintenance
        service.planifierMaintenance(id);
        System.out.println("🛠 Maintenance planifiée");

        // 4️⃣ Hors service
        service.marquerHorsService(id);
        System.out.println("⛔ Hors service");

        // 5️⃣ Disponible
        service.marquerDisponible(id);
        System.out.println("✅ De nouveau disponible");

        // 6️⃣ Suppression
        boolean deleted = service.supprimerEquipement(id);
        System.out.println("🗑 Supprimé = " + deleted);
    }
}
