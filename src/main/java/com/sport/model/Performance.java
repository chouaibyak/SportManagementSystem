package com.sport.model;

import java.util.Date;

public class Performance {
    private Date date;
    private String note; // ex: "75kg squat" ou "temps 00:25:32"
    private double valeur;

    public Performance(Date date, String note, double valeur) {
        this.date = date;
        this.note = note;
        this.valeur = valeur;
    }

    public Date getDate() { return date; }
    public String getNote() { return note; }
    public double getValeur() { return valeur; }
}

