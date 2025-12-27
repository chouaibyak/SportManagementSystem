package com.sport.controller.coach;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.sport.model.Coach;
import com.sport.model.Salle;
import com.sport.model.Seance;
import com.sport.model.SeanceCollective;
import com.sport.model.SeanceIndividuelle;
import com.sport.model.TypeCours;
import com.sport.model.TypeSeance;
import com.sport.model.Utilisateur;
import com.sport.repository.SalleRepository;
import com.sport.repository.SeanceCollectiveRepository;
import com.sport.repository.SeanceIndividuelleRepository;
import com.sport.utils.UserSession;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CoachSeanceFormController {

    @FXML private TextField txtNom;
    @FXML private ComboBox<String> cbType;           // Collective / Individuelle
    @FXML private ComboBox<TypeCours> cbTypeCours;   // YOGA, MUSCULATION, CARDIO, PILATES
    @FXML private ComboBox<Salle> cbSalle;           // Salle disponible
    @FXML private DatePicker dpDate;
    @FXML private Spinner<Integer> spHeure;
    @FXML private Spinner<Integer> spMinute;
    @FXML private Spinner<Integer> spDuree;
    @FXML private Spinner<Integer> spMaxMembres;

    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private Coach coach;
    private Seance seance; // null = ajout | not null = modification

    private final SeanceCollectiveRepository collectiveRepo = new SeanceCollectiveRepository();
    private final SeanceIndividuelleRepository indivRepo = new SeanceIndividuelleRepository();
    private final SalleRepository salleRepo = new SalleRepository();

    // =============================
    // INITIALIZE
    // =============================
    @FXML
    public void initialize() {

        // Coach connecté
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        if (user instanceof Coach) coach = (Coach) user;

        // Type séance
        cbType.setItems(FXCollections.observableArrayList("Collective", "Individuelle"));
        cbType.getSelectionModel().selectFirst();

        // Type de cours
        cbTypeCours.setItems(FXCollections.observableArrayList(TypeCours.values()));
        cbTypeCours.getSelectionModel().selectFirst();

        // Spinners
        spHeure.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 10));
        spMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        spDuree.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 180, 60));
        spMaxMembres.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10));

        // Activer / désactiver max membres selon type
        cbType.valueProperty().addListener((obs, oldV, newV) -> spMaxMembres.setDisable(!"Collective".equals(newV)));

        // Charger les salles disponibles
        chargerSallesDisponibles();

        // Personnalisation affichage salle
        cbSalle.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Salle item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
        cbSalle.setButtonCell(cbSalle.getCellFactory().call(null));

        // Boutons
        btnSave.setOnAction(e -> enregistrer());
        btnCancel.setOnAction(e -> fermerFenetre());
    }

    // =============================
    // MODE MODIFICATION
    // =============================
    public void setSeance(Seance s) {
        this.seance = s;

        txtNom.setText(s.getNom());
        dpDate.setValue(s.getDateHeure().toLocalDate());
        spHeure.getValueFactory().setValue(s.getDateHeure().getHour());
        spMinute.getValueFactory().setValue(s.getDateHeure().getMinute());
        spDuree.getValueFactory().setValue(s.getDuree());

        if (s.getTypeCours() != null) cbTypeCours.setValue(s.getTypeCours());

        // Interdire changement type en modification
        cbType.setDisable(true);

        if (s instanceof SeanceCollective sc) {
            cbType.setValue("Collective");
            spMaxMembres.getValueFactory().setValue(sc.getCapaciteMax());
            spMaxMembres.setDisable(false);
            cbSalle.setValue(sc.getSalle());
        } else {
            cbType.setValue("Individuelle");
            spMaxMembres.setDisable(true);
            cbSalle.setValue(s.getSalle());
        }
    }

    // =============================
    // SAVE
    // =============================
    private void enregistrer() {

        if (txtNom.getText().isEmpty() || dpDate.getValue() == null || cbTypeCours.getValue() == null || cbSalle.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        LocalDate date = dpDate.getValue();
        LocalTime time = LocalTime.of(spHeure.getValue(), spMinute.getValue());
        LocalDateTime dateHeure = LocalDateTime.of(date, time);

        // Vérifier disponibilité salle
        Salle salleChoisie = cbSalle.getValue();
        boolean dispo = salleRepo.verifierDisponibiliteSalle(salleChoisie.getId(), dateHeure);
        if (!dispo && seance == null) {
            showAlert("Erreur", "La salle sélectionnée n'est pas disponible à cette date et heure.");
            return;
        }

        String nom = txtNom.getText();
        int duree = spDuree.getValue();
        TypeCours typeCours = cbTypeCours.getValue();

        // =============================
        // AJOUT
        // =============================
        if (seance == null) {
            if ("Collective".equals(cbType.getValue())) {
                SeanceCollective sc = new SeanceCollective();
                sc.setNom(nom);
                sc.setDateHeure(dateHeure);
                sc.setDuree(duree);
                sc.setEntraineur(coach);
                sc.setCapaciteMax(spMaxMembres.getValue());
                sc.setPlacesDisponibles(spMaxMembres.getValue());
                sc.setTypeCours(typeCours);
                sc.setTypeSeance(TypeSeance.COLLECTIVE);
                sc.setSalle(salleChoisie);

                collectiveRepo.ajouter(sc);
            } else {
                SeanceIndividuelle si = new SeanceIndividuelle();
                si.setNom(nom);
                si.setDateHeure(dateHeure);
                si.setDuree(duree);
                si.setEntraineur(coach);
                si.setTypeCours(typeCours);
                si.setTypeSeance(TypeSeance.INDIVIDUELLE);
                si.setSalle(salleChoisie);

                indivRepo.ajouter(si);
            }
        } else { // =============================
                 // MODIFICATION
                 // =============================
            seance.setNom(nom);
            seance.setDateHeure(dateHeure);
            seance.setDuree(duree);
            seance.setTypeCours(typeCours);
            seance.setSalle(salleChoisie);

            if (seance instanceof SeanceCollective sc) {
                sc.setCapaciteMax(spMaxMembres.getValue());
                collectiveRepo.update(sc);
            } else {
                indivRepo.update((SeanceIndividuelle) seance);
            }
        }

        fermerFenetre();
    }

    // =============================
    // CHARGER SALLES DISPONIBLES
    // =============================
    private void chargerSallesDisponibles() {
        List<Salle> salles = salleRepo.listerSalles();
        cbSalle.setItems(FXCollections.observableArrayList(salles));
    }

    // =============================
    // UTILS
    // =============================
    private void fermerFenetre() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}

