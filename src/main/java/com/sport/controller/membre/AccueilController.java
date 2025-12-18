package com.sport.controller.membre;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AccueilController {

    // 1. Lier les éléments du FXML (il faut que fx:id dans le FXML soit identique au nom ici)
    @FXML
    private Label messageBienvenue;
    
    @FXML
    private TextField champNom;

    // 2. Définir les actions (méthodes appelées par les boutons)
    @FXML
    protected void onButtonClick() {
        String nom = champNom.getText();
        if (nom.isEmpty()) {
            messageBienvenue.setText("Bonjour l'inconnu !");
        } else {
            messageBienvenue.setText("Bonjour " + nom + " ! Bienvenue au club.");
        }
    }

    // 3. Méthode d'initialisation (s'exécute tout seul au lancement de la fenêtre)
    @FXML
    public void initialize() {
        // Par exemple, charger des données ou configurer l'affichage initial
        messageBienvenue.setText("En attente de votre nom...");
    }
}