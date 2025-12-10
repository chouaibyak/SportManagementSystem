package com.sport.model;

import java.util.Date;

public class Equipement {

    // --- Attributs ---
    private int id;
    private String nom;
    private TypeEquipement type; // Enum
    private EtatEquipement etat; // Enum
    private Date dateAchat;

    // --- Constructeur ---
    public Equipement() {
        this.id = 0;
        this.nom = nom;
        this.type = type;
        this.etat = etat;
        this.dateAchat = dateAchat;
    }

    // --- Getters et Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public TypeEquipement getType() { return type; }
    public void setType(TypeEquipement type) { this.type = type; }

    public EtatEquipement getEtat() { return etat; }
    public void setEtat(EtatEquipement etat) { this.etat = etat; }

    public Date getDateAchat() { return dateAchat; }
    public void setDateAchat(Date dateAchat) { this.dateAchat = dateAchat; }

    // --- Méthodes Métier (selon UML) ---
    
    public void planifierMaintenance() {
        // Logique pour planifier une maintenance
        System.out.println("Maintenance planifiée pour l'équipement " + this.nom);
    }

    public void marquerDisponible() {
        this.etat = EtatEquipement.DISPONIBLE;
    }

    public void marquerHorsService() {
        this.etat = EtatEquipement.HORS_SERVICE;
    }
}
