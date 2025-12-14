package com.sport.model;

import java.util.Calendar;
import java.util.Date;

public class Abonnement {

    private int id;
    private Membre membre; 
    private TypeAbonnement typeAbonnement;
    private StatutAbonnement statutAbonnement;
    private boolean autorenouvellement;

    // Constructeur vide
    public Abonnement() {}

    // Constructeur pour création
    public Abonnement(Membre membre, TypeAbonnement typeAbonnement, StatutAbonnement statutAbonnement, boolean autorenouvellement) {
        this.membre = membre;
        this.typeAbonnement = typeAbonnement;
        this.statutAbonnement = statutAbonnement;
        this.autorenouvellement = autorenouvellement;
    }

    // Getters / Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

    public TypeAbonnement getTypeAbonnement() { return typeAbonnement; }
    public void setTypeAbonnement(TypeAbonnement typeAbonnement) { this.typeAbonnement = typeAbonnement; }

    public StatutAbonnement getStatutAbonnement() { return statutAbonnement; }
    public void setStatutAbonnement(StatutAbonnement statutAbonnement) { this.statutAbonnement = statutAbonnement; }

    public boolean isAutorenouvellement() { return autorenouvellement; }
    public void setAutorenouvellement(boolean autorenouvellement) { this.autorenouvellement = autorenouvellement; }

    // Méthodes Métier
    public void activerAbonnement() {
        this.statutAbonnement = StatutAbonnement.ACTIF;
    }

    public void resilierAbonnement() {
        this.statutAbonnement = StatutAbonnement.RESILIE;
    }

    public Date calculerProchaineDateFin() {
        if (typeAbonnement == null) return new Date();
        Calendar cal = Calendar.getInstance();
        switch (typeAbonnement) {
            case MENSUEL: cal.add(Calendar.MONTH, 1); break;
            case TRIMESTRIEL: cal.add(Calendar.MONTH, 3); break;
            case ANNUEL: cal.add(Calendar.YEAR, 1); break;
        }
        return cal.getTime();
    }
}