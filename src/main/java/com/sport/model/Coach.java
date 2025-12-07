package com.sport.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Coach extends Utilisateur {

    private Set<String> specialites = new HashSet<>();  // Utilisation d'un Set pour éviter les doublons
    private Set<Seance> seances = new HashSet<>();     // Utilisation d'un Set pour éviter les doublons
    private List<Performance> performancesSuivies = new ArrayList<>();
    private String motDePasse; 

    // Constructeur
    public Coach(int id, String nom, String prenom, String dateNaissance, String email, String telephone, String adresse, String motDePasse) {
        super(id, nom, prenom, dateNaissance, email, telephone, adresse); 
        this.motDePasse = motDePasse; 
    }

    // --- Méthodes Métier ---

    // Méthode pour ajouter une spécialité
    public boolean ajouterSpecialite(String specialite) {
        return specialites.add(specialite);  // Retourne true si ajout réussi
    }

    // Ajout de la vérification de doublon pour affecter une séance
    public boolean affecterSeance(Seance seance) {
        if (seance == null) return false;
        
        // Vérifier si le coach a déjà une séance à cette heure-là
        for (Seance s : seances) {
            // Note : Assurez-vous que Seance a bien une méthode getDateHeure()
            if (s.getDateHeure() != null && s.getDateHeure().equals(seance.getDateHeure())) {
                System.out.println("La séance est déjà planifiée pour ce coach à cette heure.");
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

    // --- Getters et Setters ---

    public Set<Seance> getSeances() {
        return seances;
    }

    public void setSeances(Set<Seance> seances) {
        this.seances = seances;
    }

    public Set<String> getSpecialites() {
        return specialites;
    }

    public void setSpecialites(Set<String> specialites) {
        this.specialites = specialites;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
    
    public List<Performance> getPerformancesSuivies() {
        return performancesSuivies;
    }
}