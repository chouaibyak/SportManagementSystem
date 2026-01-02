package com.sport.controller.admin;

import java.io.IOException;

import com.sport.model.Equipement;
import com.sport.service.EquipementService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class EquipementAdminController {

    @FXML private TableView<Equipement> equipementTable;
    @FXML private TableColumn<Equipement, Integer> idColumn;
    @FXML private TableColumn<Equipement, String> nomColumn;
    @FXML private TableColumn<Equipement, String> typeColumn;
    @FXML private TableColumn<Equipement, String> etatColumn;
    @FXML private TableColumn<Equipement, java.util.Date> dateColumn;
    @FXML private TableColumn<Equipement, Void> colModify;
    @FXML private TableColumn<Equipement, Void> colDelete;
    @FXML private Button btnAddEquipement;


    private final EquipementService equipementService = new EquipementService(new com.sport.repository.EquipementRepository());

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        etatColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateAchat"));

        loadTable();
        setupActionColumns();
          btnAddEquipement.setOnAction(e -> openAddEquipementForm());
    }

    private void loadTable() {
        equipementTable.setItems(FXCollections.observableArrayList(equipementService.listerEquipements()));
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
                btn.setOnAction(e -> openAddEquipementForm());
                
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
                    Equipement equipement = getTableView().getItems().get(getIndex());
                    equipementService.supprimerEquipement(equipement.getId());
                    loadTable();
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
    private void openAddEquipementForm() {
          System.out.println("Open Add Equipement Form");

        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/admin/Equipement-Form.fxml")
            );
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Équipement");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Reload table after closing form
            loadTable();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
}
