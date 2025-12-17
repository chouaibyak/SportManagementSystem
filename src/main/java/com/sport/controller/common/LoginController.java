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
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
        String password = passwordField.getText();

        // 1. Appel au service d'authentification
        Utilisateur user = authService.login(email, password);

        if (user != null) {
            // 2. STOCKER L'UTILISATEUR DANS LA SESSION (Très important !)
            UserSession.getInstance().setUtilisateur(user);
            System.out.println("Connexion réussie : " + user.getNom());

            // 3. Redirection selon le rôle
            if (user instanceof Membre) {
                // ATTENTION : On charge le LAYOUT (avec le menu), pas juste le dashboard
                redirigerVers("/fxml/member/member_dashboard.fxml", "Espace Membre");
            } else if (user instanceof Coach) {
                // redirigerVers("/fxml/coach/coach_layout.fxml", "Espace Coach");
                errorLabel.setText("Interface Coach en construction...");
            } else {
                // Admin...
                errorLabel.setText("Interface Admin en construction...");
            }

        } else {
            errorLabel.setText("Email ou mot de passe incorrect.");
        }
    }

    @FXML
    private void allerVersInscription() {
        redirigerVers("/fxml/common/register.fxml", "Inscription");
    }

    // Méthode générique pour changer de page
    private void redirigerVers(String fxmlPath, String titre) {
        try {
            // 1. Vérifier si le fichier existe (Source fréquente d'erreurs)
            if (getClass().getResource(fxmlPath) == null) {
                System.err.println("ERREUR FATALE : Impossible de trouver " + fxmlPath);
                errorLabel.setText("Erreur interne : fichier vue introuvable.");
                return;
            }

            // 2. Chargement
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // 3. Changement de scène
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            
            stage.setScene(scene);
            stage.setTitle("Sport App - " + titre);
            
            // Si c'est l'espace membre, on met en plein écran
            if (titre.equals("Espace Membre")) {
                stage.setResizable(true);
                stage.setMaximized(true); 
            } else {
                // Pour login/register, on garde une petite taille
                stage.setResizable(false);
                stage.setWidth(600);
                stage.setHeight(450);
                stage.centerOnScreen();
            }

        } catch (IOException e) {
            e.printStackTrace(); // Regarde ta console pour voir l'erreur exacte
            errorLabel.setText("Erreur lors du chargement de la page.");
        }
    }
}