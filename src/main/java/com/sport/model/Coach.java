package com.sport.model;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Coach extends Utilisateur {

    private Set<String> specialites = new HashSet<>();  // Utilisation d'un Set pour éviter les doublons
    private Set<Seance> seances = new HashSet<>();     // Utilisation d'un Set pour éviter les doublons
    private List<Performance> performancesSuivies = new ArrayList<>();
    private String motDePasse;  // Ajout du mot de passe

    // Constructeur
    public Coach() {
        super(id, nom, prenom, dateNaissance, email, telephone, adresse);  // Appel du constructeur de la classe parente (Utilisateur)
        this.motDePasse = motDePasse;  // Initialisation du mot de passe
    }

    // Méthode pour ajouter une spécialité
    public boolean ajouterSpecialite(String specialite) {
        return specialites.add(specialite);  // Le Set retourne true si l'ajout a réussi (évite les doublons)
    }

    // Accesseurs pour les séances
    public Set<Seance> getSeances() {
        return seances;
    }

    // Accesseurs pour les spécialités
    public Set<String> getSpecialites() {
        return specialites;
    }

    // Ajout de la vérification de doublon pour affecter une séance
    public boolean affecterSeance(Seance seance) {
        if (seance == null) return false;
        for (Seance s : seances) {
            if (s.getDateHeure().equals(seance.getDateHeure())) {
                System.out.println("La séance est déjà planifiée pour ce coach.");
                return false;
            }
        }
        return seances.add(seance);
    }

    public void noterPerformance(Membre membre, Performance performance) {
        if (membre != null && performance != null) {
            // Vérifier que la performance n'est pas déjà suivie
            if (!performancesSuivies.contains(performance)) {
                performancesSuivies.add(performance);
            } else {
                System.out.println("La performance a déjà été notée pour ce membre.");
            }
        }
    }

    // Getter pour le mot de passe
    public String getMotDePasse() {
        return motDePasse;
    }

    // Setter pour le mot de passe
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
}

   
    

   
