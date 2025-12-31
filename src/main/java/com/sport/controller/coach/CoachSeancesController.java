package com.sport.controller.coach;

import com.sport.model.*;
import com.sport.service.MembreService;
import com.sport.service.SeanceCollectiveService;
import com.sport.service.SeanceIndividuelleService;
import com.sport.utils.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CoachSeancesController {

    @FXML private TableView<Seance> tableSeances;
    @FXML private TableColumn<Seance, String> colNom;
    @FXML private TableColumn<Seance, String> colType;
    @FXML private TableColumn<Seance, String> colDateHeure;
    @FXML private TableColumn<Seance, String> colDuree;
    @FXML private TableColumn<Seance, String> colMembres;
    @FXML private TableColumn<Seance, Void> colActions;
    @FXML private TableColumn<Seance, Void> colVoirMembres;

    @FXML private Button btnAddSeance;
    @FXML private Button btnRefresh;
    @FXML private ComboBox<String> comboFiltre;

    private Coach coach;
    private final SeanceCollectiveService collectiveService = new SeanceCollectiveService(new com.sport.repository.SeanceCollectiveRepository());
    private final SeanceIndividuelleService indivService = new SeanceIndividuelleService(new com.sport.repository.SeanceIndividuelleRepository());
    private final ObservableList<Seance> seancesData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Coach connecté
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        if (user instanceof Coach) coach = (Coach) user;

        // Colonnes TableView
        colNom.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNom()));
        colType.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue() instanceof SeanceCollective ? "Collective" : "Individuelle"
        ));
        colDateHeure.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getDateHeure() != null ?
                        data.getValue().getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "-"
        ));
        colDuree.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDuree() + " min"));
        colMembres.setCellValueFactory(data -> {
            if (data.getValue() instanceof SeanceCollective sc) {
                return new SimpleStringProperty(sc.getListeMembers().size() + "/" + sc.getCapaciteMax());
            } else if (data.getValue() instanceof SeanceIndividuelle si) {
                return new SimpleStringProperty(si.getMembre() != null
                        ? si.getMembre().getNom() + " " + si.getMembre().getPrenom() : "—");
            }
            return new SimpleStringProperty("-");
        });

        // Colonne Actions
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("\u270E");   // ✎
            private final Button btnDelete = new Button("\uD83D\uDDD1"); // 🗑
            private final Button btnDetails = new Button("\uD83D\uDC41"); // 👁
            private final HBox container = new HBox(5, btnDetails, btnEdit, btnDelete);

            {
                btnEdit.getStyleClass().addAll("button", "edit");
                btnDelete.getStyleClass().addAll("button", "delete");
                btnDetails.getStyleClass().addAll("button", "view");

                btnEdit.setOnAction(e -> ouvrirPopupAjout(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> {
                    tableSeances.getSelectionModel().select(getTableView().getItems().get(getIndex()));
                    supprimerSeance();
                });
                btnDetails.setOnAction(e -> {
                    Seance s = getTableView().getItems().get(getIndex());
                    ouvrirDetailsSeance(s);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });

        // Colonne Voir Membres
        colVoirMembres.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("\uD83D\uDC65"); // 👥
            {
                btn.getStyleClass().addAll("button", "view");
                btn.setOnAction(e -> {
                    Seance s = getTableView().getItems().get(getIndex());
                    if (s instanceof SeanceCollective sc) ouvrirSuiviMembres(sc);
                    else if (s instanceof SeanceIndividuelle si) ouvrirDetailsIndividuelle(si);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        // Boutons du haut
        btnAddSeance.setOnAction(e -> ouvrirPopupAjout(null));
        btnRefresh.setOnAction(e -> chargerSeances());

        // --- Filtre ComboBox ---
        comboFiltre.setItems(FXCollections.observableArrayList("Tout", "Collective", "Individuelle"));
        comboFiltre.getSelectionModel().select("Tout");
        comboFiltre.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> filtrerSeances(newVal));

        // Charger toutes les séances
        chargerSeances();
    }

    private void chargerSeances() {
        seancesData.clear();
        List<SeanceCollective> collectives = collectiveService.getSeancesByCoach(coach.getId());
        List<SeanceIndividuelle> individuelles = indivService.getSeancesByCoach(coach.getId());
        seancesData.addAll(collectives);
        seancesData.addAll(individuelles);

        tableSeances.setItems(seancesData);
        tableSeances.refresh();

        // Appliquer filtre si sélectionnée
        String selected = comboFiltre.getSelectionModel().getSelectedItem();
        filtrerSeances(selected);
    }

    private void filtrerSeances(String type) {
        if (type == null || type.equals("Tout")) {
            tableSeances.setItems(seancesData);
        } else {
            ObservableList<Seance> filtered = FXCollections.observableArrayList();
            for (Seance s : seancesData) {
                if (type.equals("Collective") && s instanceof SeanceCollective) filtered.add(s);
                else if (type.equals("Individuelle") && s instanceof SeanceIndividuelle) filtered.add(s);
            }
            tableSeances.setItems(filtered);
        }
        tableSeances.refresh();
    }

    private void supprimerSeance() {
        Seance selected = tableSeances.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la séance");
        alert.setContentText("Voulez-vous vraiment supprimer cette séance ?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            boolean success = false;
            if (selected instanceof SeanceCollective sc) success = collectiveService.delete(sc.getId());
            else if (selected instanceof SeanceIndividuelle si) success = indivService.delete(si.getId());
            if (success) chargerSeances();
        }
    }

    private void ouvrirPopupAjout(Seance seance) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/coach/coach_seance_form.fxml"));
            VBox root = loader.load();
            CoachSeanceFormController controller = loader.getController();
            if (seance != null) controller.setSeance(seance);

            Stage stage = new Stage();
            stage.setTitle(seance == null ? "Ajouter une séance" : "Modifier la séance");
            stage.setScene(new Scene(root));
            stage.initOwner(tableSeances.getScene().getWindow());
            stage.setOnHiding(event -> {
                Seance result = controller.getSeanceCreeeOuModifiee();
                if (result != null && !seancesData.contains(result)) seancesData.add(result);
                tableSeances.refresh();
            });
            stage.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void ouvrirDetailsSeance(Seance seance) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/coach/coach_seance_details.fxml"));
            Parent root = loader.load();
            CoachSeanceDetailsController controller = loader.getController();
            controller.setSeance(seance);

            Stage stage = new Stage();
            stage.setTitle("Détails de la séance");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setWidth(650); stage.setHeight(600);
            stage.setMinWidth(500); stage.setMinHeight(600);
            stage.initOwner(tableSeances.getScene().getWindow());
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void ouvrirSuiviMembres(SeanceCollective sc) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/coach/coach_seance_membres.fxml"));
            VBox root = loader.load();
            CoachSeanceMembresController controller = loader.getController();
            controller.setSeanceCollective(sc);

            Stage stage = new Stage();
            stage.setTitle("Membres - " + sc.getNom());
            stage.setScene(new Scene(root));
            stage.initOwner(tableSeances.getScene().getWindow());
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void ouvrirDetailsIndividuelle(SeanceIndividuelle si) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/coach/coach_seance_indiv_details.fxml"));
            Parent root = loader.load();
            CoachSeanceIndivDetailsController controller = loader.getController();
            controller.setSeance(si);
            controller.setOnSeanceUpdated(this::chargerSeances);

            Stage stage = new Stage();
            stage.setTitle("Séance individuelle");
            stage.setScene(new Scene(root));
            stage.initOwner(tableSeances.getScene().getWindow());
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void assignerMembre(SeanceIndividuelle si) {
        ComboBox<Membre> cbMembres = new ComboBox<>();
        cbMembres.setItems(FXCollections.observableArrayList(new MembreService().getAllMembres()));

        cbMembres.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Membre item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom() + " " + item.getPrenom());
            }
        });
        cbMembres.setButtonCell(cbMembres.getCellFactory().call(null));

        Button btnValider = new Button("Valider");
        btnValider.getStyleClass().add("btn-primary");
        btnValider.setOnAction(e -> {
            if (cbMembres.getValue() == null) return;
            indivService.assignerMembre(si.getId(), cbMembres.getValue());
            tableSeances.refresh();
            ((Stage) btnValider.getScene().getWindow()).close();
        });

        VBox root = new VBox(10, cbMembres, btnValider);
        root.setStyle("-fx-padding: 15;");
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Assigner un membre");
        stage.show();
    }
}
