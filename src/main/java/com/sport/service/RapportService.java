package com.sport.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.sport.model.Rapport;
import com.sport.repository.RapportRepository;

public class RapportService {
    private RapportRepository rapportRepository;
    
    public RapportService() {
        this.rapportRepository = new RapportRepository();
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
            case "OCCUPATION_SALLES":
                rapport.setDonnees(genererRapportOccupationSalles(dateDebut, dateFin));
                break;
            case "UTILISATION_EQUIPEMENTS":
                rapport.setDonnees(genererRapportUtilisationEquipements(dateDebut, dateFin));
                break;
            case "REVISIONS_AJOURNEMENTS":
                rapport.setDonnees(genererRapportRevisionsAjournements(dateDebut, dateFin));
                break;
            case "SATISFACTION_MEMBRES":
                rapport.setDonnees(genererRapportSatisfactionMembres(dateDebut, dateFin));
                break;
            case "STATISTIQUES_SEANCES":
                rapport.setDonnees(genererRapportStatistiquesSeances(dateDebut, dateFin));
                break;
            case "PERFORMANCE_COACH":
                rapport.setDonnees(genererRapportPerformanceCoach(dateDebut, dateFin));
                break;
            case "STATISTIQUES_GLOBALES":
                rapport.setDonnees(genererRapportStatistiquesGlobales(dateDebut, dateFin));
                break;
            default:
                rapport.setDonnees("Type de rapport non reconnu");
        }
        
        // Sauvegarder le rapport généré
        rapportRepository.ajouterRapport(rapport);
        return rapport;
    }
    
    // Méthodes spécifiques pour chaque type de rapport
    private String genererRapportOccupationSalles(String dateDebut, String dateFin) {
        
        // TODO: Récupérer données depuis ReservationRepository/SeanceRepository
        // Calculer taux d'occupation par salle
        return "Salle A: 85%, Salle B: 70%, Salle C: 92% - Taux moyen: 82.3%";
    }
    
    private String genererRapportUtilisationEquipements(String dateDebut, String dateFin) {
        // TODO: Récupérer données depuis EquipementRepository
        // Calculer fréquence d'utilisation
        return "Vélo: 120 utilisations, Tapis: 95, Haltères: 200 - Total: 415";
    }
    
    private String genererRapportRevisionsAjournements(String dateDebut, String dateFin) {
        // TODO: Récupérer données depuis AbonnementRepository
        // Compter révisions et ajournements
        return "Révisions: 12, Ajournements: 5, Motif principal: Blessure (40%)";
    }
    
    private String genererRapportSatisfactionMembres(String dateDebut, String dateFin) {
        // TODO: Récupérer évaluations depuis MembreRepository
        // Calculer satisfaction moyenne
        return "Note moyenne: 4.5/5, NPS: 72, Taux de satisfaction: 89%";
    }
    
    private String genererRapportStatistiquesSeances(String dateDebut, String dateFin) {
        // TODO: Récupérer données depuis SeanceRepository
        // Calculer statistiques de séances
        return "Total séances: 245, Taux présence: 87%, Collectives: 60%, Individuelles: 40%";
    }
    
    private String genererRapportPerformanceCoach(String dateDebut, String dateFin) {
        // TODO: Récupérer données depuis CoachRepository/SeanceRepository
        // Évaluer performance des coachs
        return "Coach A: 45 séances (93% présence), Coach B: 38 séances (89% présence)";
    }
    
    private String genererRapportStatistiquesGlobales(String dateDebut, String dateFin) {
        // TODO: Agréger toutes les données
        // Vue d'ensemble complète
        return "Membres actifs: 350, Revenus: 45000€, Taux rétention: 92%, Croissance: +8%";
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
