package com.sport.controller.membre;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainPane;

    @FXML
    public void initialize() {
        // Charger le dashboard par défaut au lancement
        afficherDashboard();
    }

    @FXML
    private void afficherDashboard() {
        chargerVue("/fxml/member/dashboard.fxml");
    }

    @FXML
    private void afficherMembres() {
        chargerVue("/fxml/member/membres_list.fxml");
    }

    // Méthode utilitaire pour changer le contenu central
    private void chargerVue(String cheminFxml) {
        try {
            Parent vue = FXMLLoader.load(getClass().getResource(cheminFxml));
            mainPane.setCenter(vue);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la vue : " + cheminFxml);
        }
    }
}