package com.sport.controller.coach;

import com.sport.model.Coach;
import com.sport.model.Membre;
import com.sport.model.Performance;
import com.sport.model.Utilisateur;
import com.sport.service.CoachService;
import com.sport.service.PerformanceService;
import com.sport.utils.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.time.LocalDate;

public class CoachPerformanceController {

    // ==== Membres ====
    @FXML private TableView<Membre> tableMembres;
    @FXML private TableColumn<Membre, String> colNom;
    @FXML private TableColumn<Membre, String> colPrenom;

    // ==== Performances ====
    @FXML private TableView<Performance> tablePerformances;
    @FXML private TableColumn<Performance, LocalDate> colDate;
    @FXML private TableColumn<Performance, Double> colPoids;
    @FXML private TableColumn<Performance, Double> colImc;
    @FXML private TableColumn<Performance, Double> colTourTaille;
    @FXML private TableColumn<Performance, Double> colForce;
    @FXML private TableColumn<Performance, Double> colEndurance;

    // ==== Graphiques ====
    @FXML private LineChart<String, Number> chartPoids;
    @FXML private LineChart<String, Number> chartImc;
    @FXML private LineChart<String, Number> chartTourTaille;

    // ==== Formulaire ====
    @FXML private TextField txtPoids;
    @FXML private TextField txtTourTaille;
    @FXML private TextField txtForce;
    @FXML private TextField txtEndurance;
    @FXML private TextField txtIMC;

    // ==== Services ====
    private final CoachService coachService = new CoachService();
    private final PerformanceService performanceService = new PerformanceService();

    private final ObservableList<Membre> membres = FXCollections.observableArrayList();
    private final ObservableList<Performance> performances = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initTables();
        chargerMembres();
        ecouterSelectionMembre();
    }

    private void initTables() {
        // Membres
        colNom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNom()));
        colPrenom.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPrenom()));
        tableMembres.setItems(membres);

        // Performances
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDateMesure()));
        colPoids.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getPoids()));
        colImc.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getImc()));
        colTourTaille.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTourTaille()));
        colForce.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getForce()));
        colEndurance.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getEndurance()));
        tablePerformances.setItems(performances);
    }

    private void chargerMembres() {
        Utilisateur u = UserSession.getInstance().getUtilisateur();
        if (!(u instanceof Coach)) { 
            showAlert("Erreur", "Utilisateur non coach"); 
            return; 
        }

        Coach coach = (Coach) u;
        membres.clear();
        membres.addAll(coachService.getMembresParCoach(coach));
    }

    private void ecouterSelectionMembre() {
        tableMembres.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) chargerPerformances(newVal.getId());
        });
    }

    private void chargerPerformances(int membreId) {
        performances.clear();
        performances.addAll(performanceService.recupererHistoriqueMembre(membreId));
        genererGraphiques();
    }

    private void genererGraphiques() {
        chartPoids.getData().clear();
        chartImc.getData().clear();
        chartTourTaille.getData().clear();

        XYChart.Series<String, Number> seriesPoids = new XYChart.Series<>();
        seriesPoids.setName("Poids");

        XYChart.Series<String, Number> seriesIMC = new XYChart.Series<>();
        seriesIMC.setName("IMC");

        XYChart.Series<String, Number> seriesTaille = new XYChart.Series<>();
        seriesTaille.setName("Tour Taille");

        for (Performance p : performances) {
            String date = p.getDateMesure().toString();
            seriesPoids.getData().add(new XYChart.Data<>(date, p.getPoids()));
            seriesIMC.getData().add(new XYChart.Data<>(date, p.getImc()));
            seriesTaille.getData().add(new XYChart.Data<>(date, p.getTourTaille()));
        }

        chartPoids.getData().add(seriesPoids);
        chartImc.getData().add(seriesIMC);
        chartTourTaille.getData().add(seriesTaille);
    }

    @FXML
    private void enregistrerPerformance() {
        Membre m = tableMembres.getSelectionModel().getSelectedItem();
        if (m == null) { showAlert("Erreur", "Sélectionner un membre"); return; }

        try {
            Performance p = new Performance();
            p.setMembre(m);
            p.setDateMesure(LocalDate.now());
            p.setPoids(Double.parseDouble(txtPoids.getText()));
            p.setTourTaille(Double.parseDouble(txtTourTaille.getText()));
            p.setForce(Double.parseDouble(txtForce.getText()));
            p.setEndurance(Double.parseDouble(txtEndurance.getText()));

            if (!txtIMC.getText().isEmpty()) {
                p.setImc(Double.parseDouble(txtIMC.getText()));
            }

            performanceService.enregistrerPerformance(p);
            clearForm();
            chargerPerformances(m.getId());

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Valeurs numériques invalides");
        }
    }

    private void clearForm() {
        txtPoids.clear();
        txtTourTaille.clear();
        txtForce.clear();
        txtEndurance.clear();
        txtIMC.clear();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(title);
        a.setContentText(msg);
        a.showAndWait();
    }
}
