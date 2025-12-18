package com.sport.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.sport.model.Abonnement;
import com.sport.model.Rapport;
import com.sport.model.Reservation;
import com.sport.model.Seance;
import com.sport.repository.AbonnementRepository;
import com.sport.repository.MembreRepository;
import com.sport.repository.RapportRepository;
import com.sport.repository.ReservationRepository;
import com.sport.repository.SeanceRepository;

public class RapportService {
    private RapportRepository rapportRepository;
    private SeanceRepository seanceRepository;
    private ReservationRepository reservationRepository;
    private MembreRepository membreRepository;
    private AbonnementRepository abonnementRepository;
    
    public RapportService() {
        this.rapportRepository = new RapportRepository();
        this.seanceRepository = new SeanceRepository();
        this.reservationRepository = new ReservationRepository();
        this.membreRepository = new MembreRepository();
        this.abonnementRepository = new AbonnementRepository();
    }

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

    // Récupérer toutes les séances planifiées sur la période
    List<Seance> seances = seanceRepository.getSeancesParPeriode(debut, fin);
    if (seances.isEmpty()) return "Aucune séance programmée pour cette période.";

    int totalCours = seances.size();
    int totalCollectifs = 0;
    int totalIndividuels = 0;
    int participantsTotal = 0;
    int capaciteTotal = 0;

    for (Seance s : seances) {
        if ("COLLECTIVE".equalsIgnoreCase(s.getTypeCours().toString())) {
            totalCollectifs++;
            participantsTotal += s.getNombreParticipants();
            capaciteTotal += s.getCapaciteMax();
        } else {
            totalIndividuels++;
        }
    }

    double tauxOccupation = capaciteTotal > 0 ? 
        (participantsTotal * 100.0) / capaciteTotal : 0;

    double pctCollectifs = (totalCollectifs * 100.0) / totalCours;

    return String.format(
        "Total cours: %d | Collectifs: %d (%.0f%%) | Individuels: %d | Participants: %d/%d | Taux occupation: %.1f%%",
        totalCours, totalCollectifs, pctCollectifs, totalIndividuels, participantsTotal, capaciteTotal, tauxOccupation
    );
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
    LocalDate debut = LocalDate.parse(dateDebut);
    LocalDate fin = LocalDate.parse(dateFin);

    // Récupérer tous les abonnements sur la période
    List<Abonnement> abonnements = abonnementRepository.getAbonnementsParPeriode(debut, fin);

    if (abonnements.isEmpty()) {
        return "Aucun abonnement enregistré pour cette période";
    }

    // Compteurs et revenus par type d'abonnement
    int mensuel = 0, trimestriel = 0, annuel = 0;
    double revenuMensuel = 0, revenuTrimestriel = 0, revenuAnnuel = 0;

    for (Abonnement abo : abonnements) {
        if (abo.getTypeAbonnement() != null) {
            switch (abo.getTypeAbonnement()) {
                case "MENSUEL" -> {
                    mensuel++;
                    revenuMensuel += abo.getMontant();
                }
                case "TRIMESTRIEL" -> {
                    trimestriel++;
                    revenuTrimestriel += abo.getMontant();
                }
                case "ANNUEL" -> {
                    annuel++;
                    revenuAnnuel += abo.getMontant();
                }
            }
        }
    }

    double revenuTotal = revenuMensuel + revenuTrimestriel + revenuAnnuel;
    int totalAbonnements = abonnements.size();
    double revenuMoyen = revenuTotal / totalAbonnements;

    return String.format(
        "Total abonnements: %d | Revenu total: %.2f€ | Revenu moyen: %.2f€ | " +
        "Mensuel: %d (%.2f€) | Trimestriel: %d (%.2f€) | Annuel: %d (%.2f€)",
        totalAbonnements, revenuTotal, revenuMoyen,
        mensuel, revenuMensuel, trimestriel, revenuTrimestriel,
        annuel, revenuAnnuel
    );
}

    
    private String genererRapportSatisfactionMembres(String dateDebut, String dateFin) {
    LocalDate debut = LocalDate.parse(dateDebut);
    LocalDate fin = LocalDate.parse(dateFin);

    // Récupérer toutes les évaluations des membres pour la période
    List<Evaluation> evaluations = membreRepository.getEvaluationsParPeriode(debut, fin);

    if (evaluations.isEmpty()) {
        return "Aucune évaluation disponible pour cette période";
    }

    double sommeNotes = 0;
    int tresSatisfaits = 0; // note >= 4.5
    int satisfaits = 0;     // note >= 3.5 et < 4.5
    int insatisfaits = 0;   // note < 3.5

    for (Evaluation eval : evaluations) {
        double note = eval.getNote();
        sommeNotes += note;

        if (note >= 4.5) tresSatisfaits++;
        else if (note >= 3.5) satisfaits++;
        else insatisfaits++;
    }

    double noteMoyenne = sommeNotes / evaluations.size();
    double tauxSatisfaction = ((tresSatisfaits + satisfaits) * 100.0) / evaluations.size();
    double pctTresSatisfaits = (tresSatisfaits * 100.0) / evaluations.size();

    return String.format(
        "Évaluations: %d | Note moyenne: %.1f/5 | " +
        "Taux de satisfaction: %.0f%% | Très satisfaits: %.0f%% | " +
        "Satisfaits: %d | Insatisfaits: %d",
        evaluations.size(),
        noteMoyenne,
        tauxSatisfaction,
        pctTresSatisfaits,
        satisfaits,
        insatisfaits
    );
}

    
    
    // Récupérer tous les rapports
    public List<Rapport> obtenirTousLesRapports() {
        return rapportRepository.listerRapports();
    }
    
    // Supprimer un rapport
    public void supprimerRapport(int rapportId) {
        rapportRepository.supprimerRapport(rapportId);
    }
} 
