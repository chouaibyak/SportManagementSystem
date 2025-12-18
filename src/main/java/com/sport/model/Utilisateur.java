package com.sport.model;

public abstract class Utilisateur {

    // ----- Attributs -----
    protected int id;
    protected String nom;
    protected String prenom;
    protected String dateNaissance;
    protected String email;
    protected String motDePasse;
    protected String telephone;
    protected String adresse;

    // ----- Constructeurs -----

    public Utilisateur() {
    }

    public Utilisateur(String nom, String prenom, String dateNaissance,
                       String email, String telephone, String adresse) {
        this.id = 0;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
    }
     public Utilisateur(int id, String nom, String prenom, String dateNaissance,
                       String email, String telephone, String adresse) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
    }

    // ----- Getters & Setters -----

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }


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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
}

