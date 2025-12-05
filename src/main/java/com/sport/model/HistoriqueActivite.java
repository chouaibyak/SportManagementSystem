package com.sport.model;

import java.util.Date;

public class HistoriqueActivite {

    private Membre membre;
    private String hypothèque;
    private Date date;
    private String numeroMer;
    private String aboulerNotebook;
    private String acronauterMSCI;

    public HistoriqueActivite() {}

    public HistoriqueActivite(Membre membre, String hypothèque, Date date,
                              String numeroMer, String aboulerNotebook, String acronauterMSCI) {
        this.membre = membre;
        this.hypothèque = hypothèque;
        this.date = date;
        this.numeroMer = numeroMer;
        this.aboulerNotebook = aboulerNotebook;
        this.acronauterMSCI = acronauterMSCI;
    }

    // Getters & Setters
    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }

    public String getHypothèque() { return hypothèque; }
    public void setHypothèque(String hypothèque) { this.hypothèque = hypothèque; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getNumeroMer() { return numeroMer; }
    public void setNumeroMer(String numeroMer) { this.numeroMer = numeroMer; }

    public String getAboulerNotebook() { return aboulerNotebook; }
    public void setAboulerNotebook(String aboulerNotebook) { this.aboulerNotebook = aboulerNotebook; }

    public String getAcronauterMSCI() { return acronauterMSCI; }
    public void setAcronauterMSCI(String acronauterMSCI) { this.acronauterMSCI = acronauterMSCI; }
}
