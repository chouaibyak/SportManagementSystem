package com.sport.service;

import com.sport.model.Coach;
import com.sport.model.Membre;
import com.sport.model.Performance;
import com.sport.model.Seance;
import com.sport.repository.CoachRepository;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoachService {

    private static final Logger logger = Logger.getLogger(CoachService.class.getName());
    private CoachRepository coachRepository = new CoachRepository();

    // Ajouter un coach
    public void addCoach(Coach coach) {
        if (isValidCoach(coach)) {
            try {
                coachRepository.save(coach);
                logger.info("Coach ajouté avec succès : " + coach.getNom());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Erreur lors de l'ajout du coach", e);
            }
        } else {
            logger.warning("Erreur : Le coach doit avoir un nom, un prénom et un email valide.");
        }
    }

    // Récupérer un coach par ID
    public Coach getCoachById(String id) {
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

    // Supprimer un coach
    public void deleteCoach(String id) {
        try {
            Coach coach = coachRepository.findById(id);
            if (coach != null) {
                coachRepository.delete(id);
                logger.info("Coach supprimé avec succès : " + coach.getNom());
            } else {
                logger.warning("Erreur : Coach non trouvé pour l'ID : " + id);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la suppression du coach", e);
        }
    }

    // Ajouter une séance pour un coach
    public void creerSeance(Coach coach, Seance seance) {
        if (coach != null && seance != null) {
            if (!isSeanceValid(coach, seance)) {
                logger.warning("Erreur : La séance existe déjà pour ce coach.");
                return;
            }
            coach.getListeSeances().add(seance);
            logger.info("Séance ajoutée pour le coach " + coach.getNom());
        }
    }

    // Modifier une séance
    public void modifierSeance(Coach coach, Seance seance) {
        if (coach != null && seance != null) {
            boolean found = false;
            for (int i = 0; i < coach.getListeSeances().size(); i++) {
                if (coach.getListeSeances().get(i).getId().equals(seance.getId())) {
                    coach.getListeSeances().set(i, seance);  // Remplacer la séance
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
            if (coach.getListeSeances().remove(seance)) {
                logger.info("Séance supprimée.");
            } else {
                logger.warning("Séance non trouvée.");
            }
        }
    }

    // Vérifier la disponibilité d'une salle
    public boolean verifierDisponibiliteSalle(Coach coach, Seance seance) {
        if (coach != null && seance != null) {
            for (Seance s : coach.getListeSeances()) {
                if (s.getSalle().equals(seance.getSalle()) && s.getDate().equals(seance.getDate())) {
                    return false;  // Salle déjà occupée
                }
            }
        }
        return true;  // Salle disponible
    }

    // Consulter la progression d'un membre
    public void consulterProgression(Membre membre) {
        if (membre != null) {
            logger.info("Progression de " + membre.getNom() + ": ");
            for (Performance p : membre.getPerformances()) {
                logger.info("Date: " + p.getDate() + ", Note: " + p.getNote() + ", Valeur: " + p.getValeur());
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
            membre.ajouterPerformance(performance);
            logger.info("Performance notée pour " + membre.getNom() + ": " + performance.getNote());
        }
    }

    // Validité du Coach (nom, prénom, email)
    private boolean isValidCoach(Coach coach) {
        return coach != null && !coach.getNom().isEmpty() && !coach.getPrenom().isEmpty() && !coach.getEmail().isEmpty();
    }

    // Vérification de la validité d'une séance
    private boolean isSeanceValid(Coach coach, Seance seance) {
        return !coach.getListeSeances().contains(seance);
    }
}
