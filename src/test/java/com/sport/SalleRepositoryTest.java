package com.sport;

import java.util.List;

import com.sport.model.Salle;
import com.sport.model.TypeSalle;
import com.sport.repository.SalleRepository;

public class SalleRepositoryTest {

    public static void main(String[] args) {

        SalleRepository repository = new SalleRepository();

        // CREATE
        Salle salle = new Salle("Salle Test JDBC", 20, TypeSalle.CARDIO);
        repository.ajouterSalle(salle);
        System.out.println("Salle ajoutée avec ID = " + salle.getId());

        // READ ALL
        List<Salle> salles = repository.listerSalles();
        System.out.println("Toutes les salles :");
        salles.forEach(s -> System.out.println(
                s.getId() + " - " + s.getNom() + " (" + s.getCapacite() + ")"
        ));

        // READ BY ID
        Salle found = repository.getSalleById(salle.getId());
        System.out.println("Salle trouvée : " + found.getNom());

        // UPDATE
        salle.setCapacite(50);
        repository.modifierSalle(salle);
        System.out.println("Salle modifiée.");

        // DELETE
        repository.supprimerSalle(salle.getId());
        System.out.println("Salle supprimée.");
    }
}
