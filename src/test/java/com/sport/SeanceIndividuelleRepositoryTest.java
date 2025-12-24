package com.sport;

import java.time.LocalDateTime;

import com.sport.model.Coach;
import com.sport.model.Membre;
import com.sport.model.Salle;
import com.sport.model.SeanceIndividuelle;
import com.sport.model.TypeCours;
import com.sport.repository.CoachRepository;
import com.sport.repository.MembreRepository;
import com.sport.repository.SalleRepository;
import com.sport.repository.SeanceIndividuelleRepository;

public class SeanceIndividuelleRepositoryTest {

    public static void main(String[] args) {

        // Repositories
        SeanceIndividuelleRepository repo = new SeanceIndividuelleRepository();
        SalleRepository salleRepo = new SalleRepository();
        CoachRepository coachRepo = new CoachRepository();
        MembreRepository membreRepo = new MembreRepository();

        // 1️⃣ Load existing data from DB (IMPORTANT)
        Salle salle = salleRepo.getSalleById(1);
        Coach coach = coachRepo.getCoachById(1);
        Membre membre = membreRepo.trouverParId(1);

       // if (salle == null || coach == null || membre == null) {
        //    System.out.println("❌ Salle / Coach / Membre not found in DB");
        //    return;
        //}

        // 2️⃣ Create SeanceIndividuelle
        SeanceIndividuelle seance = new SeanceIndividuelle(
                0,
                "Coaching personnel",
                1,
                salle,
                LocalDateTime.now().plusDays(1),
                coach,
                TypeCours.MUSCULATION,
                60,
                membre,
                150.0,
                "Bonne séance"
        );

        // 3️⃣ INSERT
        repo.ajouter(seance);
        System.out.println("✅ Seance ajoutée avec ID = " + seance.getId());

        // 4️⃣ GET BY ID
        SeanceIndividuelle found = repo.getById(seance.getId());
        if (found != null) {
            System.out.println("✅ Séance trouvée : " + found.getNom());
            System.out.println("   Coach : " + found.getEntraineur().getNom());
            System.out.println("   Membre : " + found.getMembre().getNom());
        } else {
            System.out.println("❌ Séance introuvable");
        }

        // 5️⃣ GET ALL
        System.out.println("\n📋 Liste des séances individuelles :");
        for (SeanceIndividuelle s : repo.getAll()) {
            System.out.println("- " + s.getId() + " | " + s.getNom());
        }

        // 6️⃣ DELETE
        //boolean deleted = repo.delete(seance.getId());
        //System.out.println(deleted ? "🗑️ Séance supprimée" : "❌ Suppression échouée");
    }
}
