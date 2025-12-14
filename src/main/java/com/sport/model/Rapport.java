package com.sport.model;

public class Rapport {
    private int id;
    private String type;
    private String dateDebut;
    private String dateFin;
    private String donnees;

    // Constructeur
    public Rapport(int id, String type, String dateDebut, String dateFin, String donnees) {
        this.id = id;
        this.type = type;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.donnees = donnees;
    }

    public Rapport() {
    }

    public Rapport(String type, String dateDebut, String dateFin) {
        this.type = type;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;    }

    // Getters
    public int getId() {
         return id; 
    }

    public String getType() {
        return type;
    }
    
    public String getDateDebut() {
        return dateDebut;
    }
    
    public String getDateFin() {
        return dateFin;
    }
    
    public String getDonnees() {
        return donnees;
    }
    
    // Setters
    public void setId(int id) { 
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }
    
    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }
    
    public void setDonnees(String donnees) {
        this.donnees = donnees;
    }
}