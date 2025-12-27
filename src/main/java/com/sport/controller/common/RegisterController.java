package com.sport.controller.common;

import java.io.IOException;
import java.net.URL;

import com.sport.model.Coach;
import com.sport.model.Membre;
// AuthService n'est plus nécessaire ici !

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    
    @FXML private RadioButton radioMembre;
    @FXML private RadioButton radioCoach;
    // ToggleGroup n'a pas besoin d'être injecté si on utilise isSelected() sur les radios

    @FXML private Label lblMessage;

    // Suppression de AuthService ici (c'est l'étape 2 qui gère la BDD)

    @FXML
    private void handleInscription() {
        // 1. Récupération
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String email = txtEmail.getText().trim();
        String pwd = txtPassword.getText();

        // 2. Validation
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || pwd.isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs.");
            return;
        }
        
        // Petite validation email bonus
        if (!email.contains("@") || !email.contains(".")) {
            afficherErreur("Format d'email invalide.");
            return;
        }

        // 3. Aiguillage
        if (radioMembre.isSelected()) {
            allerVersEtape2Membre(nom, prenom, email, pwd);
        } else if (radioCoach.isSelected()) {
            allerVersEtape2Coach(nom, prenom, email, pwd);
        } else {
            afficherErreur("Veuillez choisir un type de compte.");
        }
    }

    private void allerVersEtape2Membre(String nom, String prenom, String email, String pwd) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/common/register_member.fxml"));
            Parent root = loader.load();

            // Création de l'objet temporaire
            Membre membreTemp = new Membre();
            membreTemp.setNom(nom);
            membreTemp.setPrenom(prenom);
            membreTemp.setEmail(email);
            membreTemp.setMotDePasse(pwd);

            // Passage de données
            RegisterMemberController controller = loader.getController();
            if (controller != null) {
                controller.initData(membreTemp);
            }

            changerScene(root);

        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur chargement étape 2 Membre.");
        }
    }

    private void allerVersEtape2Coach(String nom, String prenom, String email, String pwd) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/common/register_coach.fxml"));
            Parent root = loader.load();

            // Création de l'objet temporaire
            Coach coachTemp = new Coach();
            coachTemp.setNom(nom);
            coachTemp.setPrenom(prenom);
            coachTemp.setEmail(email);
            coachTemp.setMotDePasse(pwd);

            // Passage de données (C'est ici que ça connecte avec votre nouveau code)
            RegisterCoachController controller = loader.getController();
            if (controller != null) {
                controller.initData(coachTemp);
            }

            changerScene(root);

        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur chargement étape 2 Coach.");
        }
    }

    @FXML
    private void allerVersLogin() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/common/login.fxml");
            if (fxmlUrl == null) return;

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            changerScene(root);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Petite méthode utilitaire pour éviter de copier-coller les lignes de Stage
    private void changerScene(Parent root) {
        Stage stage = (Stage) txtNom.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.sizeToScene();
        stage.centerOnScreen();
    }
    
    private void afficherErreur(String msg) {
        lblMessage.setStyle("-fx-text-fill: #e74c3c;");
        lblMessage.setText(msg);
    }
}