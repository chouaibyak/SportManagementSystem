package com.sport.model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Performance {

    // --- Attributs (selon UML + ID pour BDD) ---
    private int id;
    
    private Membre membre;
    private double poids; // En kg
    private double imc;
    private double tourTaille; // En cm
    private double force; // Ex: charge maximale au développé couché
    private double endurance; // Ex: temps au 10km ou test Cooper
    
    // Attribut conseillé (non visible sur UML mais indispensable pour suivre l'évolution)
    private LocalDate dateMesure; 

    // Pour stocker les mesures dynamiques (via ajouterMesure)
    private Map<String, Double> mesuresSupplementaires;

    // --- Constructeur 1 : CRÉATION (Sans ID) ---
    public Performance(Membre membre, double poids, double tourTaille, double force, double endurance, LocalDate dateMesure) {
        this.id = 0;
        this.membre = membre;
        this.poids = poids;
        this.tourTaille = tourTaille;
        this.force = force;
        this.endurance = endurance;
        this.dateMesure = dateMesure;
        
        // Initialisation de la Map et calcul automatique
        this.mesuresSupplementaires = new HashMap<>();
        this.imc = 0.0; // Sera calculé via la méthode
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

    // --- Méthodes Métier (selon UML) ---

    /**
     * Calcule l'IMC.
     * Note : Pour calculer l'IMC, il faut la taille (m).
     * Comme 'taille' n'est pas dans les attributs de Performance, on suppose qu'on la passe en paramètre
     * ou qu'elle devrait être dans la classe Membre.
     */
    public double calculerIMC(double tailleEnMetres) {
        if (tailleEnMetres > 0) {
            this.imc = this.poids / (tailleEnMetres * tailleEnMetres);
            // On arrondit à 2 chiffres après la virgule
            this.imc = Math.round(this.imc * 100.0) / 100.0;
        }
        return this.imc;
    }

    /**
     * Calcule la progression par rapport à une performance précédente.
     * Ici, on peut choisir de comparer le Poids ou la Force par exemple.
     * Retourne la différence (négatif = perte, positif = gain).
     */
    public double calculerProgression(Performance prevPerformance) {
        if (prevPerformance == null) {
            return 0.0;
        }
        // Exemple : on compare le poids (pour une perte de poids, un résultat négatif est bien)
        return this.poids - prevPerformance.getPoids();
    }

    /**
     * Ajoute une mesure spécifique (ex: "Tour de bras", 35.5).
     */
    public void ajouterMesure(String nomMesure, double valeur) {
        if (nomMesure != null && !nomMesure.isEmpty()) {
            this.mesuresSupplementaires.put(nomMesure, valeur);
        }
    }

    /**
     * Retourne la liste des mesures supplémentaires.
     */
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
    
    // Getter/Setter pour la Map (optionnel, souvent géré juste par ajouterMesure)
    public void setMesuresSupplementaires(Map<String, Double> mesures) {
        this.mesuresSupplementaires = mesures;
    }
}