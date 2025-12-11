package com.sport.service;

import com.sport.model.Coach;
import com.sport.model.Membre;
import com.sport.model.Performance;
import com.sport.model.Seance;
import com.sport.repository.CoachRepository;
import com.sport.repository.PerformanceRepository;
import com.sport.repository.SeanceRepository;

import java.util.List;

public class CoachService {

    // On instancie le repository pour pouvoir l'utiliser
    private CoachRepository coachRepository = new CoachRepository();
    private SeanceRepository seanceRepository = new SeanceRepository();
    private PerformanceRepository performanceRepository = new PerformanceRepository();

    // ============================================================
    // GESTION DES COACHS
    // ============================================================

    // Récupérer un coach par ID
    public Coach getCoachById(int id) {
        // CORRECTION : Appel au repository (avant c'était une boucle infinie)
        return coachRepository.getCoachById(id);
    }

    // Récupérer tous les coachs
    public List<Coach> getAllCoaches() {
        // CORRECTION : Le nom de la méthode dans le repo est 'listerCoachs'
        List<Coach> coaches = coachRepository.listerCoachs();
        if (coaches.isEmpty()) {
            System.out.println("Aucun coach trouvé.");
        }
        return coaches;
    }

    public void ajouterCoach(Coach coach) {
        // Validation basique
        if (coach != null && coach.getNom() != null) {
            coachRepository.ajouterCoach(coach);
        } else {
            System.out.println("Données du coach invalides.");
        }
    }

    public void modifierCoach(Coach coach) {
        if (coach != null && coach.getId() > 0) {
            coachRepository.modifierCoach(coach);
        }
    }

    public void supprimerCoach(int id) {
        coachRepository.supprimerCoach(id);
    }

    // ============================================================
    // GESTION DES SÉANCES (Déléguée au Repository)
    // ============================================================

    public void creerSeance(Coach coach, Seance seance) {
        if (coach == null || seance == null) {
            System.out.println("Erreur : Coach ou Séance null.");
            return;
        }

        // Vérification disponibilité salle avant création
        if (seance.getSalle() != null) {
             boolean salleLibre = verifierDisponibiliteSalle(coach, seance);
             if (!salleLibre) {
                 System.out.println("Erreur : La salle n'est pas disponible à cette heure.");
                 return;
             }
        }

        seanceRepository.creerSeance(coach, seance);
        System.out.println("Séance ajoutée pour le coach " + coach.getNom());
    }

  public void modifierSeance(Coach coach, Seance seance) {
        if (coach != null && seance != null) {
            seanceRepository.modifierSeance(seance, coach.getId());   
            System.out.println("Séance modifiée en base de données.");
        }
    }

    public void supprimerSeance(Coach coach, Seance seance) {
        if (coach != null && seance != null) {
            seanceRepository.supprimerSeance(seance.getId(), coach.getId());
        } else {
            System.out.println("Erreur : Coach ou Séance null.");
        }
    }

    // Dans CoachService.java

public boolean verifierDisponibiliteSalle(Coach coach, Seance seance) {
    if (seance != null && seance.getSalle() != null && seance.getDateHeure() != null) {        
        return seanceRepository.verifierDisponibiliteSalle(
            seance.getSalle(), 
            seance.getDateHeure() // C'est déjà un LocalDateTime
        );
    }
    return false;
}

    // ============================================================
    // GESTION DES MEMBRES & PERFORMANCES
    // ============================================================

     public void consulterProgression(Membre membre) {
        if (membre != null) {
            System.out.println("--- Progression de " + membre.getNom() + " ---");

            // Appel à PerformanceRepository
            List<Performance> liste = performanceRepository.trouverPerformanceParMembreId(membre.getId());

            if (liste.isEmpty()) {
                System.out.println("Aucune performance enregistrée.");
            } else {
                for (Performance p : liste) {
                    System.out.println("Date : " + p.getDateMesure() 
                                     + " | Poids : " + p.getPoids() + "kg"
                                     + " | IMC : " + p.getImc()
                                     + " | Force : " + p.getForce());
                }
            }
            System.out.println("-----------------------------------");
        } else {
            System.out.println("Erreur : Membre invalide.");
        }
    }

    public void donnerFeedback(Membre membre, String commentaire) {
        if (membre != null && commentaire != null) {
            // Idéalement, il faudrait sauvegarder ce feedback en BDD
            // Exemple : performanceRepository.ajouterFeedback(membre.getId(), commentaire);
            System.out.println("Feedback enregistré (simulation) pour " + membre.getNom() + ": " + commentaire);
        }
    }

    public void noterPerformance(Membre membre, Performance performance) {
        if (membre != null && performance != null) {
            performance.setMembre(membre);
            performanceRepository.ajouterPerformance(performance);
            System.out.println("Performance notée pour " + membre.getNom());
        } else {
            System.out.println("Erreur : Membre ou Performance null.");
        }
    }
}