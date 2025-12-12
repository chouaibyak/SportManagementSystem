package com.sport.controller.common;

import com.sport.model.Utilisateur;
import com.sport.model.Membre;
import com.sport.model.Coach;
import com.sport.service.AuthService;
import com.sport.utils.UserSession; 
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String pwd = passwordField.getText();

        if (email.isEmpty() || pwd.isEmpty()) {
            errorLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        // Appel au service
        Utilisateur user = authService.login(email, pwd);

        if (user != null) {
            // Mise en session
            // Si tu n'as pas encore créé la classe UserSession, commente la ligne ci-dessous
            // UserSession.getInstance().setUtilisateur(user);
            
            System.out.println("✅ Connexion réussie : " + user.getNom());

            // Redirection
            if (user instanceof Membre) {
                rediriger("/fxml/member/main_layout.fxml", "Espace Membre");
            } else if (user instanceof Coach) {
                errorLabel.setText("Espace Coach en construction...");
            } else {
                errorLabel.setText("Bienvenue Admin.");
            }
        } else {
            errorLabel.setText("Email ou mot de passe incorrect.");
        }
    }

    @FXML
    private void allerVersInscription() {
        rediriger("/fxml/common/register.fxml", "Inscription");
    }

    private void rediriger(String fxmlPath, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            
            if (fxmlPath.contains("main_layout")) {
                stage.setScene(new Scene(root, 900, 600));
            } else {
                stage.setScene(new Scene(root));
            }
            stage.setTitle(titre);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur : Fichier introuvable " + fxmlPath);
        }
    }
}