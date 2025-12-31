package com.sport.controller.member;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.sport.model.Membre;
import com.sport.model.Performance;
import com.sport.service.PerformanceService;
import com.sport.utils.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

public class MemberPerformanceController {

    // Graphiques
    @FXML
    private LineChart<String, Number> chartPoids;
    @FXML
    private LineChart<String, Number> chartIMC;
    @FXML
    private LineChart<String, Number> chartTourTaille;
    @FXML
    private BarChart<String, Number> chartComparaison;

    // Statistiques actuelles
    @FXML
    private Label lblPoidsActuel;
    @FXML
    private Label lblIMCActuel;
    @FXML
    private Label lblTourTailleActuel;
    @FXML
    private Label lblForceActuelle;
    @FXML
    private Label lblEnduranceActuelle;

    // Évolutions
    @FXML
    private Label lblEvolutionPoids;
    @FXML
    private Label lblEvolutionIMC;
    @FXML
    private Label lblEvolutionTourTaille;
    @FXML
    private Label lblDateDerniereMesure;

    // Objectifs
    @FXML
    private Label lblObjectifPoids;
    @FXML
    private ProgressBar progressPoids;
    @FXML
    private Label lblProgressPourcentage;

    // Tableau historique
    @FXML
    private TableView<PerformanceDisplay> tableHistorique;
    @FXML
    private TableColumn<PerformanceDisplay, String> colDate;
    @FXML
    private TableColumn<PerformanceDisplay, String> colPoids;
    @FXML
    private TableColumn<PerformanceDisplay, String> colIMC;
    @FXML
    private TableColumn<PerformanceDisplay, String> colTourTaille;
    @FXML
    private TableColumn<PerformanceDisplay, String> colForce;
    @FXML
    private TableColumn<PerformanceDisplay, String> colEndurance;

    // Filtres
    @FXML
    private ComboBox<String> comboPeriode;
    @FXML
    private TextField txtPoidsObjectif;

    private PerformanceService performanceService;
    private Membre membreConnecte;
    private ObservableList<PerformanceDisplay> listePerformances;
    private List<Performance> historiqueComplet;

    @FXML
    public void initialize() {
        performanceService = new PerformanceService();
        listePerformances = FXCollections.observableArrayList();

        try {
            Object u = UserSession.getInstance().getUtilisateur();
            if (!(u instanceof Membre)) {
                afficherErreur("Session invalide",
                        "Utilisateur non connecté ou type incorrect (pas un Membre).");
                return;
            }
            membreConnecte = (Membre) u;

            configurerComboBox();
            configurerTableau();

            // Important : lancer le chargement après que la vue soit prête
            javafx.application.Platform.runLater(() -> {
                try {
                    chargerDonnees();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    afficherErreur("Erreur chargement performances",
                            "Impossible de charger les données. Vérifiez la console.");
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
            afficherErreur("Erreur initialize",
                    "Erreur inattendue. Vérifiez la console.");
        }
    }

    private void afficherErreur(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(titre);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void configurerComboBox() {
        comboPeriode.setItems(FXCollections.observableArrayList(
                "7 derniers jours",
                "30 derniers jours",
                "3 derniers mois",
                "6 derniers mois",
                "Tout"));
        comboPeriode.setValue("3 derniers mois");
        comboPeriode.setOnAction(e -> rafraichirDonnees());
    }

    private void configurerTableau() {
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colPoids.setCellValueFactory(new PropertyValueFactory<>("poids"));
        colIMC.setCellValueFactory(new PropertyValueFactory<>("imc"));
        colTourTaille.setCellValueFactory(new PropertyValueFactory<>("tourTaille"));
        colForce.setCellValueFactory(new PropertyValueFactory<>("force"));
        colEndurance.setCellValueFactory(new PropertyValueFactory<>("endurance"));

        tableHistorique.setItems(listePerformances);
    }

    private void chargerDonnees() {
        historiqueComplet = performanceService.recupererHistoriqueMembre(membreConnecte.getId());

        if (historiqueComplet == null || historiqueComplet.isEmpty()) {
            listePerformances.clear();
            chartPoids.getData().clear();
            chartIMC.getData().clear();
            chartTourTaille.getData().clear();
            chartComparaison.getData().clear();
            afficherMessageAucuneDonnee();
            return;
        }

        List<Performance> historiqueFiltre = filtrerParPeriode(historiqueComplet);

        remplirTableau(historiqueFiltre);
        calculerStatistiques(historiqueComplet);
        genererGraphiques(historiqueFiltre);
        calculerProgression(historiqueComplet);
    }

    private List<Performance> filtrerParPeriode(List<Performance> historique) {
        if (historique.isEmpty())
            return historique;

        LocalDate dateDebut = LocalDate.now();
        String periode = comboPeriode.getValue();

        switch (periode) {
            case "7 derniers jours":
                dateDebut = LocalDate.now().minusDays(7);
                break;
            case "30 derniers jours":
                dateDebut = LocalDate.now().minusDays(30);
                break;
            case "3 derniers mois":
                dateDebut = LocalDate.now().minusMonths(3);
                break;
            case "6 derniers mois":
                dateDebut = LocalDate.now().minusMonths(6);
                break;
            default:
                return historique;
        }

        final LocalDate dateLimite = dateDebut;
        return historique.stream()
                .filter(p -> p.getDateMesure().isAfter(dateLimite) || p.getDateMesure().isEqual(dateLimite))
                .toList();
    }

    private void remplirTableau(List<Performance> historique) {
        listePerformances.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Performance p : historique) {
            listePerformances.add(new PerformanceDisplay(
                    p.getDateMesure().format(formatter),
                    String.format("%.1f kg", p.getPoids()),
                    String.format("%.1f", p.getImc()),
                    String.format("%.1f cm", p.getTourTaille()),
                    String.format("%.0f", p.getForce()),
                    String.format("%.0f", p.getEndurance())));
        }
    }

    private void calculerStatistiques(List<Performance> historique) {
        if (historique.isEmpty())
            return;

        // Dernière performance
        Performance derniere = historique.get(historique.size() - 1);

        lblPoidsActuel.setText(String.format("%.1f kg", derniere.getPoids()));
        lblIMCActuel.setText(String.format("%.1f", derniere.getImc()));
        lblTourTailleActuel.setText(String.format("%.1f cm", derniere.getTourTaille()));
        lblForceActuelle.setText(String.format("%.0f", derniere.getForce()));
        lblEnduranceActuelle.setText(String.format("%.0f", derniere.getEndurance()));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblDateDerniereMesure.setText("Dernière mesure : " + derniere.getDateMesure().format(formatter));
    }

    private void calculerProgression(List<Performance> historique) {
        if (historique.size() < 2) {
            lblEvolutionPoids.setText("N/A");
            lblEvolutionIMC.setText("N/A");
            lblEvolutionTourTaille.setText("N/A");
            return;
        }

        Performance premiere = historique.get(0);
        Performance derniere = historique.get(historique.size() - 1);

        // Évolution du poids
        double evolutionPoids = derniere.getPoids() - premiere.getPoids();
        lblEvolutionPoids.setText(String.format("%+.1f kg", evolutionPoids));
        lblEvolutionPoids.setStyle(evolutionPoids < 0 ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        // Évolution de l'IMC
        double evolutionIMC = derniere.getImc() - premiere.getImc();
        lblEvolutionIMC.setText(String.format("%+.1f", evolutionIMC));
        lblEvolutionIMC.setStyle(evolutionIMC < 0 ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        // Évolution du tour de taille
        double evolutionTaille = derniere.getTourTaille() - premiere.getTourTaille();
        lblEvolutionTourTaille.setText(String.format("%+.1f cm", evolutionTaille));
        lblEvolutionTourTaille.setStyle(evolutionTaille < 0 ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;"
                : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        // Calcul objectif (si renseigné)
        calculerObjectifPoids(derniere.getPoids());
    }

    private void calculerObjectifPoids(double poidsActuel) {
        String objectifStr = txtPoidsObjectif.getText();
        if (objectifStr == null || objectifStr.isEmpty()) {
            lblObjectifPoids.setText("Définissez un objectif");
            progressPoids.setProgress(0);
            lblProgressPourcentage.setText("0%");
            return;
        }

        try {
            double poidsObjectif = Double.parseDouble(objectifStr);
            double poidsDepart = historiqueComplet.isEmpty() ? poidsActuel : historiqueComplet.get(0).getPoids();

            double progression = ((poidsDepart - poidsActuel) / (poidsDepart - poidsObjectif)) * 100;
            progression = Math.max(0, Math.min(100, progression));

            progressPoids.setProgress(progression / 100.0);
            lblProgressPourcentage.setText(String.format("%.0f%%", progression));
            lblObjectifPoids.setText(String.format("%.1f kg / %.1f kg", poidsActuel, poidsObjectif));

        } catch (NumberFormatException e) {
            lblObjectifPoids.setText("Objectif invalide");
        }
    }

    private void genererGraphiques(List<Performance> historique) {
        genererGraphiquePoids(historique);
        genererGraphiqueIMC(historique);
        genererGraphiqueTourTaille(historique);
        genererGraphiqueComparaison(historique);
    }

    private void genererGraphiquePoids(List<Performance> historique) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Poids (kg)");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        for (Performance p : historique) {
            series.getData().add(new XYChart.Data<>(
                    p.getDateMesure().format(formatter),
                    p.getPoids()));
        }

        chartPoids.getData().clear();
        chartPoids.getData().add(series);
    }

    private void genererGraphiqueIMC(List<Performance> historique) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("IMC");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        for (Performance p : historique) {
            series.getData().add(new XYChart.Data<>(
                    p.getDateMesure().format(formatter),
                    p.getImc()));
        }

        chartIMC.getData().clear();
        chartIMC.getData().add(series);
    }

    private void genererGraphiqueTourTaille(List<Performance> historique) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tour de taille (cm)");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        for (Performance p : historique) {
            series.getData().add(new XYChart.Data<>(
                    p.getDateMesure().format(formatter),
                    p.getTourTaille()));
        }

        chartTourTaille.getData().clear();
        chartTourTaille.getData().add(series);
    }

    private void genererGraphiqueComparaison(List<Performance> historique) {
        if (historique.size() < 2)
            return;

        Performance premiere = historique.get(0);
        Performance derniere = historique.get(historique.size() - 1);

        XYChart.Series<String, Number> seriesDebut = new XYChart.Series<>();
        seriesDebut.setName("Début");
        seriesDebut.getData().add(new XYChart.Data<>("Force", premiere.getForce()));
        seriesDebut.getData().add(new XYChart.Data<>("Endurance", premiere.getEndurance()));

        XYChart.Series<String, Number> seriesFin = new XYChart.Series<>();
        seriesFin.setName("Aujourd'hui");
        seriesFin.getData().add(new XYChart.Data<>("Force", derniere.getForce()));
        seriesFin.getData().add(new XYChart.Data<>("Endurance", derniere.getEndurance()));

        chartComparaison.getData().clear();
        chartComparaison.getData().addAll(seriesDebut, seriesFin);
    }

    private void afficherMessageAucuneDonnee() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aucune donnée");
        alert.setHeaderText("Pas encore de performances enregistrées");
        alert.setContentText("Demandez à votre coach d'enregistrer vos premières mesures !");
        alert.showAndWait();
    }

    @FXML
    private void handleDefinirObjectif() {
        if (!txtPoidsObjectif.getText().isEmpty()) {
            calculerObjectifPoids(Double.parseDouble(lblPoidsActuel.getText().replace(" kg", "")));
        }
    }

    @FXML
    private void handleRafraichir() {
        rafraichirDonnees();
    }

    private void rafraichirDonnees() {
        chargerDonnees();
    }

    @FXML
    private void handleExporter() {
        if (historiqueComplet == null || historiqueComplet.isEmpty()) {
            afficherMessageAucuneDonnee();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les performances");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("performances_" + membreConnecte.getNom() + ".csv");

        // Utiliser la fenêtre principale comme parent si possible, sinon null
        File file = fileChooser.showSaveDialog(lblPoidsActuel.getScene().getWindow());

        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // En-têtes CSV
                writer.println("Date;Poids (kg);IMC;Tour de taille (cm);Force;Endurance");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                for (Performance p : historiqueComplet) {
                    writer.printf("%s;%.1f;%.1f;%.1f;%.0f;%.0f%n",
                            p.getDateMesure().format(formatter),
                            p.getPoids(),
                            p.getImc(),
                            p.getTourTaille(),
                            p.getForce(),
                            p.getEndurance());
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export réussi");
                alert.setHeaderText(null);
                alert.setContentText("Les données ont été exportées avec succès vers " + file.getName());
                alert.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
                afficherErreur("Erreur d'export",
                        "Une erreur est survenue lors de l'export du fichier : " + e.getMessage());
            }
        }
    }

    // Classe interne pour l'affichage dans le tableau
    public static class PerformanceDisplay {
        private String date;
        private String poids;
        private String imc;
        private String tourTaille;
        private String force;
        private String endurance;

        public PerformanceDisplay(String date, String poids, String imc,
                String tourTaille, String force, String endurance) {
            this.date = date;
            this.poids = poids;
            this.imc = imc;
            this.tourTaille = tourTaille;
            this.force = force;
            this.endurance = endurance;
        }

        public String getDate() {
            return date;
        }

        public String getPoids() {
            return poids;
        }

        public String getImc() {
            return imc;
        }

        public String getTourTaille() {
            return tourTaille;
        }

        public String getForce() {
            return force;
        }

        public String getEndurance() {
            return endurance;
        }
    }
}