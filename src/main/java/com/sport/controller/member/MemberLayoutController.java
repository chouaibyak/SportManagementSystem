package com.sport.controller.member;

import java.io.IOException;

import com.sport.utils.UserSession;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class MemberLayoutController {

    @FXML private StackPane contentArea;
    
    // Références aux boutons pour gérer le style "Actif"
    @FXML private Button btnDashboard;
    @FXML private Button btnPlanning;   
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

    // <--- 2. AJOUT DE LA MÉTHODE D'AFFICHAGE
    @FXML
    private void afficherPlanning(ActionEvent event) {
        // Assurez-vous que le fichier member_planning.fxml existe bien !
        chargerVue("/fxml/member/member_planning.fxml"); 
        setButtonActive(btnPlanning);
    }

    @FXML
    private void afficherSeances(ActionEvent event) {
        chargerVue("/fxml/member/member_seances.fxml"); 
        setButtonActive(btnSeances);
    }

    @FXML
    private void afficherPerformances(ActionEvent event) {
        chargerVue("/fxml/member/member_performances.fxml"); 
        setButtonActive(btnPerformance);
    }

    @FXML
    private void afficherProfil(ActionEvent event) {
        // Crée ce fichier fxml plus tard
        System.out.println("CLICK PROFIL OK");
        chargerVue("/fxml/member/member_profile.fxml"); 
        setButtonActive(btnProfil);
    }

    // --- LOGIQUE DE CHANGEMENT DE VUE ---

    private void chargerVue(String fxmlPath) {
       try {
        var url = getClass().getResource(fxmlPath);
        if (url == null) {
            System.err.println("ERREUR : Fichier introuvable -> " + fxmlPath);
            return;
        }

        FXMLLoader loader = new FXMLLoader(url);
        Parent vue = loader.load();   // si ça plante, on verras l'exception
        contentArea.getChildren().setAll(vue);

        System.out.println("Vue chargée: " + fxmlPath);

    } catch (Exception e) {
        System.err.println("ERREUR chargement vue: " + fxmlPath);
        e.printStackTrace();
    }
    }

    /**
     * Change le style du bouton cliqué pour montrer qu'il est actif.
     */
    private void setButtonActive(Button activeButton) {
        resetButtonStyles();

        if (activeButton != null) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    private void resetButtonStyles() {
        // Retire la classe 'nav-button-active' de tous les boutons
        if (btnDashboard != null) btnDashboard.getStyleClass().remove("nav-button-active");
        if (btnSeances != null) btnSeances.getStyleClass().remove("nav-button-active");
        if (btnPerformance != null) btnPerformance.getStyleClass().remove("nav-button-active");
        if (btnProfil != null) btnProfil.getStyleClass().remove("nav-button-active");
        
        // <--- 3. AJOUT DE LA REMISE A ZERO DU STYLE
        if (btnPlanning != null) btnPlanning.getStyleClass().remove("nav-button-active");
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