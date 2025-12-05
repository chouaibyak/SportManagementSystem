package com.sport.model;

import java.util.Calendar;
import java.util.Date;

public class Abonnement {

    private int id;
    private TypeAbonnement typeAbonnement;
    private StatutAbonnement statutAbonnement;
    private boolean autorenouvellement;

    public Abonnement() {}

    public Abonnement(TypeAbonnement typeAbonnement,
                      StatutAbonnement statutAbonnement,
                      boolean autorenouvellement) {
        this.typeAbonnement = typeAbonnement;
        this.statutAbonnement = statutAbonnement;
        this.autorenouvellement = autorenouvellement;
    }

    // Getters / Setters
    public TypeAbonnement getTypeAbonnement() {
        return typeAbonnement;
    }

    public void setTypeAbonnement(TypeAbonnement typeAbonnement) {
        this.typeAbonnement = typeAbonnement;
    }

    public StatutAbonnement getStatutAbonnement() {
        return statutAbonnement;
    }

    public void setStatutAbonnement(StatutAbonnement statutAbonnement) {
        this.statutAbonnement = statutAbonnement;
    }

    public boolean isAutorenouvellement() {
        return autorenouvellement;
    }

    public void setAutorenouvellement(boolean autorenouvellement) {
        this.autorenouvellement = autorenouvellement;
    }

    // Méthode : Calcul de la prochaine date de fin
    public Date calculerProchaineDateFin() {
        Calendar cal = Calendar.getInstance();

        switch (typeAbonnement) {
            case MENSUEL:
                cal.add(Calendar.MONTH, 1);
                break;

            case TRIMESTRIEL:
                cal.add(Calendar.MONTH, 3);
                break;

            case ANNUEL:
                cal.add(Calendar.YEAR, 1);
                break;
        }

        return cal.getTime();
    }

    // Méthodes du diagramme UML
    public void activerAbonnement() {
        this.statutAbonnement = StatutAbonnement.ACTIF;
    }

    public void suspendreAbonnement() {
        this.statutAbonnement = StatutAbonnement.SUSPENDU;
    }

    public void resilierAbonnement() {
        this.statutAbonnement = StatutAbonnement.RESILIE;
    }
}
