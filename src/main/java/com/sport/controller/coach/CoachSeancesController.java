package com.sport.controller.coach;

import com.sport.model.SeanceCollective;
import com.sport.model.SeanceIndividuelle;
import com.sport.repository.SeanceCollectiveRepository;
import com.sport.repository.SeanceIndividuelleRepository;
import com.sport.service.SeanceCollectiveService;
import com.sport.service.SeanceIndividuelleService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class CoachSeancesController {

    @FXML private TableView<SeanceCollective> tableCollective;
    @FXML private TableColumn<SeanceCollective, Integer> colId;
    @FXML private TableColumn<SeanceCollective, String> colNom;
    @FXML private TableColumn<SeanceCollective, String> colDate;
    @FXML private TableColumn<SeanceCollective, String> colSalle;
    @FXML private TableColumn<SeanceCollective, Integer> colPlaces;
    @FXML private TableColumn<SeanceCollective, Void> colActions;

    @FXML private TableView<SeanceIndividuelle> tableIndividuelle;
    @FXML private TableColumn<SeanceIndividuelle, Integer> colIdInd;
    @FXML private TableColumn<SeanceIndividuelle, String> colMembre;
    @FXML private TableColumn<SeanceIndividuelle, String> colDateInd;
    @FXML private TableColumn<SeanceIndividuelle, String> colSalleInd;
    @FXML private TableColumn<SeanceIndividuelle, Void> colActionsInd;

 private SeanceCollectiveService seanceCService = new SeanceCollectiveService(new SeanceCollectiveRepository());
private SeanceIndividuelleService seanceIService = new SeanceIndividuelleService(new SeanceIndividuelleRepository());


    @FXML
    public void initialize() {
        setupCollectiveTable();
        setupIndividuelleTable();
        loadCollectiveSeances();
        loadIndividuelleSeances();
    }

    private void setupCollectiveTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateHeure"));
        colSalle.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getSalle().getNom())
        );
        colPlaces.setCellValueFactory(new PropertyValueFactory<>("placesDisponibles"));

        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button btnSupprimer = new Button("Supprimer");
            private final HBox pane = new HBox(5, btnSupprimer);

            {
                btnSupprimer.setOnAction(event -> {
                    SeanceCollective s = getTableView().getItems().get(getIndex());
                    seanceCService.delete(s.getId());
                    loadCollectiveSeances();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void setupIndividuelleTable() {
        colIdInd.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMembre.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getMembre().getNom())
        );
        colDateInd.setCellValueFactory(new PropertyValueFactory<>("dateHeure"));
        colSalleInd.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getSalle().getNom())
        );

        colActionsInd.setCellFactory(param -> new TableCell<>() {
            private final Button btnFeedback = new Button("Feedback");
            private final Button btnSupprimer = new Button("Supprimer");
            private final HBox pane = new HBox(5, btnFeedback, btnSupprimer);

            {
                btnFeedback.setOnAction(event -> {
                    SeanceIndividuelle s = getTableView().getItems().get(getIndex());
                    System.out.println("Donner Feedback à " + s.getMembre().getNom());
                    // Ajouter la logique de feedback ici
                });

                btnSupprimer.setOnAction(event -> {
                    SeanceIndividuelle s = getTableView().getItems().get(getIndex());
                    seanceIService.delete(s.getId());
                    loadIndividuelleSeances();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void loadCollectiveSeances() {
        tableCollective.getItems().clear();
        List<SeanceCollective> seances = seanceCService.getAll();
        tableCollective.getItems().addAll(seances);
    }

    private void loadIndividuelleSeances() {
        tableIndividuelle.getItems().clear();
        List<SeanceIndividuelle> seances = seanceIService.getAll();
        tableIndividuelle.getItems().addAll(seances);
    }
}
