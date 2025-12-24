package com.sport;

import com.sport.model.*;
import com.sport.service.AdministrateurService;

import java.util.Date;
import java.util.List;

public class TestAdministrateur {

    public static void main(String[] args) {

        AdministrateurService adminService = new AdministrateurService();

        System.out.println("=======================================");
        System.out.println("   TEST ADMINISTRATEUR SERVICE");
        System.out.println("=======================================");

        // ==================================================
        // 1. TEST MEMBRE
        // ==================================================
        System.out.println("\n--- TEST MEMBRE ---");

        Membre membre = new Membre(
            "Dupont",
            "Jean",
            "1990-05-15",
            "jean.dupont@test.com",
            "0601020304",
            "10 Rue de la Paix",
            "123",
            TypeObjectif.PERTE_POIDS,
            TypePreference.CARDIO
        );

        adminService.ajouterMembre(membre);
        System.out.println("Membre ajouté");

        List<Membre> membres = adminService.listerMembres();
        System.out.println("Liste des membres : " + membres.size());

        Membre membreBDD = membres.get(0);
        membreBDD.setAdresse("Nouvelle adresse");
        adminService.modifierMembre(membreBDD);
        System.out.println("Membre modifié");

        adminService.supprimerMembre(membreBDD.getId());
        System.out.println("Membre supprimé");

        // ==================================================
        // 2. TEST COACH
        // ==================================================
        System.out.println("\n--- TEST COACH ---");

        Coach coach = new Coach(
            "Martin",
            "Paul",
            "1985-03-20",
            "paul.martin@test.com",
            "0611223344",
            "Coach Street",
            "Fitness"
        );

        adminService.ajouterCoach(coach);
        System.out.println("Coach ajouté");

        List<Coach> coachs = adminService.listerCoachs();
        System.out.println("Nombre de coachs : " + coachs.size());

        Coach coachBDD = coachs.get(0);
        coachBDD.getSpecialites().add("CrossFit");
        adminService.modifierCoach(coachBDD);
        System.out.println("Coach modifié");

        adminService.supprimerCoach(coachBDD.getId());
        System.out.println("Coach supprimé");

        // ==================================================
        // 3. TEST EQUIPEMENT
        // ==================================================
        System.out.println("\n--- TEST EQUIPEMENT ---");

        Equipement equipement = new Equipement(
             "Tapis de course",             // nom
            TypeEquipement.VELO,         // type
            EtatEquipement.EN_MAINTENANCE, 
            new Date()     
        );

        adminService.ajouterEquipement(equipement);
        System.out.println("Équipement ajouté");

        List<Equipement> equipements = adminService.listerEquipements();
        System.out.println("Nombre d'équipements : " + equipements.size());

        Equipement equipBDD = equipements.get(0);
        equipBDD.setEtat(EtatEquipement.EN_MAINTENANCE);
        adminService.modifierEquipement(equipBDD);
        System.out.println("Équipement modifié");

        List<Equipement> enPanne = adminService.listerEquipementsParEtat(EtatEquipement.EN_MAINTENANCE);
        System.out.println("Équipements en panne : " + enPanne.size());

        adminService.supprimerEquipement(equipBDD.getId());
        System.out.println("Équipement supprimé");

        // ==================================================
        // 4. TEST SALLE
        // ==================================================
        System.out.println("\n--- TEST SALLE ---");

        Salle salle = new Salle(
            "Salle Cardio",
            30,
            TypeSalle.CARDIO
        );

        adminService.ajouterSalle(salle);
        System.out.println("Salle ajoutée");

        List<Salle> salles = adminService.listerSalles();
        System.out.println("Nombre de salles : " + salles.size());

        Salle salleBDD = salles.get(0);
        salleBDD.setCapacite(40);
        adminService.modifierSalle(salleBDD);
        System.out.println("Salle modifiée");

        boolean dispo = adminService.verifierDisponibiliteSalle(salleBDD.getId(), "2025-01-10 10:00");
        System.out.println("Salle disponible ? " + dispo);

        adminService.supprimerSalle(salleBDD.getId());
        System.out.println("Salle supprimée");

        // ==================================================
        // 5. TEST RAPPORT
        // ==================================================
        System.out.println("\n--- TEST RAPPORT ---");

        Rapport rapport = adminService.genererRapport(
            "ACTIVITES",
            "2025-01-01",
            "2025-01-31"
        );

        System.out.println("Rapport généré : " + rapport.getType());

        List<Rapport> rapports = adminService.listerRapports();
        System.out.println("Nombre de rapports : " + rapports.size());

        if (!rapports.isEmpty()) {
            adminService.supprimerRapport(rapports.get(0).getId());
            System.out.println("Rapport supprimé");
        }

        System.out.println("\n=======================================");
        System.out.println("        FIN DES TESTS");
        System.out.println("=======================================");
    }
}
