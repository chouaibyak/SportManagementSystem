// package com.sport;

// import java.util.Date;
// import java.util.List;

// import com.sport.model.Coach;
// import com.sport.model.Equipement;
// import com.sport.model.EtatEquipement;
// import com.sport.model.Membre;
// import com.sport.model.Rapport;
// import com.sport.model.Salle;
// import com.sport.model.TypeEquipement;
// import com.sport.model.TypeObjectif;
// import com.sport.model.TypePreference;
// import com.sport.model.TypeSalle;
// import com.sport.service.AdministrateurService;

// public class TestAdministrateur {

//     public static void main(String[] args) {

//         AdministrateurService adminService = new AdministrateurService();

//         System.out.println("=======================================");
//         System.out.println("   TEST ADMINISTRATEUR SERVICE");
//         System.out.println("=======================================");

//         // ==================================================
//         // 1. TEST MEMBRE
//         // ==================================================
//         System.out.println("\n--- TEST MEMBRE ---");

//         Membre membre = new Membre(
//             "Dupont",
//             "Jean",
//             "1990-05-15",
//             "chouaib@test.com",
//             "0601020304",
//             "10 Rue de la Paix",
//             "123",
//             TypeObjectif.PERTE_POIDS,
//             TypePreference.CARDIO
//         );

//         adminService.ajouterMembre(membre);
//         System.out.println("Membre ajouté");

//         List<Membre> membres = adminService.listerMembres();
//         System.out.println("Nombre de membres : " + membres.size());

//         if (!membres.isEmpty()) {
//             Membre membreBDD = membres.get(membres.size() - 1); // dernier ajouté
//             membreBDD.setAdresse("Nouvelle adresse");
//             adminService.modifierMembre(membreBDD);
//             System.out.println("Membre modifié");

//             adminService.supprimerMembre(membreBDD.getId());
//             System.out.println("Membre supprimé");
//         }

//         // ==================================================
//         // 2. TEST COACH
//         // ==================================================
//         System.out.println("\n--- TEST COACH ---");

//         Coach coach = new Coach(
//             "Martin",
//             "Paul",
//             "1985-03-20",
//             "paul@test.com",
//             "0611223344",
//             "Coach Street",
//             "123"
//         );

//         adminService.ajouterCoach(coach);
//         System.out.println("Coach ajouté");

//         List<Coach> coachs = adminService.listerCoachs();
//         System.out.println("Nombre de coachs : " + coachs.size());

//         if (!coachs.isEmpty()) {
//             Coach coachBDD = coachs.get(coachs.size() - 1); // dernier ajouté

//             // modifier infos utilisateur
//             coachBDD.setTelephone("0699999999");

//             // modifier spécialités si gérées via le modèle
//             if (coachBDD.getSpecialites() != null) {
//                 coachBDD.getSpecialites().add("CrossFit");
//             }

//             adminService.modifierCoach(coachBDD);
//             System.out.println("Coach modifié");

//             adminService.supprimerCoach(coachBDD.getId());
//             System.out.println("Coach supprimé");
//         }

//         // ==================================================
//         // 3. TEST EQUIPEMENT
//         // ==================================================
//         System.out.println("\n--- TEST EQUIPEMENT ---");

//         Equipement equipement = new Equipement(
//             "Tapis de course",
//             TypeEquipement.VELO,
//             EtatEquipement.EN_MAINTENANCE,
//             new Date()
//         );

//         adminService.ajouterEquipement(equipement);
//         System.out.println("Équipement ajouté");

//         List<Equipement> equipements = adminService.listerEquipements();
//         System.out.println("Nombre d'équipements : " + equipements.size());

//         if (!equipements.isEmpty()) {
//             Equipement equipBDD = equipements.get(equipements.size() - 1);
//             equipBDD.setEtat(EtatEquipement.EN_MAINTENANCE);
//             adminService.modifierEquipement(equipBDD);
//             System.out.println("Équipement modifié");

//             List<Equipement> enMaintenance =
//                 adminService.listerEquipementsParEtat(EtatEquipement.EN_MAINTENANCE);
//             System.out.println("Équipements en maintenance : " + enMaintenance.size());

//             adminService.supprimerEquipement(equipBDD.getId());
//             System.out.println("Équipement supprimé");
//         }

//         // ==================================================
//         // 4. TEST SALLE
//         // ==================================================
//         System.out.println("\n--- TEST SALLE ---");

//         Salle salle = new Salle("Salle Cardio", 30, TypeSalle.CARDIO);

//         adminService.ajouterSalle(salle);
//         System.out.println("Salle ajoutée");

//         List<Salle> salles = adminService.listerSalles();
//         System.out.println("Nombre de salles : " + salles.size());

//         if (!salles.isEmpty()) {
//             Salle salleBDD = salles.get(salles.size() - 1);
//             salleBDD.setCapacite(40);
//             adminService.modifierSalle(salleBDD);
//             System.out.println("Salle modifiée");

//         boolean dispo = adminService.verifierDisponibiliteSalle(salleBDD.getId(), "2025-01-10 10:00:00");
//         System.out.println("Salle disponible ? " + dispo);

//             adminService.supprimerSalle(salleBDD.getId());
//             System.out.println("Salle supprimée");
//         }

//         // ==================================================
//         // 5. TEST RAPPORT
//         // ==================================================
//         System.out.println("\n--- TEST RAPPORT ---");

//         Rapport rapport = adminService.genererRapport(
//             "STATISTIQUES_GLOBALES",
//             "2025-01-01",
//             "2025-01-31"
//         );

//         System.out.println("Rapport généré : " + rapport.getType());

//         List<Rapport> rapports = adminService.listerRapports();
//         System.out.println("Nombre de rapports : " + rapports.size());

//         if (!rapports.isEmpty()) {
//             adminService.supprimerRapport(rapports.get(rapports.size() - 1).getId());
//             System.out.println("Rapport supprimé");
//         }

//         System.out.println("\n=======================================");
//         System.out.println("        FIN DES TESTS");
//         System.out.println("=======================================");
//     }
// }
