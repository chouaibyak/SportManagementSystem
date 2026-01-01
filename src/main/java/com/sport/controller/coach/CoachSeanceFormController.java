package com.sport.controller.coach;

import com.sport.model.*;
import com.sport.service.SeanceCollectiveService;
import com.sport.service.SeanceIndividuelleService;
import com.sport.service.SalleService;
import com.sport.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class CoachSeanceFormController {

    @FXML private TextField txtNom;
    @FXML private ComboBox<String> cbType;
    @FXML private ComboBox<TypeCours> cbTypeCours;
    @FXML private ComboBox<Salle> cbSalle;
    @FXML private DatePicker dpDate;
    @FXML private Spinner<Integer> spHeure;
    @FXML private Spinner<Integer> spMinute;
    @FXML private Spinner<Integer> spDuree;
    @FXML private Spinner<Integer> spMaxMembres;

    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private Coach coach;
    private Seance seance;

    private final SeanceCollectiveService collectiveService = new SeanceCollectiveService(new com.sport.repository.SeanceCollectiveRepository());
    private final SeanceIndividuelleService indivService = new SeanceIndividuelleService(new com.sport.repository.SeanceIndividuelleRepository());
    private final SalleService salleService = new SalleService(new com.sport.repository.SalleRepository());

    @FXML
    public void initialize() {

        // Coach connecté
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        if (user instanceof Coach) coach = (Coach) user;

        // Types de séance
        cbType.setItems(FXCollections.observableArrayList("Collective", "Individuelle"));
        cbType.getSelectionModel().selectFirst();

        // Types de cours
        cbTypeCours.setItems(FXCollections.observableArrayList(TypeCours.values()));
        cbTypeCours.getSelectionModel().selectFirst();

        // Spinners
        spHeure.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 10));
        spMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        spDuree.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 180, 60));
        spMaxMembres.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10));

        // Activer/désactiver max membres selon type
        cbType.valueProperty().addListener((obs, o, n) ->
                spMaxMembres.setDisable(!"Collective".equals(n))
        );

        // Lister salles depuis le service
        cbSalle.setItems(FXCollections.observableArrayList(salleService.getToutesLesSalles()));

        // Actions boutons
        btnSave.setOnAction(e -> enregistrer());
        btnCancel.setOnAction(e -> fermerFenetre());
    }

    public Seance getSeanceCreeeOuModifiee() {
        return seance;
    }

    public void setSeance(Seance s) {
        this.seance = s;

        txtNom.setText(s.getNom());
        dpDate.setValue(s.getDateHeure().toLocalDate());
        spHeure.getValueFactory().setValue(s.getDateHeure().getHour());
        spMinute.getValueFactory().setValue(s.getDateHeure().getMinute());
        spDuree.getValueFactory().setValue(s.getDuree());
        cbTypeCours.setValue(s.getTypeCours());
        cbSalle.setValue(s.getSalle());

        cbType.setDisable(true);

        if (s instanceof SeanceCollective sc) {
            cbType.setValue("Collective");
            spMaxMembres.getValueFactory().setValue(sc.getCapaciteMax());
        } else {
            cbType.setValue("Individuelle");
            spMaxMembres.setDisable(true);
        }
    }

    private void enregistrer() {
        LocalDateTime dateHeure = LocalDateTime.of(
                dpDate.getValue(),
                LocalTime.of(spHeure.getValue(), spMinute.getValue())
        );

        if (seance == null) {
            if ("Collective".equals(cbType.getValue())) {
                SeanceCollective sc = new SeanceCollective();
                sc.setNom(txtNom.getText());
                sc.setDateHeure(dateHeure);
                sc.setDuree(spDuree.getValue());
                sc.setCapaciteMax(spMaxMembres.getValue());
                sc.setPlacesDisponibles(spMaxMembres.getValue());
                sc.setTypeCours(cbTypeCours.getValue());
                sc.setSalle(cbSalle.getValue());
                sc.setEntraineur(coach);
                sc.setTypeSeance(TypeSeance.COLLECTIVE);
                collectiveService.ajouterSeance(sc);
                seance = sc;
            } else {
                SeanceIndividuelle si = new SeanceIndividuelle();
                si.setNom(txtNom.getText());
                si.setDateHeure(dateHeure);
                si.setDuree(spDuree.getValue());
                si.setTypeCours(cbTypeCours.getValue());
                si.setSalle(cbSalle.getValue());
                si.setEntraineur(coach);
                si.setTypeSeance(TypeSeance.INDIVIDUELLE);
                indivService.ajouterSeance(si);
                seance = si;
            }
        }
        fermerFenetre();
    }

    private void fermerFenetre() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
}
