package com.sport.model;

import java.util.Objects;

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
                       String email, String telephone, String adresse, String motdePasse) {
        this.id = 0;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
        this.motDePasse = motdePasse;
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

    // ============================================================
    // ⚠️ AJOUT OBLIGATOIRE POUR LE BON FONCTIONNEMENT DES LISTES ⚠️
    // ============================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        // Deux utilisateurs sont les mêmes si ils ont le même ID
        return id == that.id;
    }

    @Override
    public int hashCode() {
        // Génère un code unique basé sur l'ID
        return Objects.hash(id);
    }

    // ============================================================
    // ----- Getters & Setters -----
    // ============================================================

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    @Override
    public String toString() {
        return nom + " " + prenom + " (" + email + ")";
    }
}