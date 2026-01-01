package com.sport.controller.admin;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.sport.model.Abonnement;
import com.sport.model.Membre;
import com.sport.model.StatutAbonnement;
import com.sport.repository.AbonnementRepository;
import com.sport.repository.MembreRepository;
import com.sport.service.AbonnementService;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AbonnementAdminController {

    @FXML
    private TableView<Abonnement> abonnementTable;
    @FXML
    private TableColumn<Abonnement, Integer> colId;
    @FXML
    private TableColumn<Abonnement, String> colMembre;
    @FXML
    private TableColumn<Abonnement, String> colType;
    @FXML
    private TableColumn<Abonnement, String> colStatut;
    @FXML
    private TableColumn<Abonnement, String> colAuto;
    @FXML
    private TableColumn<Abonnement, Date> colDateDebut;
    @FXML
    private TableColumn<Abonnement, Date> colDateFin;
    @FXML
    private TableColumn<Abonnement, Void> colActions;

    @FXML
    private ComboBox<StatutAbonnement> statutFilterCombo;

    private AbonnementService abonnementService = new AbonnementService();
    private AbonnementRepository abonnementRepository = new AbonnementRepository();
    private MembreRepository membreRepository = new MembreRepository();

    @FXML
    public void initialize() {

        // ===== Table Columns =====
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());

        colMembre.setCellValueFactory(data -> {
            Membre m = data.getValue().getMembre();
            String fullName = m.getNom() + " " + m.getPrenom(); // adjust according to your fields
            return new SimpleStringProperty(fullName);
        });

        colType.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTypeAbonnement().name())
        );

        colStatut.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getStatutAbonnement().name())
        );

        colDateDebut.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getDateDebut())
        );

        colDateFin.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().calculerProchaineDateFin())
        );

        colAuto.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().isAutorenouvellement() ? "Oui" : "Non")
        );

        // ===== Filter =====
        statutFilterCombo.setItems(FXCollections.observableArrayList(StatutAbonnement.values()));
        statutFilterCombo.setOnAction(e -> filterByStatut());

        // ===== Action Buttons =====
        addButtonToTable();

        // ===== Load Table =====
        refreshTable();
    }

    @FXML
    private void onAddAbonnement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/AddAbonnementForm.fxml"));
            Parent root = loader.load();

            // Pass member list to the form controller
            AddAbonnementFormController formController = loader.getController();
            List<Membre> membres = membreRepository.listerMembres();
            formController.setMembres(membres);

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Abonnement");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refresh after adding
            refreshTable();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir le formulaire : " + e.getMessage()).show();
        }
    }

    private void addButtonToTable() {
        colActions.setCellFactory(col -> new TableCell<>() {

            private final Button btnActiver = new Button("Activer");
            private final Button btnResilier = new Button("Résilier");
            private final Button btnSupprimer = new Button("Supprimer");

            private final HBox container = new HBox(8, btnActiver, btnResilier, btnSupprimer);

            {
                btnActiver.setOnAction(e -> {
                    Abonnement ab = getTableRow().getItem();
                    if (ab != null) activerAbonnement(ab);
                });

                btnResilier.setOnAction(e -> {
                    Abonnement ab = getTableRow().getItem();
                    if (ab != null) resilierAbonnement(ab);
                });

                btnSupprimer.setOnAction(e -> {
                    Abonnement ab = getTableRow().getItem();
                    if (ab != null) supprimerAbonnement(ab);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

    private void activerAbonnement(Abonnement ab) {
        ab.setStatutAbonnement(StatutAbonnement.ACTIF);
        abonnementRepository.modifierAbonnement(ab);
        refreshTable();
    }

    private void resilierAbonnement(Abonnement ab) {
        ab.setStatutAbonnement(StatutAbonnement.RESILIE);
        abonnementRepository.modifierAbonnement(ab);
        refreshTable();
    }

    private void supprimerAbonnement(Abonnement ab) {
        abonnementService.supprimerAbonnement(ab.getId());
        refreshTable();
    }

    @FXML
    private void onRefresh() {
        filterByStatut();
    }

    private void filterByStatut() {
        StatutAbonnement selected = statutFilterCombo.getSelectionModel().getSelectedItem();
        List<Abonnement> abonnements;

        if (selected == null) {
            abonnements = abonnementService.recupererTousLesAbonnements();
        } else {
            abonnements = abonnementService.recupererTousLesAbonnements().stream()
                    .filter(a -> a.getStatutAbonnement() == selected)
                    .toList();
        }

        abonnementTable.setItems(FXCollections.observableArrayList(abonnements));
    }

    private void refreshTable() {
        abonnementTable.setItems(FXCollections.observableArrayList(abonnementService.recupererTousLesAbonnements()));
    }
}
