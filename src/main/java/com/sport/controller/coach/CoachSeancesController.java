package com.sport.controller.coach;

import com.sport.model.*;
import com.sport.repository.SeanceCollectiveRepository;
import com.sport.repository.SeanceIndividuelleRepository;
import com.sport.utils.UserSession;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CoachSeancesController {

    @FXML private TableView<Seance> tableSeances;
    @FXML private TableColumn<Seance, String> colNom;
    @FXML private TableColumn<Seance, String> colType;
    @FXML private TableColumn<Seance, String> colDateHeure;
    @FXML private TableColumn<Seance, String> colDuree;
    @FXML private TableColumn<Seance, String> colMembres;

    @FXML private Button btnAddSeance;
    @FXML private Button btnEditSeance;
    @FXML private Button btnDeleteSeance;
    @FXML private Button btnRefresh;

    private Coach coach;

    private final SeanceCollectiveRepository collectiveRepo = new SeanceCollectiveRepository();
    private final SeanceIndividuelleRepository indivRepo = new SeanceIndividuelleRepository();

    private ObservableList<Seance> seancesData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Coach connecté
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        if (user instanceof Coach) {
            coach = (Coach) user;
        }

        // Colonnes
        colNom.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNom())
        );

        colType.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue() instanceof SeanceCollective ? "Collective" : "Individuelle"
                )
        );

        colDateHeure.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDateHeure()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                )
        );

        colDuree.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDuree() + " min")
        );

        colMembres.setCellValueFactory(data -> {
            if (data.getValue() instanceof SeanceCollective sc) {
                return new SimpleStringProperty(
                        sc.getListeMembers().size() + "/" + sc.getCapaciteMax()
                );
            }
            return new SimpleStringProperty("-");
        });

        // Désactiver boutons au départ
        btnEditSeance.setDisable(true);
        btnDeleteSeance.setDisable(true);

        tableSeances.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean selected = newVal != null;
            btnEditSeance.setDisable(!selected);
            btnDeleteSeance.setDisable(!selected);
        });

        chargerSeances();

        btnRefresh.setOnAction(e -> chargerSeances());
        btnAddSeance.setOnAction(e -> ouvrirPopupAjout(null));
        btnEditSeance.setOnAction(e -> ouvrirPopupAjout(tableSeances.getSelectionModel().getSelectedItem()));
        btnDeleteSeance.setOnAction(e -> supprimerSeance());
    }

    private void chargerSeances() {
        seancesData.clear();

        List<SeanceCollective> collectives = collectiveRepo.getAll().stream()
                .filter(s -> s.getEntraineur().getId() == coach.getId())
                .collect(Collectors.toList());

        List<SeanceIndividuelle> individuelles = indivRepo.getAll().stream()
                .filter(s -> s.getEntraineur().getId() == coach.getId())
                .collect(Collectors.toList());

        seancesData.addAll(collectives);
        seancesData.addAll(individuelles);

        tableSeances.setItems(seancesData);
    }

    private void supprimerSeance() {
        Seance selected = tableSeances.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la séance");
        alert.setContentText("Voulez-vous vraiment supprimer cette séance ?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (selected instanceof SeanceCollective) {
                collectiveRepo.delete(selected.getId());
            } else {
                indivRepo.delete(selected.getId());
            }
            chargerSeances();
        }
    }

  private void ouvrirPopupAjout(Seance seance) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/coach/coach_seance_form.fxml"));
        VBox root = loader.load(); // VBox men FXML

        CoachSeanceFormController controller = loader.getController();
        if (seance != null) {
            controller.setSeance(seance);
        }

        Stage stage = new Stage();
        stage.setTitle(seance == null ? "Ajouter une séance" : "Modifier la séance");
        stage.setScene(new Scene(root));
        stage.initOwner(tableSeances.getScene().getWindow());
        stage.showAndWait();

        chargerSeances();
    } catch (Exception e) {
        e.printStackTrace();
    }
}


}


