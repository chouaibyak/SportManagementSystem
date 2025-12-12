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

public class RegisterController {

    // --- Éléments de l'interface (FXML) ---
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    
    @FXML private RadioButton radioMembre;
    @FXML private RadioButton radioCoach;
    @FXML private ToggleGroup roleGroup; // Assure-toi que fx:id="roleGroup" est défini dans le FXML

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

        boolean succes = false;

        // 3. Logique selon le rôle choisi
        if (radioMembre.isSelected()) {
            // --- Inscription MEMBRE ---
            Membre nouveau = new Membre();
            nouveau.setNom(nom);
            nouveau.setPrenom(prenom);
            nouveau.setEmail(email);
            // On délègue l'insertion et la gestion du mot de passe au service
            succes = authService.registerMembre(nouveau, pwd);

        } else if (radioCoach.isSelected()) {
            // --- Inscription COACH ---
            Coach nouveau = new Coach();
            nouveau.setNom(nom);
            nouveau.setPrenom(prenom);
            nouveau.setEmail(email);
            // On délègue l'insertion et la gestion du mot de passe au service
            succes = authService.registerCoach(nouveau, pwd);
        } else {
            // Aucun bouton radio n'est coché (si pas de valeur par défaut)
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Veuillez choisir un rôle (Membre ou Coach).");
            return;
        }

        // 4. Gestion du résultat
        if (succes) {
            lblMessage.setStyle("-fx-text-fill: green;");
            lblMessage.setText("Compte créé avec succès ! Vous pouvez vous connecter.");
            viderChamps();
            // Optionnel : décommenter la ligne suivante pour rediriger immédiatement
            // allerVersLogin(); 
        } else {
            lblMessage.setStyle("-fx-text-fill: red;");
            lblMessage.setText("Erreur : Cet email est déjà utilisé ou problème technique.");
        }
    }

    /**
     * Méthode appelée quand on clique sur "Retour Connexion".
     */
    @FXML
    private void allerVersLogin() {
        try {
            // 1. Définir le chemin attendu
            String fxmlPath = "/fxml/common/login.fxml";
            
            // 2. Vérifier si Java le trouve AVANT de charger
            java.net.URL fxmlUrl = getClass().getResource(fxmlPath);
            
            if (fxmlUrl == null) {
                System.err.println("ERREUR CRITIQUE : Fichier introuvable !");
                System.err.println("Je cherche ici : src/main/resources" + fxmlPath);
                System.err.println("Vérifie que le dossier 'common' existe et que le fichier s'appelle bien 'login.fxml' (minuscules).");
                lblMessage.setText("Erreur interne : Fichier login.fxml introuvable.");
                return;
            }

            System.out.println("Fichier trouvé : " + fxmlUrl);

            // 3. Chargement
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();
            
            // 4. Changement de scène
            Stage stage = (Stage) txtNom.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Sport App - Connexion");
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            lblMessage.setText("Impossible de charger la page de connexion.");
        }
    }

    /**
     * Vide les champs après une inscription réussie.
     */
    private void viderChamps() {
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        txtPassword.clear();
        // On remet le focus sur le nom
        txtNom.requestFocus();
    }
}