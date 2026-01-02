package com.sport.model;

import java.util.Calendar;
import java.util.Date;

public class Abonnement {

    private int id;
    private Membre membre; 
    private TypeAbonnement typeAbonnement;
    private StatutAbonnement statutAbonnement;
    private boolean autorenouvellement;
    private double montant;
    private Date dateDebut;
    private Date dateFin;


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

   
   
    private String membreFullname ;

    public String getMembreFullname() {
        return membreFullname;
    }

    public void setMembreFullname(String membreFullname) {
        this.membreFullname = membreFullname;
    }


    public TypeAbonnement getTypeAbonnement() { return typeAbonnement; }
    public void setTypeAbonnement(TypeAbonnement typeAbonnement) { this.typeAbonnement = typeAbonnement; }

    public StatutAbonnement getStatutAbonnement() { return statutAbonnement; }
    public void setStatutAbonnement(StatutAbonnement statutAbonnement) { this.statutAbonnement = statutAbonnement; }

    public boolean isAutorenouvellement() { return autorenouvellement; }
    public void setAutorenouvellement(boolean autorenouvellement) { this.autorenouvellement = autorenouvellement; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public Date getDateDebut() { return dateDebut; }
    public void setDateDebut(Date dateDebut) { this.dateDebut = dateDebut; }

    public Date getDateFin() { return dateFin; }
    public void setDateFin(Date dateFin) { this.dateFin = dateFin; }
    
    // Méthodes Métier
    public void activerAbonnement() {
        this.statutAbonnement = StatutAbonnement.ACTIF;
    }

    public void resilierAbonnement() {
        this.statutAbonnement = StatutAbonnement.RESILIE;
    }

   public Date calculerProchaineDateFin() {
        if (typeAbonnement == null || dateDebut == null) return dateFin;

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateDebut);

        switch (typeAbonnement) {
            case MENSUEL -> cal.add(Calendar.MONTH, 1);
            case TRIMESTRIEL -> cal.add(Calendar.MONTH, 3);
            case ANNUEL -> cal.add(Calendar.YEAR, 1);
        }

        return cal.getTime();
    }

    public void calculerEtSetDateFin() {
        this.dateFin = calculerProchaineDateFin();
    }


}