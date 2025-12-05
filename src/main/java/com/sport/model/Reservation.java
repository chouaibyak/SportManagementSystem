package com.sport.model;

import java.util.Date;

public class Reservation {

    private Membre membre;
    private Seance seance;
    private StatutReservation statut;
    private Date dateReservation;

    // Constructeur par défaut
    public Reservation() {}

    // Constructeur avec paramètres
    public Reservation(Membre membre, Seance seance, StatutReservation statut, Date dateReservation) {
        this.membre = membre;
        this.seance = seance;
        this.statut = statut;
        this.dateReservation = dateReservation;
    }

    // Getters et setters
    public Membre getMembre() {
        return membre;
    }

    public void setMembre(Membre membre) {
        this.membre = membre;
    }

    public Seance getSeance() {
        return seance;
    }

    public void setSeance(Seance seance) {
        this.seance = seance;
    }

    public StatutReservation getStatut() {
        return statut;
    }

    public void setStatut(StatutReservation statut) {
        this.statut = statut;
    }

    public Date getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(Date dateReservation) {
        this.dateReservation = dateReservation;
    }

    // Méthode du diagramme (simple dans model)
    public void marquerPresence() {
        this.statut = StatutReservation.PRESENT;
    }
}
