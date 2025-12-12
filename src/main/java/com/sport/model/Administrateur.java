package com.sport.model;

public class Administrateur extends Utilisateur 
{
    public Administrateur() {
        super(); // Appelle le constructeur vide de Utilisateur
    }

    public Administrateur(int id, String nom, String prenom, String dateNaissance,
        String email, String telephone, String adresse) 
        {
            super(id, nom, prenom, dateNaissance, email, telephone, adresse);
        }
}
