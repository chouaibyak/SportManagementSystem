package com.sport.model;

import java.time.LocalDate;
public class HistoriqueActivite {

    // --- Attributs ---
    private int id; // Ajouté pour la BDD (Clé primaire)
    
    private Membre membre; // Relation vers le Membre (Clé étrangère en BDD)
    private TypeSeance typeSeance; // Enum
    private int duree; // En minutes
    private LocalDate date;
    private String notes;

    // --- Constructeur 1 : CRÉATION (Sans ID) ---
    public HistoriqueActivite(Membre membre, TypeSeance typeSeance, int duree, LocalDate date, String notes) {
        this.id = 0;
        this.membre = membre;
        this.typeSeance = typeSeance;
        this.duree = duree;
        this.date = date;
        this.notes = notes;
    }

    // --- Constructeur 2 : RÉCUPÉRATION BDD (Avec ID) ---
    public HistoriqueActivite(int id, Membre membre, TypeSeance typeSeance, int duree, LocalDate date, String notes) {
        this.id = id;
        this.membre = membre;
        this.typeSeance = typeSeance;
        this.duree = duree;
        this.date = date;
        this.notes = notes;
    }

    // --- Méthodes Métier (selon UML) ---

    /**
     * Ajoute une note à la suite des notes existantes.
     */
    public void ajouterNote(String note) {
        if (note != null && !note.isEmpty()) {
            if (this.notes == null || this.notes.isEmpty()) {
                this.notes = note;
            } else {
                // On ajoute un saut de ligne ou un séparateur avant la nouvelle note
                this.notes += "\n- " + note;
            }
        }
    }

    /**
     * Retourne les notes actuelles.
     */
    public String consulterNote() {
        return this.notes;
    }

    // --- Getters et Setters ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

    public TypeSeance getTypeSeance() { return typeSeance; }
    public void setTypeSeance(TypeSeance typeSeance) { this.typeSeance = typeSeance; }

    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}