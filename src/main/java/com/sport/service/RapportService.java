package com.sport.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.sport.model.Abonnement;
import com.sport.model.Membre;
import com.sport.model.Rapport;
import com.sport.model.Reservation;
import com.sport.model.Seance;
import com.sport.model.TypeSeance;
import com.sport.repository.*;


public class RapportService {
    RapportRepository rapportRepository = new RapportRepository();
    SeanceRepository seanceRepository  = new SeanceRepository();
    ReservationRepository reservationRepository = new ReservationRepository();
    MembreRepository membreRepository = new MembreRepository();
    AbonnementRepository abonnementRepository = new AbonnementRepository();
   

 // Générer un rapport selon son type
    public Rapport genererRapport(String type, String dateDebut, String dateFin) {
        System.out.println("\n=== Génération du rapport de type: " + type + " ===");
        System.out.println("Période: " + dateDebut + " au " + dateFin);
        
        Rapport rapport = new Rapport(type, dateDebut, dateFin);
        
        // Date de création
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        rapport.setDateDebut(now.format(formatter));
        
        // Appel de la méthode spécifique selon le type
         switch(type) {
            case "OCCUPATION_COURS":
                rapport.setDonnees(genererRapportOccupationCours(dateDebut, dateFin));
                break;
            case "FREQUENTATION_SALLE":
                rapport.setDonnees(genererRapportFrequentationSalle(dateDebut, dateFin));
                break;
            case "SATISFACTION_MEMBRES":
                rapport.setDonnees(genererRapportSatisfactionMembres(dateDebut, dateFin));
                break;
            case "REVENUS_ABONNEMENTS":
                rapport.setDonnees(genererRapportRevenusAbonnements(dateDebut, dateFin));
                break;
            default:
                rapport.setDonnees("Type de rapport non reconnu");
        }
        
        // Sauvegarder le rapport généré
        rapportRepository.ajouterRapport(rapport);
        return rapport;
    }
    
    // Méthodes spécifiques pour chaque type de rapport
 private String genererRapportOccupationCours(String dateDebut, String dateFin) {
        LocalDate debut = LocalDate.parse(dateDebut);
        LocalDate fin = LocalDate.parse(dateFin);
        
        List<Seance> seances = seanceRepository.getSeancesParPeriode(debut, fin);
        
        if (seances.isEmpty()) {
            return "Aucune séance programmée pour cette période";
        }
        
        // Compter par type de séance
        int collectives = 0;
        int individuelles = 0;
        int capaciteTotale = 0;
        
        for (Seance seance : seances) {
            if (seance.getTypeSeance() == TypeSeance.COLLECTIVE) {
                collectives++;
                capaciteTotale += seance.getCapaciteMax();
            } else if (seance.getTypeSeance() == TypeSeance.INDIVIDUELLE) {
                individuelles++;
            }
        }
        
        int totalCours = seances.size();
        double pctCollectives = (collectives * 100.0) / totalCours;
        double pctIndividuelles = (individuelles * 100.0) / totalCours;
        double capaciteMoyenne = collectives > 0 ? 
            (double) capaciteTotale / collectives : 0;
        
        return String.format("Total cours programmés: %d | Collectifs: %d (%.0f%%) | " +
                           "Individuels: %d (%.0f%%) | Capacité totale: %d places | " +
                           "Capacité moyenne/cours: %.1f places",
                           totalCours, collectives, pctCollectives, 
                           individuelles, pctIndividuelles, capaciteTotale, 
                           capaciteMoyenne);
    }
    
    private String genererRapportFrequentationSalle(String dateDebut, String dateFin) {
        LocalDate debut = LocalDate.parse(dateDebut);
    LocalDate fin = LocalDate.parse(dateFin);

    // Récupérer toutes les réservations sur la période
    List<Reservation> reservations = reservationRepository.getReservationsParPeriode(debut, fin);

    if (reservations.isEmpty()) {
        return "Aucune fréquentation enregistrée pour cette période";
    }

    // Compteurs par type de cours
    int yoga = 0, musculation = 0, cardio = 0, pilates = 0;

    for (Reservation res : reservations) {
        if (res.getSeance() != null && res.getSeance().getTypeCours() != null) {
            switch(res.getSeance().getTypeCours()) {
                case YOGA -> yoga++;
                case MUSCULATION -> musculation++;
                case CARDIO -> cardio++;
                case PILATES -> pilates++;
            }
        }
    }

    int totalVisites = reservations.size();

    return String.format(
        "Total visites: %d | Yoga: %d | Musculation: %d | Cardio: %d | Pilates: %d",
        totalVisites, yoga, musculation, cardio, pilates
    );
}
    }
    
    private String genererRapportRevenusAbonnements(String dateDebut, String dateFin) {
    List<Abonnement> abonnements = abonnementRepository.listerTout();
    
    if (abonnements.isEmpty()) {
        return "Aucun abonnement dans le système";
    }
    
    // Calculer sans filtrer par période
    int mensuel = 0, trimestriel = 0, annuel = 0;
    double revenuTotal = 0;
    
    for (Abonnement abo : abonnements) {
        double montant = abo.getMontant();
        revenuTotal += montant;
        
        String type = abo.getTypeAbonnement().name();
        if (type.contains("Mensuel")) mensuel++;
        else if (type.contains("Trimestriel")) trimestriel++;
        else if (type.contains("Annuel")) annuel++;
    }
    
    return String.format("Total abonnements: %d | Revenu total: %.2f€ | " +
                       "Mensuel: %d | Trimestriel: %d | Annuel: %d",
                       abonnements.size(), revenuTotal, mensuel, trimestriel, annuel);
}

    
    private String genererRapportSatisfactionMembres(String dateDebut, String dateFin) {
        LocalDate debut = LocalDate.parse(dateDebut);
        LocalDate fin = LocalDate.parse(dateFin);
        
        // Récupérer tous les membres actifs de la période
        List<Membre> membres = membreRepository.listerMembres();
        
        if (membres.isEmpty()) {
            return "Aucun membre actif pour cette période";
        }
        
        // Calculer statistiques sur les membres évalués pendant la période
        double sommeNotes = 0;
        int nbEvalues = 0;
        int tresSatisfaits = 0;  // note >= 4.5
        int satisfaits = 0;       // note >= 3.5
        int insatisfaits = 0;     // note < 3.5
        
        for (Membre membre : membres) {
            // Vérifier si le membre a une évaluation dans la période
            if (membre.getNoteSatisfaction() != null && 
                membre.getDateEvaluation() != null &&
                !membre.getDateEvaluation().isBefore(debut) &&
                !membre.getDateEvaluation().isAfter(fin)) {
                
                double note = membre.getNoteSatisfaction();
                sommeNotes += note;
                nbEvalues++;
                
                if (note >= 4.5) tresSatisfaits++;
                else if (note >= 3.5) satisfaits++;
                else insatisfaits++;
            }
        }
        
        if (nbEvalues == 0) {
            return "Aucune évaluation disponible pour cette période";
        }
        
        double noteMoyenne = sommeNotes / nbEvalues;
        double tauxSatisfaction = ((tresSatisfaits + satisfaits) * 100.0) / nbEvalues;
        double pctTresSatisfaits = (tresSatisfaits * 100.0) / nbEvalues;
        double tauxEvaluation = (nbEvalues * 100.0) / membres.size();
        
        return String.format("Membres actifs: %d | Évaluations: %d (%.0f%%) | " +
                           "Note moyenne: %.1f/5 | Taux satisfaction: %.0f%% | " +
                           "Très satisfaits: %.0f%% | Satisfaits: %d | Insatisfaits: %d",
                           membres.size(), nbEvalues, tauxEvaluation, noteMoyenne, 
                           tauxSatisfaction, pctTresSatisfaits, satisfaits, insatisfaits);
    }
    
    // Récupérer tous les rapports
    public List<Rapport> listerRapports() {
        return rapportRepository.listerRapports();
    }
    
    // Supprimer un rapport
    public void supprimerRapport(int rapportId) {
        rapportRepository.supprimerRapport(rapportId);
    }
