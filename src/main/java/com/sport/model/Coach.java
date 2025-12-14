package com.sport.model;

import java.util.ArrayList;
import java.util.List;

public class Coach extends Utilisateur {

    private String motDePasse; // Password specific to Coach (or move to Utilisateur)
    
    // Lists
    private List<String> specialites;
    private List<Seance> seances; 
    private List<Performance> performancesSuivies;

    // --- Constructor 1: Creation (No ID) ---
    public Coach(String nom, String prenom, String dateNaissance, String email, 
                 String telephone, String adresse, String motDePasse) {
        super(nom, prenom, dateNaissance, email, telephone, adresse);
        this.motDePasse = motDePasse;
        this.specialites = new ArrayList<>();
        this.seances = new ArrayList<>();
        this.performancesSuivies = new ArrayList<>();
    }

    // --- Constructor 2: From DB (With ID) ---
    public Coach(int id, String nom, String prenom, String dateNaissance, String email, 
                 String telephone, String adresse, String motDePasse) {
        super(id, nom, prenom, dateNaissance, email, telephone, adresse);
        this.motDePasse = motDePasse;
        this.specialites = new ArrayList<>();
        this.seances = new ArrayList<>();
        this.performancesSuivies = new ArrayList<>();
    }

    public Coach() {
        super();
        this.specialites = new ArrayList<>();
        this.seances = new ArrayList<>();
        this.performancesSuivies = new ArrayList<>();
    }

    // --- Business Logic ---

    public boolean ajouterSpecialite(String specialite) {
        if (specialite != null && !specialites.contains(specialite)) {
            return specialites.add(specialite);
        }
        return false;
    }

    public boolean affecterSeance(Seance seance) {
        if (seance == null) return false;
        
        // Check for time conflict
        for (Seance s : seances) {
            if (s.getDateHeure() != null && s.getDateHeure().equals(seance.getDateHeure())) {
                System.out.println("Erreur: Le coach a déjà une séance à cette heure.");
                return false;
            }
        }
        return seances.add(seance);
    }

    public void noterPerformance(Membre membre, Performance performance) {
        if (membre != null && performance != null) {
            if (!performancesSuivies.contains(performance)) {
                performancesSuivies.add(performance);
                // Usually, you would also add this performance to the Member's list here
                membre.getPerformances().add(performance);
            }
        }
    }

    // --- Getters and Setters ---

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public List<String> getSpecialites() { return specialites; }
    public void setSpecialites(List<String> specialites) { this.specialites = specialites; }

    public List<Seance> getSeances() { return seances; }
    public void setSeances(List<Seance> seances) { this.seances = seances; }

    public List<Performance> getPerformancesSuivies() { return performancesSuivies; }
    public void setPerformancesSuivies(List<Performance> performancesSuivies) { this.performancesSuivies = performancesSuivies; }
}