package com.sport.controller.common;

import java.io.IOException;
import com.sport.model.Coach;
import com.sport.service.CoachService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterCoachController {

    @FXML private DatePicker dpDateNaissance;
    @FXML private TextField txtTelephone;
    @FXML private TextField txtAdresse;
    @FXML private TextField txtSpecialite;
    @FXML private Label lblMessage;

    private Coach coachEnCours;
    private CoachService coachService = new CoachService();

    // Reçoit les données de l'étape 1
    public void initData(Coach coach) {
        this.coachEnCours = coach;
    }

    @FXML
    private void handleFinaliserInscription() {
        if (dpDateNaissance.getValue() == null || txtTelephone.getText().isEmpty() || 
            txtAdresse.getText().isEmpty() || txtSpecialite.getText().isEmpty()) {
            lblMessage.setText("Veuillez remplir tous les champs.");
            return;
        }

        // Remplissage des données manquantes
        coachEnCours.setDateNaissance(dpDateNaissance.getValue().toString());
        coachEnCours.setTelephone(txtTelephone.getText().trim());
        coachEnCours.setAdresse(txtAdresse.getText().trim());
        
        // Ajout de la spécialité
        coachEnCours.ajouterSpecialite(txtSpecialite.getText().trim());

        // Appel au SERVICE (qui appellera le Repository mis à jour)
        coachService.ajouterCoach(coachEnCours);

        // Si pas d'exception, on suppose que c'est bon (ou vérifiez l'ID > 0)
        // Redirection vers Login
        allerVersLogin("Compte Coach créé ! Connectez-vous.");
    }

    @FXML
    private void handleAnnuler() {
        allerVersLogin(null);
    }

    private void allerVersLogin(String successMessage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/common/login.fxml"));
            Parent root = loader.load();
            
            // Si vous avez un moyen de passer un message au LoginController, faites-le ici
            // Sinon, c'est juste une redirection simple
            
            Stage stage = (Stage) txtTelephone.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.sizeToScene();
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}