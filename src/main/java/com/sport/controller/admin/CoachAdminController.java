package com.sport.controller.admin;

import java.util.stream.Collectors;

import com.sport.model.Coach;
import com.sport.service.CoachService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CoachAdminController {

    @FXML
    private TableView<Coach> coachTable;
    @FXML
    private TableColumn<Coach, Integer> colId;
    @FXML
    private TableColumn<Coach, String> colNom;
    @FXML
    private TableColumn<Coach, String> colPrenom;
    @FXML
    private TableColumn<Coach, String> colEmail;
    @FXML
    private TableColumn<Coach, String> colTelephone;
    @FXML
    private TableColumn<Coach, String> colSpecialites;
    @FXML
    private TableColumn<Coach, Void> colModify;
    @FXML
    private TableColumn<Coach, Void> colDelete;
    @FXML
    private Button btnAddCoach;

    private ObservableList<Coach> coachList;
    private final CoachService coachService = new CoachService();

    @FXML
    public void initialize() {
        loadCoaches();
        setupColumns();
        setupActionColumns();

        btnAddCoach.setOnAction(e -> openCoachForm(null));
    }
   
    private void loadCoaches() {
        coachList = FXCollections.observableArrayList(coachService.getAllCoaches());
        coachTable.setItems(coachList);
    }

    private void setupColumns() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colNom.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNom()));
        colPrenom.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPrenom()));
        colEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));
        colTelephone.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTelephone()));
        colSpecialites.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getSpecialites().stream().collect(Collectors.joining(", "))
        ));
    }

    private void setupActionColumns() {
        addModifyButton();
        addDeleteButton();
    }

    private void addModifyButton() {
        colModify.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button();

            {
                btn.setStyle("-fx-background-color: orange; -fx-text-fill: white;");
                btn.setOnAction(e -> {
                    Coach coach = getTableView().getItems().get(getIndex());
                    openCoachForm(coach);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void addDeleteButton() {
        colDelete.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("✎");

            {
                btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                btn.setOnAction(e -> {
                    Coach coach = getTableView().getItems().get(getIndex());
                    coachService.supprimerCoach(coach.getId());
                    coachList.remove(coach);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

   private void openCoachForm(Coach coach) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/coach-form.fxml"));
        Parent root = loader.load();

        CoachFormController controller = loader.getController();
        controller.setCoach(coach); // null for Add, existing Coach for Edit

        Stage stage = new Stage();
        stage.setTitle(coach == null ? "Ajouter Coach" : "Modifier Coach");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(root));
        stage.showAndWait();

        loadCoaches(); // refresh table
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
