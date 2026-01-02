package com.sport.controller.admin;

import java.io.IOException;

import com.sport.model.Salle;
import com.sport.model.TypeSalle;
import com.sport.service.SalleService;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SalleAdminController {

    @FXML private TableView<Salle> salleTable;
    @FXML private TableColumn<Salle, Integer> idColumn;
    @FXML private TableColumn<Salle, String> nomColumn;
    @FXML private TableColumn<Salle, Integer> capaciteColumn;
    @FXML private TableColumn<Salle, TypeSalle> typeColumn;
    @FXML private TableColumn<Salle, Void> actionsColumn;
    @FXML private TableColumn<Salle, Integer> colNbPersonnes;
    @FXML private TableColumn<Salle, Boolean> colStatus;
    @FXML private TableColumn<Salle, Void> colModify;
    @FXML private TableColumn<Salle, Void> colDelete;

    private final SalleService salleService = new SalleService();
    
    private final ObservableList<Salle> salles = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        capaciteColumn.setCellValueFactory(new PropertyValueFactory<>("capacite"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        setupActionColumns();
        loadSalles();
    }

    private void setupActionColumns() {
        addModifyButton();
        addDeleteButton();
    }

    private void loadSalles() {
        salleTable.getItems().setAll(
            salleService.getToutesLesSalles()
        );

        colNbPersonnes.setCellValueFactory(cell -> {
            Salle salle = cell.getValue();
            return new SimpleIntegerProperty(
                salleService.countPeopleInSalle(salle.getId())
            ).asObject();
        });


        colStatus.setCellValueFactory(cell -> {
            Salle salle = cell.getValue();
            boolean available =
                salleService.countPeopleInSalle(salle.getId()) < salle.getCapacite();

            return new SimpleBooleanProperty(available);
        });

        colStatus.setCellFactory(CheckBoxTableCell.forTableColumn(colStatus));
    }

      private void addModifyButton() {
        colModify.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("✎");

            {
                btn.setStyle("-fx-background-color: orange; -fx-text-fill: white;");
                btn.setOnAction(e -> openAddSalleForm());
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
            private final Button btn = new Button("🗑");

            {
                btn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                btn.setOnAction(e -> {
                     Salle s = getTableView().getItems().get(getIndex());
                    salleService.supprimerSalle(s.getId());
                    loadSalles();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }


   @FXML
    private void openAddSalleForm() {
        System.out.println("Open Add Salle Form");

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/admin/salle-form.fxml")
            );
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter une Salle");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Reload table after closing form
            loadSalles();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
