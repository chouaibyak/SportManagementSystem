package com.sport.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Salle {
    private int id;
    private String nom;
    private int capacite;
    private TypeSalle type;
    private List<Equipement> listeEquipements;

    // Constructeur
    public Salle(String nom, int capacite, TypeSalle type) {
        this.id = 0;
        this.nom = nom;
        this.capacite = capacite;
        this.type = type;
        this.listeEquipements = new ArrayList<>();
    }

    // --- Méthodes Métier (du Diagramme UML) ---

    /**
     * Vérifie la disponibilité (Logique simplifiée pour le modèle).
     * En réalité, cela nécessiterait souvent l'accès au planning des réservations.
     */
    public boolean verifierDisponibilite(Date date) {
        // Logique fictive ici : on pourrait vérifier si la salle est en travaux
        // ou simplement retourner true par défaut si aucune contrainte interne n'existe.
        System.out.println("Vérification disponibilité salle " + this.nom + " pour le " + date);
        return true; 
    }

    public void ajouterEquipement(Equipement eq) {
        if (eq != null) {
            this.listeEquipements.add(eq);
            System.out.println("Équipement ajouté : " + eq.getNom());
        }
    }

    public void retirerEquipement(Equipement eq) {
        if (this.listeEquipements.contains(eq)) {
            this.listeEquipements.remove(eq);
            System.out.println("Équipement retiré : " + eq.getNom());
        } else {
            System.out.println("Cet équipement n'est pas dans la salle.");
        }
    }

    public List<Equipement> listerEquipements() {
        return this.listeEquipements;
    }

    // --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getCapacite() { return capacite; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public TypeSalle getType() { return type; }
    public void setType(TypeSalle type) { this.type = type; }

    @Override
    public String toString() {
        return "Salle : " + nom + " [" + type + "] - Capacité: " + capacite;
    }
}