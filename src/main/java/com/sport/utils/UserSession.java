package com.sport.utils;

import com.sport.model.Utilisateur;

public class UserSession {

    // Instance unique (Singleton)
    private static UserSession instance;

    // L'utilisateur actuellement connecté
    private Utilisateur utilisateurConnecte;

    // Constructeur privé (personne ne peut faire "new UserSession")
    private UserSession() {
    }

    // Méthode pour récupérer l'instance unique
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // --- Getters et Setters ---

    public Utilisateur getUtilisateur() {
        return utilisateurConnecte;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
    }

    // Pour se déconnecter
    public void cleanUserSession() {
        utilisateurConnecte = null;
    }
}