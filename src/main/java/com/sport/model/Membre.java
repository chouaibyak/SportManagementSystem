package com.sport.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Membre extends Utilisateur {

    public static Object Objectif;

    // --- Attributs ---
    private TypeObjectif objectifSportif;
    private TypePreference preferences;
     private Double noteSatisfaction;  // Note de 1 à 5
    private LocalDate dateEvaluation; // Date de la dernière évaluation

    // Relations (Listes)
    private List<HistoriqueActivite> historiqueActivite;
    private List<Seance> listeSeances;
    private List<Performance> performances; 
    
    public Membre() {
        super(); // Cela nécessite que la classe Utilisateur ait aussi un constructeur vide !
        this.historiqueActivite = new ArrayList<>();
        this.listeSeances = new ArrayList<>();
        this.performances = new ArrayList<>();
    }

    // --- Constructeur 1 : CRÉATION (Sans ID) ---
    public Membre(String nom, String prenom, String dateNaissance, String email, 
                  String telephone, String adresse, String motDePasse,
                  TypeObjectif objectifSportif, TypePreference preferences) {

        
        super(nom, prenom, dateNaissance, email, telephone, adresse, motDePasse, "membre");
        
        this.objectifSportif = objectifSportif;
        this.preferences = preferences;
        
        // Initialisation des listes
        this.historiqueActivite = new ArrayList<>();
        this.listeSeances = new ArrayList<>();
        this.performances = new ArrayList<>();
    }

    // --- Constructeur 2 : RÉCUPÉRATION BDD (Avec ID) ---
    public Membre(int id, String nom, String prenom, String dateNaissance, String email, 
                  String telephone, String adresse, 
                  TypeObjectif objectifSportif, TypePreference preferences) {
        
        super(id, nom, prenom, dateNaissance, email, telephone, adresse);
        
        this.objectifSportif = objectifSportif;
        this.preferences = preferences;
        
        this.historiqueActivite = new ArrayList<>();
        this.listeSeances = new ArrayList<>();
        this.performances = new ArrayList<>();
    }

    // --- Méthodes Métier ---

    public List<Seance> consulterSeances() {
        return this.listeSeances;
    }

    public void reserverSeance(Seance seance) {
        if (seance != null) {
            this.listeSeances.add(seance);
        }
    }

    public void annulerReservation(Reservation reservation) {
        if (reservation != null && reservation.getSeance() != null) {
            this.listeSeances.remove(reservation.getSeance());
        }
    }

    public void consulterProgression() {
        System.out.println("Affichage de la progression pour " + this.getNom());
        // Logique d'affichage ici
    }

    // --- Getters et Setters ---

    public TypeObjectif getObjectifSportif() { return objectifSportif; }
    public void setObjectifSportif(TypeObjectif objectifSportif) { this.objectifSportif = objectifSportif; }

    public TypePreference getPreferences() { return preferences; }
    public void setPreferences(TypePreference preferences) { this.preferences = preferences; }

    public List<HistoriqueActivite> getHistoriqueActivite() { return historiqueActivite; }
    public void setHistoriqueActivite(List<HistoriqueActivite> historiqueActivite) { this.historiqueActivite = historiqueActivite; }

    public List<Seance> getListeSeances() { return listeSeances; }
    public void setListeSeances(List<Seance> listeSeances) { this.listeSeances = listeSeances; }

    public List<Performance> getPerformances() { return performances; }
    public void setPerformances(List<Performance> performances) { this.performances = performances; }

    public Double getNoteSatisfaction() {
        return noteSatisfaction;
    }
    
    public void setNoteSatisfaction(Double noteSatisfaction) {
        // Validation : note entre 1 et 5
        if (noteSatisfaction != null && (noteSatisfaction < 1 || noteSatisfaction > 5)) {
            throw new IllegalArgumentException("La note doit être entre 1 et 5");
        }
        this.noteSatisfaction = noteSatisfaction;
        this.dateEvaluation = LocalDate.now(); // Mettre à jour la date automatiquement
    }
    
    public LocalDate getDateEvaluation() {
        return dateEvaluation;
    }
    
    public void setDateEvaluation(LocalDate dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }
    
}