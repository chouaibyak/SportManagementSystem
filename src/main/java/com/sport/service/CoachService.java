package com.sport.service;

import com.sport.model.Coach;
import com.sport.model.Membre;
import com.sport.model.Performance;
import com.sport.model.Seance;
import com.sport.repository.CoachRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoachService {

    private static final Logger logger = Logger.getLogger(CoachService.class.getName());
    private CoachRepository coachRepository = new CoachRepository();

    // Récupérer un coach par ID
    public Coach getCoachById(int id) {
        Coach coach = null;
        try {
            coach = coachRepository.findById(id);
            if (coach == null) {
                logger.warning("Coach non trouvé pour l'ID : " + id);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération du coach par ID", e);
        }
        return coach;
    }

    // Récupérer tous les coachs
    public List<Coach> getAllCoaches() {
        List<Coach> coaches = null;
        try {
            coaches = coachRepository.findAll();
            if (coaches.isEmpty()) {
                logger.info("Aucun coach trouvé.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la récupération des coachs", e);
        }
        return coaches;
    }

    // Ajouter une séance pour un coach
    public void creerSeance(Coach coach, Seance seance) {
        if (coach != null && seance != null) {
            if (!isSeanceValid(coach, seance)) {
                logger.warning("Erreur : La séance existe déjà pour ce coach.");
                return;
            }
            coachRepository.creerSeance(coach, seance);
            logger.info("Séance ajoutée pour le coach " + coach.getNom());
        }
    }

    // Modifier une séance
    public void modifierSeance(Coach coach, Seance seance) {
        if (coach != null && seance != null) {
            boolean found = false;
            for (Seance s : coach.getSeances()) {
                if (s.getId() == seance.getId()) {
                    coach.getSeances().remove(s);  // Supprimer l'ancienne séance
                    coach.getSeances().add(seance); // Ajouter la nouvelle séance
                    found = true;
                    logger.info("Séance modifiée.");
                    break;
                }
            }
            if (!found) {
                logger.warning("Séance non trouvée.");
            }
        }
    }

    // Supprimer une séance
    public void supprimerSeance(Coach coach, Seance seance) {
        if (coach != null && seance != null) {
            if (coach.getSeances().remove(seance)) {
                logger.info("Séance supprimée.");
            } else {
                logger.warning("Séance non trouvée.");
            }
        }
    }

  public boolean verifierDisponibiliteSalle(Coach coach, Seance seance) {
    if (coach != null && seance != null) {
        LocalDateTime dateHeure = seance.getDateHeure();
        Timestamp ts = Timestamp.valueOf(dateHeure);  // conversion LocalDateTime -> Timestamp
        return coachRepository.verifierDisponibiliteSalle(seance.getSalle(), ts);
    }
    return true;  // La salle est disponible par défaut
}
    // Consulter la progression d'un membre
    public void consulterProgression(Membre membre) {
        if (membre != null) {
            logger.info("Progression de " + membre.getNom() + ": ");
            for (Performance p : membre.getPerformances()) {
                logger.info("Date: " + p.getDateMesure() + ", poids: " + p.getPoids() );
            }
        }
    }

    // Donner un feedback à un membre
    public void donnerFeedback(Membre membre, String commentaire) {
        if (membre != null && commentaire != null) {
            logger.info("Feedback pour " + membre.getNom() + ": " + commentaire);
        }
    }

    // Recommander une séance individuelle à un membre
    public void recommanderSeanceIndividuelle(Membre membre) {
        if (membre != null) {
            logger.info("Séance recommandée pour " + membre.getNom());
        }
    }

    // Noter la performance d'un membre
    public void noterPerformance(Membre membre, Performance performance) {
        if (membre != null && performance != null) {
         
        }
    }

    // Vérification de la validité d'une séance
    private boolean isSeanceValid(Coach coach, Seance seance) {
        return !coach.getSeances().contains(seance);
    }
}
