package com.sport.controller.admin;

import java.time.LocalDate;
import java.util.List;

import com.sport.model.Rapport;
import com.sport.service.RapportService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class RapportAdminController {

    @FXML private ComboBox<String> typeComboBox;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;

    @FXML private TableView<Rapport> rapportTable;
    @FXML private TableColumn<Rapport, Integer> idColumn;
    @FXML private TableColumn<Rapport, String> typeColumn;
    @FXML private TableColumn<Rapport, String> dateDebutColumn;
    @FXML private TableColumn<Rapport, String> dateFinColumn;
    @FXML private TableColumn<Rapport, String> donneesColumn;

    private final RapportService rapportService = new RapportService();

    @FXML
    public void initialize() {
        // Initialiser ComboBox
        typeComboBox.setItems(FXCollections.observableArrayList(
                "OCCUPATION_COURS",
                "FREQUENTATION_SALLE",
                "SATISFACTION_MEMBRES",
                "REVENUS_ABONNEMENTS"
        ));

        // Lier les colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateDebutColumn.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        dateFinColumn.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        donneesColumn.setCellValueFactory(new PropertyValueFactory<>("donnees"));

        chargerRapports();
    }

    @FXML
    private void handleGenererRapport() {
        String type = typeComboBox.getValue();
        LocalDate debut = dateDebutPicker.getValue();
        LocalDate fin = dateFinPicker.getValue();

        if (type == null || debut == null || fin == null) {
            afficherAlerte("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        rapportService.genererRapport(
                type,
                debut.toString(),
                fin.toString()
        );

        chargerRapports();
    }

    @FXML
    private void handleSupprimerRapport() {
        Rapport rapport = rapportTable.getSelectionModel().getSelectedItem();

        if (rapport == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner un rapport.");
            return;
        }

        rapportService.supprimerRapport(rapport.getId());
        chargerRapports();
    }

    private void chargerRapports() {
        List<Rapport> rapports = rapportService.listerRapports();
        rapportTable.setItems(FXCollections.observableArrayList(rapports));
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
