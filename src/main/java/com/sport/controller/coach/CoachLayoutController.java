package com.sport.controller.coach;

import com.sport.model.Utilisateur;
import com.sport.utils.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class CoachLayoutController {

    @FXML private StackPane contentArea;

    // Boutons de navigation
    @FXML private Button btnDashboard;
    @FXML private Button btnSeances;
    @FXML private Button btnProfil;

    @FXML
   public void initialize() {
    afficherDashboard();
}
    // --- NAVIGATION ---

    @FXML
   private void afficherDashboard() {
    chargerVue("/fxml/coach/coach_dashboard.fxml");
    setButtonActive(btnDashboard);
}
    @FXML
    private void afficherSeances(ActionEvent event) {
        // Crée ce fichier fxml plus tard
        chargerVue("/fxml/coach/coach_seances.fxml");
        setButtonActive(btnSeances);
    }

    @FXML
    private void afficherProfil(ActionEvent event) {
        // Crée ce fichier fxml plus tard
        chargerVue("/fxml/coach/coach_profile.fxml");
        setButtonActive(btnProfil);
    }

    // --- LOGIQUE DE CHARGEMENT DE VUE ---
    private void chargerVue(String fxmlPath) {
        try {
            if (getClass().getResource(fxmlPath) == null) {
                System.err.println("Fichier introuvable : " + fxmlPath);
                return;
            }

            Parent vue = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(vue);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setButtonActive(Button activeButton) {
        // On remet tous les boutons à l’état normal
        resetButtonStyles();
        if (activeButton != null) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    private void resetButtonStyles() {
        btnDashboard.getStyleClass().remove("nav-button-active");
        btnSeances.getStyleClass().remove("nav-button-active");
        btnProfil.getStyleClass().remove("nav-button-active");
    }

    // --- DECONNEXION ---
    @FXML
    private void handleLogout() {
        UserSession.getInstance().cleanUserSession();

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/common/login.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root));

            stage.sizeToScene();
            stage.centerOnScreen();
            stage.setResizable(false);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
