package com.sport.controller.admin;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class AdminLayoutController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private AnchorPane mainContainer; // ✅ MATCHES FXML

    @FXML
    private Label lblLayoutTitle;

    @FXML private Button btnMembers;
    @FXML private Button btnCoaches;
    @FXML private Button btnSalles;
    @FXML private Button btnEquipements;
    @FXML private Button btnRapports;
    @FXML private Button btnLogout;

    @FXML
    public void initialize() {
        showMembers();
    }

    @FXML
    private void showMembers() {
        loadSection("member-admin.fxml", "Members");
    }

    @FXML
    private void showCoaches() {
        loadSection("coach-admin.fxml", "Coaches");
    }

    @FXML
    private void showSalles() {
        loadSection("room-admin.fxml", "Salles");
    }

    @FXML
    private void showEquipements() {
        loadSection("equipment-admin.fxml", "Equipements");
    }

    @FXML
    private void showRapports() {
        loadSection("rapport-admin.fxml", "Rapports");
    }

    @FXML
    private void logout() {
        System.out.println("Logout clicked");
    }

    // ✅ SAFE LOADER FOR ANY FXML ROOT
    private void loadSection(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/admin/" + fxmlFile)
            );

            Parent view = loader.load();

            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(view);

            // Make child fill AnchorPane
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            lblLayoutTitle.setText(title);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
