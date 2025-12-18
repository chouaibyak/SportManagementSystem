package com.sport.controller.coach;

import com.sport.model.Coach;
import com.sport.model.Utilisateur;
import com.sport.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class CoachDashboardController {

    @FXML private Label lblBienvenue;

    @FXML
    public void initialize() {
        // Récupérer le coach connecté depuis la session
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        
        if (user != null && user instanceof Coach) {
            lblBienvenue.setText("Bonjour, " + user.getPrenom());
        } else {
            lblBienvenue.setText("Coach inconnu");
        }
    }

    @FXML
    private void handleLogout() {
        // Vider la session
        UserSession.getInstance().cleanUserSession();
        
        // Retourner au login
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/common/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) lblBienvenue.getScene().getWindow();
            stage.setScene(new Scene(root));
            
            // Redimensionner proprement pour la fenêtre de login
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.setResizable(false);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}