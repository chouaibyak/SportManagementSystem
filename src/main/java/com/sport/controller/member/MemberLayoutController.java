package com.sport.controller.member;

import com.sport.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

public class MemberLayoutController {

    @FXML private StackPane contentArea;

    @FXML
    public void initialize() {
        // Affiche le dashboard dès l'ouverture
        afficherDashboard();
    }

    @FXML
    private void afficherDashboard() {
        chargerVue("/fxml/member/member_dashboard.fxml");
    }

    // Méthode générique pour changer la vue au centre
    private void chargerVue(String fxmlPath) {
        try {
            Parent vue = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().setAll(vue);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur chargement vue : " + fxmlPath);
        }
    }

    @FXML
    private void handleLogout() {
        // 1. Vider la session
        UserSession.getInstance().cleanUserSession();
        
        // 2. Retour au Login
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/common/login.fxml"));
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 450));
            stage.centerOnScreen();
            stage.setResizable(false);
        } catch (IOException e) { e.printStackTrace(); }
    }
}