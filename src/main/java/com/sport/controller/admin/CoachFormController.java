package com.sport.controller.admin;

import com.sport.model.Coach;
import com.sport.service.CoachService;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CoachFormController {

    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;
    @FXML private TextField txtAdresse;

    private Coach coach; // null if adding
    private final CoachService coachService = new CoachService();

    // Called by AdminController before showing form
    public void setCoach(Coach coach) {
        this.coach = coach;
        loadCoachData();
    }

    private void loadCoachData() {
        if (coach != null) {
            txtNom.setText(coach.getNom());
            txtPrenom.setText(coach.getPrenom());
            txtEmail.setText(coach.getEmail());
            txtTelephone.setText(coach.getTelephone());
            txtAdresse.setText(coach.getAdresse());
        }
    }

    @FXML
    private void onSave() {
        if (coach == null) {
            // Add new coach
            Coach newCoach = new Coach(
                txtNom.getText(),
                txtPrenom.getText(),
                "2000-01-01", // default date
                txtEmail.getText(),
                txtTelephone.getText(),
                txtAdresse.getText(),
                "defaultPassword" // required
            );
            coachService.ajouterCoach(newCoach);
        } else {
            // Edit existing coach
            coach.setNom(txtNom.getText());
            coach.setPrenom(txtPrenom.getText());
            coach.setEmail(txtEmail.getText());
            coach.setTelephone(txtTelephone.getText());
            coach.setAdresse(txtAdresse.getText());
            coachService.modifierCoach(coach);
        }

        closeForm();
    }

    @FXML
    private void onCancel() {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) txtNom.getScene().getWindow();
        stage.close();
    }
}
