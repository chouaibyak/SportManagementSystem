package com.sport.controller.member;

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

public class MemberLayoutController {

    @FXML private StackPane contentArea;
    
    // Références aux boutons pour gérer le style "Actif"
    @FXML private Button btnDashboard;
    @FXML private Button btnSeances;
    @FXML private Button btnPerformance;
    @FXML private Button btnProfil;

    @FXML
    public void initialize() {
        // Au démarrage, on charge le dashboard et on active son bouton
        afficherDashboard(null);
    }

    // --- NAVIGATION ---

    @FXML
    private void afficherDashboard(ActionEvent event) {
        chargerVue("/fxml/member/member_dashboard.fxml");
        setButtonActive(btnDashboard);
    }

    @FXML
    private void afficherSeances(ActionEvent event) {
        // Crée ce fichier fxml plus tard
        chargerVue("/fxml/member/member_seances.fxml"); 
        setButtonActive(btnSeances);
    }

    @FXML
    private void afficherPerformances(ActionEvent event) {
        // Crée ce fichier fxml plus tard
        chargerVue("/fxml/member/member_performances.fxml"); 
        setButtonActive(btnPerformance);
    }

    @FXML
    private void afficherProfil(ActionEvent event) {
        // Crée ce fichier fxml plus tard
        chargerVue("/fxml/member/member_profile.fxml"); 
        setButtonActive(btnProfil);
    }

    // --- LOGIQUE DE CHANGEMENT DE VUE ---

    private void chargerVue(String fxmlPath) {
        try {
            // Vérif pour éviter le crash si tu n'as pas encore créé les autres fichiers FXML
            if (getClass().getResource(fxmlPath) == null) {
                System.err.println("ERREUR : Fichier introuvable -> " + fxmlPath);
                return;
            }
            
            Parent vue = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(vue);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Change le style du bouton cliqué pour montrer qu'il est actif.
     */
    private void setButtonActive(Button activeButton) {
        // 1. On remet tous les boutons en style normal
        resetButtonStyles();

        // 2. On applique le style actif au bouton cliqué (si ce n'est pas null)
        if (activeButton != null) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    private void resetButtonStyles() {
        // Retire la classe 'nav-button-active' de tous les boutons
        btnDashboard.getStyleClass().remove("nav-button-active");
        btnSeances.getStyleClass().remove("nav-button-active");
        btnPerformance.getStyleClass().remove("nav-button-active");
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
            
            // On remet la taille "Login" et on bloque le redimensionnement
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.setResizable(false);
            
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
    }
}