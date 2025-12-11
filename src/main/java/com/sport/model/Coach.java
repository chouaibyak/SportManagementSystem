package com.sport.model;

import java.util.ArrayList;
import java.util.List;

public class Coach extends Utilisateur {

    private List<String> specialites = new ArrayList<>();  // Utilisation d'un Set pour éviter les doublons
    private List<Seance> seances = new ArrayList<>();     // Utilisation d'un Set pour éviter les doublons
    private List<Performance> performancesSuivies = new ArrayList<>();
   

    // Constructeur
    public Coach(int id, String nom, String prenom, String dateNaissance, String email, String telephone, String adresse, String motDePasse) {
        super(id, nom, prenom, dateNaissance, email, telephone, adresse);  // Appel du constructeur de la classe parente (Utilisateur)
    
    }

     public Coach() {
    }
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


    // Getter pour le mot de passe
    public String getMotDePasse() {
        return motDePasse;
    // --- Getters et Setters ---

    public List<Seance> getSeances() {
        return seances;
    }

    public void setSeances(List<Seance> seances) {
        this.seances = seances;
    }

    public List<String> getSpecialites() {
        return specialites;
    }

    public void setSpecialites(List<String> specialites) {
        this.specialites = specialites;
    }}


   
    

   
