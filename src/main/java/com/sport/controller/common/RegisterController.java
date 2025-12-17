package com.sport.controller.common;

import com.sport.model.Coach;
import com.sport.model.Membre;
import com.sport.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

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
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Veuillez remplir tous les champs.");
            return;
        }

        // 3. Aiguillage selon le rôle
        if (radioMembre.isSelected()) {
            // --- CAS MEMBRE : ON PASSE A L'ETAPE 2 ---
            // On ne sauvegarde RIEN en BDD ici, on passe juste les infos
            allerVersEtape2Membre(nom, prenom, email, pwd);

        } else if (radioCoach.isSelected()) {
            // --- CAS COACH : INSCRIPTION DIRECTE ---
            Coach nouveau = new Coach();
            nouveau.setNom(nom);
            nouveau.setPrenom(prenom);
            nouveau.setEmail(email);
            
            // AuthService gère l'insert Coach + Utilisateur + Hash mot de passe
            boolean succes = authService.registerCoach(nouveau, pwd);
            
            if (succes) {
                lblMessage.setStyle("-fx-text-fill: green;");
                lblMessage.setText("Compte Coach créé avec succès !");
                viderChamps(); // Important pour ne pas réinscrire par erreur
            } else {
                lblMessage.setStyle("-fx-text-fill: red;");
                lblMessage.setText("Erreur : Email déjà utilisé ou problème technique.");
            }
        } else {
            // Cas où aucun bouton n'est coché
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Veuillez choisir un type de compte.");
        }
    }

    private void allerVersEtape2Membre(String nom, String prenom, String email, String pwd) {
        try {
            // 1. Charger la vue suivante (Assure-toi que ce fichier FXML existe !)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/common/register_member.fxml"));
            Parent root = loader.load();

            // 2. Créer l'objet temporaire (pas encore en BDD)
            Membre membreTemp = new Membre();
            membreTemp.setNom(nom);
            membreTemp.setPrenom(prenom);
            membreTemp.setEmail(email);
            membreTemp.setMotDePasse(pwd); // On passe le mot de passe dans l'objet pour l'étape suivante

            // 3. Passer l'objet au contrôleur suivant
            // C'est ici que la magie opère : on envoie les données à l'autre écran
            RegisterMemberController controller = loader.getController();
            controller.initData(membreTemp);

            // 4. Afficher la nouvelle scène
            Stage stage = (Stage) txtNom.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inscription Membre - Étape 2");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Erreur de chargement de l'étape 2 (Vérifiez le fichier FXML).");
        }
    }

    @FXML
    private void allerVersLogin() {
        try {
            String fxmlPath = "/fxml/common/login.fxml";
            URL fxmlUrl = getClass().getResource(fxmlPath);
            
            if (fxmlUrl == null) {
                System.err.println("Fichier login.fxml introuvable !");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            Stage stage = (Stage) txtNom.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sport App - Connexion");
            
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
}