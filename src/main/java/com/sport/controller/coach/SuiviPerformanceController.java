package com.sport.controller.coach;

import com.sport.model.Coach;
import com.sport.model.Membre;
import com.sport.model.Performance;
import com.sport.model.Utilisateur;
import com.sport.service.CoachService;
import com.sport.service.MembreService;
import com.sport.service.PerformanceService;
import com.sport.utils.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class SuiviPerformanceController {

    // ===== TABLE MEMBRES =====
    @FXML private TableView<Membre> tableMembres;
    @FXML private TableColumn<Membre, String> colNom;
    @FXML private TableColumn<Membre, String> colPrenom;

    // ===== TABLE PERFORMANCES =====
    @FXML private TableView<Performance> tablePerformances;
    @FXML private TableColumn<Performance, LocalDate> colDate;
    @FXML private TableColumn<Performance, Double> colPoids;
    @FXML private TableColumn<Performance, Double> colImc;
    @FXML private TableColumn<Performance, Double> colForce;
    @FXML private TableColumn<Performance, Double> colEndurance;

    // ===== FORM =====
    @FXML private TextField txtPoids;
    @FXML private TextField txtImc;
    @FXML private TextField txtForce;
    @FXML private TextField txtEndurance;
    @FXML private TextField txtTourTaille;


    // ===== SERVICES =====
    private final MembreService membreService = new MembreService();
    private final PerformanceService performanceService = new PerformanceService();
       private final CoachService coachService = new CoachService();


    private final ObservableList<Membre> membres = FXCollections.observableArrayList();
    private final ObservableList<Performance> performances = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initTables();
        chargerMembres();
        ecouterSelectionMembre();
    }

    // ===== INIT TABLES =====
    private void initTables() {

        // Membres
        colNom.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        colPrenom.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPrenom()));
        tableMembres.setItems(membres);

        // Performances
        colDate.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDateMesure()));
        colPoids.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPoids()));
        colImc.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getImc()));
        colForce.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getForce()));
        colEndurance.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getEndurance()));

        tablePerformances.setItems(performances);
    }


private void chargerMembres() {
    // récupère l'utilisateur connecté
    Utilisateur u = UserSession.getInstance().getUtilisateur();

    if (!(u instanceof Coach)) {
        showAlert("Erreur", "Utilisateur connecté n'est pas un coach !");
        return;
    }

    Coach coachConnecte = (Coach) u; // cast vers Coach
    membres.clear();

    // on suppose que CoachService a une méthode getMembresParCoach
    membres.addAll(coachService.getMembresParCoach(coachConnecte));
}



    // ===== LISTENER =====
    private void ecouterSelectionMembre() {
        tableMembres.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldMembre, newMembre) -> {
                if (newMembre != null) {
                    chargerPerformances(newMembre.getId());
                }
            }
        );
    }

    // ===== LOAD PERFORMANCES =====
    private void chargerPerformances(int membreId) {
        performances.clear();
        performances.addAll(
            performanceService.recupererHistoriqueMembre(membreId)
        );
    }

    // ===== SAVE PERFORMANCE =====
            @FXML
private void enregistrerPerformance() {

    Membre membre = tableMembres.getSelectionModel().getSelectedItem();
    if (membre == null) {
        showAlert("Erreur", "Sélectionne un membre");
        return;
    }

    try {
        Performance p = new Performance();
        p.setMembre(membre);
        p.setDateMesure(LocalDate.now());

        p.setPoids(Double.parseDouble(txtPoids.getText()));
        p.setImc(Double.parseDouble(txtImc.getText()));
        p.setTourTaille(Double.parseDouble(txtTourTaille.getText()));
        p.setForce(Double.parseDouble(txtForce.getText()));
        p.setEndurance(Double.parseDouble(txtEndurance.getText()));

        performanceService.enregistrerPerformance(p);

        clearForm();
        chargerPerformances(membre.getId());

    } catch (NumberFormatException e) {
        showAlert("Erreur", "Veuillez saisir des valeurs numériques valides");
    }
}


    // ===== UTILS =====
 private void clearForm() {
    txtPoids.clear();
    txtImc.clear();
    txtTourTaille.clear();
    txtForce.clear();
    txtEndurance.clear();
}

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
