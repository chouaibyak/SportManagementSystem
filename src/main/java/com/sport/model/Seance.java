package com.sport.model;
import java.time.LocalDateTime;

public class Seance {

    protected int id;
    protected String nom;
    protected int capaciteMax;
    protected LocalDateTime dateHeure;
    protected int duree; // En minutes
    protected TypeCours typeCours;
    protected TypeSeance typeSeance;
    protected Salle salle;
    protected Coach entraineur;

    // Constructeur par défaut
    public Seance() {
    }

    // Constructeur avec tous les paramètres
    public Seance(int id, String nom, int capaciteMax, Salle salle, LocalDateTime dateHeure, Coach entraineur, TypeCours typeCours, int duree) {
        this.id = id;
        this.nom = nom;
        this.capaciteMax = capaciteMax;
        this.salle = salle;
        this.dateHeure = dateHeure;
        this.entraineur = entraineur;
        this.typeCours = typeCours;
        this.duree = duree;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(int capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public Coach getEntraineur() {
        return entraineur;
    }

    public void setEntraineur(Coach entraineur) {
        this.entraineur = entraineur;
    }

    public TypeCours getTypeCours() {
        return typeCours;
    }

    public void setTypeCours(TypeCours typeCours) {
        this.typeCours = typeCours;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public void setTypeSeance(TypeSeance typeSeance){
        this.typeSeance = typeSeance;
    }

    public TypeSeance getTypeSeance(){
        return typeSeance;
    }

    
}