package com.sport.model;

import java.util.ArrayList;
import java.util.List;

public class Membre extends Utilisateur {

    // --- Attributs spécifiques au Membre (selon UML) ---
    private TypeObjectif objectifSportif;
    private TypePreference preferences;
    
    // Relations (Listes)
    private List<HistoriqueActivite> historiqueActivite;
    private List<Seance> listeSeances;

    // --- Constructeur 1 : CRÉATION (Sans ID) ---
    public Membre(String nom, String prenom, String dateNaissance, String email, 
                  String telephone, String adresse, 
                  TypeObjectif objectifSportif, TypePreference preferences) {
        
        // On envoie les infos de base au parent (Utilisateur)
        super(nom, prenom, dateNaissance, email, telephone, adresse);
        
        // On remplit les infos spécifiques au Membre
        this.objectifSportif = objectifSportif;
        this.preferences = preferences;
        
        // Initialisation des listes pour qu'elles ne soient pas null
        this.historiqueActivite = new ArrayList<>();
        this.listeSeances = new ArrayList<>();
    }

    // --- Constructeur 2 : RÉCUPÉRATION BDD (Avec ID) ---
    public Membre(int id, String nom, String prenom, String dateNaissance, String email, 
                  String telephone, String adresse, 
                  TypeObjectif objectifSportif, TypePreference preferences) {
        
        // On envoie l'ID et les infos au parent
        super(id, nom, prenom, dateNaissance, email, telephone, adresse);
        
        this.objectifSportif = objectifSportif;
        this.preferences = preferences;
        
        this.historiqueActivite = new ArrayList<>();
        this.listeSeances = new ArrayList<>();
    }

    // --- Méthodes Métier (selon UML) ---

    /**
     * Retourne la liste des séances prévues.
     */
    public List<Seance> consulterSeances() {
        return this.listeSeances;
    }

    /**
     * Ajoute une séance à la liste du membre.
     */
    public void reserverSeance(Seance seance) {
        if (seance != null) {
            this.listeSeances.add(seance);
            // Note: Dans une vraie appli, il faudrait aussi vérifier s'il reste de la place
            // et appeler seance.ajouterParticipant(this);
        }
    }

    /**
     * Annule une réservation.
     * Note: Le diagramme parle de "Reservation" en paramètre, mais la liste contient des "Seance".
     * Ici, on suppose qu'on annule via l'objet Reservation ou directement la séance.
     * Pour simplifier selon ta liste actuelle :
     */
    public void annulerReservation(Reservation reservation) {
        // Logique pour trouver la séance liée à la réservation et la retirer
        if (reservation != null && reservation.getSeance() != null) {
            this.listeSeances.remove(reservation.getSeance());
        }
    }

    /**
     * Consulte la progression (basé sur l'historique ou les performances).
     */
    public void consulterProgression() {
        System.out.println("Affichage de la progression pour " + this.nom);
        for (HistoriqueActivite activite : historiqueActivite) {
            // Supposons que HistoriqueActivite a une méthode toString ou getDetails
            System.out.println("- " + activite.toString());
        }
    }

    // --- Getters et Setters spécifiques ---

    public TypeObjectif getObjectifSportif() { return objectifSportif; }
    public void setObjectifSportif(TypeObjectif objectifSportif) { this.objectifSportif = objectifSportif; }

    public TypePreference getPreferences() { return preferences; }
    public void setPreferences(TypePreference preferences) { this.preferences = preferences; }

    public List<HistoriqueActivite> getHistoriqueActivite() { return historiqueActivite; }
    public void setHistoriqueActivite(List<HistoriqueActivite> historiqueActivite) { this.historiqueActivite = historiqueActivite; }

    public List<Seance> getListeSeances() { return listeSeances; }
    public void setListeSeances(List<Seance> listeSeances) { this.listeSeances = listeSeances; }
}