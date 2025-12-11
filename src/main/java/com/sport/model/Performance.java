package com.sport.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Performance {

    // --- Attributs (selon UML + ID pour BDD) ---
    private int id;
    
    private Membre membre; // Obligatoire pour savoir à qui c'est
    private double poids; 
    private double imc;
    private double tourTaille; 
    private double force; 
    private double endurance; 
    
    private LocalDate dateMesure; 

    // Pour les mesures supplémentaires (ex: "Biceps", 35.0)
    private Map<String, Double> mesuresSupplementaires;

    public Performance() {
        this.mesuresSupplementaires = new HashMap<>(); // Important pour éviter les erreurs null
    }

    // --- Constructeur 1 : CRÉATION (Sans ID) ---
    public Performance(Membre membre, double poids, double tourTaille, double force, double endurance, LocalDate dateMesure) {
        this.id = 0;
        this.membre = membre;
        this.poids = poids;
        this.tourTaille = tourTaille;
        this.force = force;
        this.endurance = endurance;
        this.dateMesure = dateMesure;
        
        this.mesuresSupplementaires = new HashMap<>();
        this.imc = 0.0; 
    }

    // --- Constructeur 2 : RÉCUPÉRATION BDD (Avec ID) ---
    public Performance(int id, Membre membre, double poids, double imc, double tourTaille, 
                       double force, double endurance, LocalDate dateMesure) {
        this.id = id;
        this.membre = membre;
        this.poids = poids;
        this.imc = imc;
        this.tourTaille = tourTaille;
        this.force = force;
        this.endurance = endurance;
        this.dateMesure = dateMesure;
        
        this.mesuresSupplementaires = new HashMap<>();
    }

    // --- Méthodes Métier ---

    public double calculerIMC(double tailleEnMetres) {
        if (tailleEnMetres > 0) {
            this.imc = this.poids / (tailleEnMetres * tailleEnMetres);
            this.imc = Math.round(this.imc * 100.0) / 100.0;
        }
        return this.imc;
    }

    public double calculerProgression(Performance prevPerformance) {
        if (prevPerformance == null) {
            return 0.0;
        }
        return this.poids - prevPerformance.getPoids();
    }

    public void ajouterMesure(String nomMesure, double valeur) {
        if (nomMesure != null && !nomMesure.isEmpty()) {
            this.mesuresSupplementaires.put(nomMesure, valeur);
        }
    }

    public Map<String, Double> consulterMesures() {
        return this.mesuresSupplementaires;
    }

    // --- Getters et Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

    public double getPoids() { return poids; }
    public void setPoids(double poids) { this.poids = poids; }

    public double getImc() { return imc; }
    public void setImc(double imc) { this.imc = imc; }

    public double getTourTaille() { return tourTaille; }
    public void setTourTaille(double tourTaille) { this.tourTaille = tourTaille; }

    public double getForce() { return force; }
    public void setForce(double force) { this.force = force; }

    public double getEndurance() { return endurance; }
    public void setEndurance(double endurance) { this.endurance = endurance; }

    public LocalDate getDateMesure() { return dateMesure; }
    public void setDateMesure(LocalDate dateMesure) { this.dateMesure = dateMesure; }
    
    public void setMesuresSupplementaires(Map<String, Double> mesures) {
        this.mesuresSupplementaires = mesures;
    }
}