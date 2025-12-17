package com.sport.controller.common;

import com.sport.model.Membre;
import com.sport.model.TypeObjectif;
import com.sport.model.TypePreference;
import com.sport.service.MembreService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterMemberController {

    @FXML private DatePicker datePicker;
    @FXML private TextField txtTelephone;
    @FXML private TextField txtAdresse;
    @FXML private ComboBox<TypeObjectif> comboObjectif;
    @FXML private ComboBox<TypePreference> comboPreference;
    @FXML private Label lblMessage;

    private Membre membreEnCours;
    private MembreService membreService = new MembreService();

    @FXML
    public void initialize() {
        // Remplir les listes déroulantes avec les Enum
        comboObjectif.getItems().setAll(TypeObjectif.values());
        comboPreference.getItems().setAll(TypePreference.values());
    }

    /**
     * Méthode appelée par l'écran précédent pour passer les infos.
     */
    public void initData(Membre membrePartiel) {
        this.membreEnCours = membrePartiel;
    }

    @FXML
    private void handleFinaliser() {
        // 1. Récupération des suites d'infos
        String tel = txtTelephone.getText();
        String adr = txtAdresse.getText();
        
        // Conversion de la date
        String dateNaiss = (datePicker.getValue() != null) ? datePicker.getValue().toString() : null;

        if (comboObjectif.getValue() == null || comboPreference.getValue() == null) {
            lblMessage.setText("Veuillez sélectionner un objectif et une préférence.");
            return;
        }

        // 2. Mise à jour de l'objet Membre
        membreEnCours.setTelephone(tel);
        membreEnCours.setAdresse(adr);
        membreEnCours.setDateNaissance(dateNaiss);
        membreEnCours.setObjectifSportif(comboObjectif.getValue());
        membreEnCours.setPreferences(comboPreference.getValue());

        // 3. Sauvegarde via le SERVICE (qui appelle le Repository existant)
        // Pas besoin de AuthService ici car MembreService gère tout
        membreService.creerMembre(membreEnCours);

        // 4. Succès et redirection vers Login
        lblMessage.setStyle("-fx-text-fill: green;");
        lblMessage.setText("Inscription terminée !");
        retourAuLogin();
    }

    @FXML
    private void handleRetour() {
        // Logique pour revenir en arrière si besoin (ou fermer)
    }

    private void retourAuLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/common/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) txtTelephone.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}