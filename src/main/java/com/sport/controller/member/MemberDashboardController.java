package com.sport.controller.member;

import com.sport.model.Utilisateur;
import com.sport.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MemberDashboardController {

    @FXML private Label lblBienvenue;

    @FXML
    public void initialize() {
        // Récupère le nom de l'utilisateur connecté
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        if (user != null) {
            lblBienvenue.setText("Bonjour, " + user.getPrenom() + " !");
        }
    }
}