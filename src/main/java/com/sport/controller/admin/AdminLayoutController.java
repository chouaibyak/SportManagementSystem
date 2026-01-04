package com.sport.controller.admin;

import java.io.IOException;

import com.sport.utils.UserSession;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AdminLayoutController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private AnchorPane mainContainer; // ✅ MATCHES FXML

    
    @FXML private Button btnMembers;
    @FXML private Button btnMemberShip;
    @FXML private Button btnCoaches;
    @FXML private Button btnSalles;
    @FXML private Button btnEquipements;
    @FXML private Button btnRapports;
    @FXML private Button btnLogout;
    @FXML private Button btnDashboard;
    @FXML private StackPane contentPane;

    @FXML
    public void initialize() {
        showDashboard(); // default view
    }

    @FXML
    private void showDashboard() {
        loadSection("DashboardAdmin.fxml", "Dashboard");
    }

    @FXML
    private void showMembers() {
        loadSection("member-admin.fxml", "Members");
    }

    @FXML
    private void showMemberShip() {
        loadSection("AbonnementAdmin.fxml", "Member-ship");
    }

    @FXML
    private void showCoaches() {
        loadSection("coach-admin.fxml", "Coaches");
    }

    @FXML
    private void showSalles() {
        loadSection("salle-admin.fxml", "Salles");
    }

    @FXML
    private void showEquipements() {
        loadSection("equipement-admin.fxml", "Equipements");
    }

    @FXML
    private void showRapports() {
        loadSection("RapportAdmin.fxml", "Rapports");
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

           

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     // --- DECONNEXION ---

    @FXML
    private void logout() {
        UserSession.getInstance().cleanUserSession();
        
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/common/login.fxml"));
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.setResizable(false);
            
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
    }

}
