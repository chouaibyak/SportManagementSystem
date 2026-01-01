package com.sport;

import java.util.List;

import com.sport.model.Salle;
import com.sport.model.TypeSalle;
import com.sport.repository.SalleRepository;
import com.sport.service.SalleService;

public class TestSalleApp {

    public static void main(String[] args) {

        SalleRepository salleRepository = new SalleRepository();
        SalleService salleService = new SalleService();

        // ➤ AJOUTER DES SALLES
        Salle salle1 = new Salle("Salle Cardio", 20, TypeSalle.CARDIO);
        Salle salle2 = new Salle("Salle Musculation", 15, TypeSalle.MUSCULATION);
        Salle salle3 = new Salle("Salle Cours Collectifs", 25, TypeSalle.COURS_COLLECTIFS);
        Salle salle4 = new Salle("Piscine", 10, TypeSalle.PISCINE);

        salleService.ajouterSalle(salle1);
        salleService.ajouterSalle(salle2);
        salleService.ajouterSalle(salle3);
        salleService.ajouterSalle(salle4);

        System.out.println("Salles ajoutées avec succès !");

        // ➤ LISTER TOUTES LES SALLES
        List<Salle> toutesSalles = salleService.getToutesLesSalles();
        System.out.println("\n--- LISTE DES SALLES ---");
        for (Salle s : toutesSalles) {
            System.out.println("ID: " + s.getId() + " | Nom: " + s.getNom() + " | Type: " + s.getType() + " | Capacité: " + s.getCapacite());
        }

        // ➤ MODIFIER UNE SALLE
        salle1.setCapacite(30);
        salleService.modifierSalle(salle1);
        System.out.println("\nSalle modifiée : " + salle1.getNom() + " | Nouvelle capacité: " + salle1.getCapacite());

        // ➤ SUPPRIMER UNE SALLE
        salleService.supprimerSalle(salle2.getId());
        System.out.println("\nSalle supprimée : " + salle2.getNom());

        // ➤ LISTER APRÈS SUPPRESSION
        toutesSalles = salleService.getToutesLesSalles();
        System.out.println("\n--- LISTE DES SALLES APRÈS SUPPRESSION ---");
        for (Salle s : toutesSalles) {
            System.out.println("ID: " + s.getId() + " | Nom: " + s.getNom() + " | Type: " + s.getType() + " | Capacité: " + s.getCapacite());
        }
    }
}
