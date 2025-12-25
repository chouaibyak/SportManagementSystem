package com.sport.model;

import java.time.LocalDateTime;

public class SeanceIndividuelle extends Seance {

    private Membre membre;
    private double tarif;
    private String notesCoach;

    // Constructeur
    public SeanceIndividuelle(int id, String nom, int capaciteMax, Salle salle, LocalDateTime dateHeure,
                              Coach entraineur, TypeCours type, int duree,
                              Membre membre, double tarif, String notesCoach) {
        super(id, nom, capaciteMax, salle, dateHeure, entraineur, type, duree);
        this.membre = membre;
        this.tarif = tarif;
        this.notesCoach = notesCoach;
        this.typeSeance = TypeSeance.INDIVIDUELLE;
    }

        public SeanceIndividuelle() {
        super(); // constructeur par défaut
        this.typeSeance = TypeSeance.INDIVIDUELLE;
    }


    // Getters et setters
    public Membre getMembre() {
        return membre;
    }

    public void setMembre(Membre membre) {
        this.membre = membre;
    }

    public double getTarif() {
        return tarif;
    }

    public void setTarif(double tarif) {
        this.tarif = tarif;
    }

    public String getNotesCoach() {
        return notesCoach;
    }

    public void setNotesCoach(String notesCoach) {
        this.notesCoach = notesCoach;
    }

}

