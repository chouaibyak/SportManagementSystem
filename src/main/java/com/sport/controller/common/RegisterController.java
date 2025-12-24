package com.sport.controller.common;

import java.io.IOException;
import java.net.URL;

import com.sport.model.Coach;
import com.sport.model.Membre;
import com.sport.service.AuthService;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class RegisterController {

    // --- Éléments de l'interface (FXML) ---
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    
    @FXML private RadioButton radioMembre;
    @FXML private RadioButton radioCoach;
    @FXML private ToggleGroup roleGroup; 

    @FXML private Label lblMessage;

    // --- Service ---
    private AuthService authService = new AuthService();

    /**
     * Méthode appelée quand on clique sur "S'inscrire".
     */
    @FXML
    private void handleInscription() {
        // 1. Récupération des valeurs
        String nom = txtNom.getText().trim();
        String prenom = txtPrenom.getText().trim();
        String email = txtEmail.getText().trim();
        String pwd = txtPassword.getText();

        // 2. Validation basique
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || pwd.isEmpty()) {
            afficherErreur("Veuillez remplir tous les champs.");
            return;
        }

        // 3. Aiguillage selon le rôle
        if (radioMembre.isSelected()) {
            // --- CAS MEMBRE : ON PASSE A L'ETAPE 2 ---
            allerVersEtape2Membre(nom, prenom, email, pwd);

        } else if (radioCoach.isSelected()) {
            // --- CAS COACH : INSCRIPTION DIRECTE ---
            Coach nouveau = new Coach();
            nouveau.setNom(nom);
            nouveau.setPrenom(prenom);
            nouveau.setEmail(email);
            
            boolean succes = authService.registerCoach(nouveau, pwd);
            
            if (succes) {
                lblMessage.setStyle("-fx-text-fill: green;");
                lblMessage.setText("Compte Coach créé avec succès !");
                viderChamps(); 
            } else {
                afficherErreur("Erreur : Email déjà utilisé ou problème technique.");
            }
        } else {
            afficherErreur("Veuillez choisir un type de compte.");
        }
    }

    private void allerVersEtape2Membre(String nom, String prenom, String email, String pwd) {
        try {
            // 1. Charger la vue suivante
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/common/register_member.fxml"));
            Parent root = loader.load();

            // 2. Créer l'objet temporaire
            Membre membreTemp = new Membre();
            membreTemp.setNom(nom);
            membreTemp.setPrenom(prenom);
            membreTemp.setEmail(email);
            membreTemp.setMotDePasse(pwd);

            // 3. Passer l'objet au contrôleur suivant
            RegisterMemberController controller = loader.getController();
            // Vérification de sécurité
            if (controller != null) {
                controller.initData(membreTemp);
            } else {
                System.err.println("Erreur : Le contrôleur RegisterMemberController est null.");
            }

            // 4. Afficher la nouvelle scène et REDIMENSIONNER LA FENETRE
            Stage stage = (Stage) txtNom.getScene().getWindow();
            
            // On applique la nouvelle scène
            stage.setScene(new Scene(root));
            
            // --- CORRECTION MAJEURE ICI ---
            stage.sizeToScene();   // Force la fenêtre à prendre la taille du FXML (register_member.fxml)
            stage.centerOnScreen(); // Recentre la fenêtre
            // ------------------------------

        } catch (IOException e) {
            e.printStackTrace();
            afficherErreur("Erreur de chargement de l'étape 2.");
        }
    }

    @FXML
    private void allerVersLogin() {
        try {
            URL fxmlUrl = getClass().getResource("/fxml/common/login.fxml");
            if (fxmlUrl == null) {
                System.err.println("Fichier login.fxml introuvable !");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            Stage stage = (Stage) txtNom.getScene().getWindow();
            stage.setScene(new Scene(root));
            
            // --- CORRECTION MAJEURE ICI ---
            stage.sizeToScene();    // Force la fenêtre à prendre la taille du login
            stage.centerOnScreen(); // Recentre
            // ------------------------------
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void viderChamps() {
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        txtPassword.clear();
    }
    
    private void afficherErreur(String msg) {
        lblMessage.setStyle("-fx-text-fill: #e74c3c;"); // Rouge définie dans le CSS généralement
        lblMessage.setText(msg);
    }
}