package com.sport.controller.coach;

import com.sport.model.Membre;
import com.sport.model.SeanceCollective;
import com.sport.service.MembreService;
import com.sport.service.SeanceCollectiveService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.Optional;

public class CoachSeanceMembresController {

    @FXML private TableView<Membre> tableMembres;
    @FXML private TableColumn<Membre, String> colNom;
    @FXML private TableColumn<Membre, String> colPrenom;
    @FXML private TableColumn<Membre, String> colEmail;
    @FXML private Button btnAjouterMembre;
    @FXML private Button btnSupprimerMembre;

    private SeanceCollectiveService seanceService;
    private MembreService membreService;
    private SeanceCollective selectedSeanceCollective;
    private ObservableList<Membre> membresObservable = FXCollections.observableArrayList();

    public void initialize() {
        seanceService = new SeanceCollectiveService(new com.sport.repository.SeanceCollectiveRepository());
        membreService = new MembreService();

        colNom.setCellValueFactory(cellData -> new javafx.beans.property.ReadOnlyStringWrapper(cellData.getValue().getNom()));
        colPrenom.setCellValueFactory(cellData -> new javafx.beans.property.ReadOnlyStringWrapper(cellData.getValue().getPrenom()));
        colEmail.setCellValueFactory(cellData -> new javafx.beans.property.ReadOnlyStringWrapper(cellData.getValue().getEmail()));

        tableMembres.setItems(membresObservable);
    }

    public void setSeanceCollective(SeanceCollective seance) {
        this.selectedSeanceCollective = seance;
        loadMembres();
    }

    private void loadMembres() {
        if (selectedSeanceCollective != null) {
            membresObservable.setAll(selectedSeanceCollective.getListeMembers());
        }
    }

    @FXML
    private void ajouterMembre() {
        if (selectedSeanceCollective == null) return;

        List<Membre> tousLesMembres = membreService.getAllMembres();
        List<Membre> disponibles = tousLesMembres.stream()
                .filter(m -> !selectedSeanceCollective.getListeMembers().contains(m))
                .toList();

        if (disponibles.isEmpty()) {
            showAlert("Info", "Aucun membre disponible pour l'ajout.");
            return;
        }

        List<String> choix = disponibles.stream()
                .map(m -> m.getNom() + " " + m.getPrenom() + " (" + m.getEmail() + ")")
                .toList();

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choix.get(0), choix);
        dialog.setTitle("Ajouter Membre");
        dialog.setHeaderText("Sélectionner un membre à ajouter");
        dialog.setContentText("Membre :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selection -> {
            Membre membreChoisi = disponibles.stream()
                    .filter(m -> (m.getNom() + " " + m.getPrenom() + " (" + m.getEmail() + ")").equals(selection))
                    .findFirst()
                    .orElse(null);

            if (membreChoisi != null) {
                boolean success = seanceService.reserverPlace(selectedSeanceCollective.getId(), membreChoisi);
                if (success) {
                    selectedSeanceCollective.setListeMembers(
                            seanceService.getById(selectedSeanceCollective.getId()).getListeMembers()
                    );
                    membresObservable.setAll(selectedSeanceCollective.getListeMembers());
                    showAlert("Succès", "Membre ajouté à la séance.");
                } else {
                    showAlert("Erreur", "Impossible d'ajouter ce membre (place max atteinte).");
                }
            }
        });
    }

    @FXML
    private void supprimerMembre() {
        Membre membre = tableMembres.getSelectionModel().getSelectedItem();
        if (selectedSeanceCollective == null || membre == null) {
            showAlert("Info", "Veuillez sélectionner un membre à supprimer.");
            return;
        }

        boolean success = seanceService.annulerReservation(selectedSeanceCollective.getId(), membre);
        if (success) {
            membresObservable.remove(membre);
            showAlert("Succès", "Membre supprimé de la séance.");
        } else {
            showAlert("Erreur", "Impossible de supprimer ce membre.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
