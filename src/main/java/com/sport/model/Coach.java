package com.sport.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Coach extends Utilisateur {

    private Set<String> specialites = new HashSet<>();  // Utilisation d'un Set pour éviter les doublons
    private Set<Seance> seances = new HashSet<>();     // Utilisation d'un Set pour éviter les doublons
    private List<Performance> performancesSuivies = new ArrayList<>();

    // Constructeur
    public Coach(int id, String nom, String prenom, String dateNaissance, String email, String telephone, String adresse) {
        super(id, nom, prenom, dateNaissance, email, telephone, adresse);  // Appel du constructeur de la classe parente (Utilisateur)
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

        public boolean affecterSeance(Seance seance) {
        if (seance == null) return false;
        return seances.add(seance);  // Ajoute la séance si elle n'existe pas déjà
    }

    // Méthode pour noter une performance
    public void noterPerformance(Membre membre, Performance performance) {
        if (membre != null && performance != null) {
            performancesSuivies.add(performance);
        }
    }

}
