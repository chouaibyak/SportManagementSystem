package com.sport.controller.coach;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// 1. CORRECTION DE L'IMPORT (Supprime com.google.protobuf... et utilise javafx)
import javafx.scene.control.Label; 

import com.sport.model.Coach;
import com.sport.model.Salle;
import com.sport.model.Seance;
import com.sport.model.SeanceCollective;
import com.sport.model.SeanceIndividuelle;
import com.sport.model.TypeCours;
import com.sport.model.TypeSeance;
import com.sport.model.Utilisateur;
import com.sport.repository.SeanceCollectiveRepository;
import com.sport.repository.SeanceIndividuelleRepository;
import com.sport.service.SalleService;
import com.sport.service.SeanceCollectiveService;
import com.sport.service.SeanceIndividuelleService;
import com.sport.utils.UserSession;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    
    // Ces champs doivent exister dans ton FXML avec les mêmes fx:id
    @FXML private Spinner<Double> spTarif; 
    @FXML private TextArea txtNotes;       
    @FXML private Label lblTarif;          
    @FXML private Label lblNotes;        

    private Coach coach;
    private Seance seance;

    private final SeanceCollectiveRepository seanceCollective =
            new SeanceCollectiveRepository();
    private final SeanceIndividuelleRepository seanceIndividuelle =
            new SeanceIndividuelleRepository();
    private final SeanceCollectiveService collectiveService =
            new SeanceCollectiveService(seanceCollective);
    private final SeanceIndividuelleService indivService =
            new SeanceIndividuelleService(seanceIndividuelle);
    private final SalleService salleService =
            new SalleService();

    @FXML
    public void initialize() {
        // Coach connecté
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        if (user instanceof Coach coach1) {
            coach = coach1;
        }

        // Types de séance
        cbType.setItems(FXCollections.observableArrayList("Collective", "Individuelle"));
        
        // Types de cours
        cbTypeCours.setItems(FXCollections.observableArrayList(TypeCours.values()));
        cbTypeCours.getSelectionModel().selectFirst();

        // Spinners
        spHeure.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 10));
        spMinute.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        spDuree.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 180, 60));
        spMaxMembres.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10));
        
        // Configuration du Spinner Tarif
        spTarif.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 500.0, 50.0, 5.0));

        // 2. MODIFICATION ICI : Gestion de l'affichage (Cacher/Montrer les champs)
        cbType.valueProperty().addListener((obs, o, n) -> {
            boolean isCollective = "Collective".equals(n);
            
            // Si Collective : On active MaxMembres, on CACHE Tarif et Notes
            spMaxMembres.setDisable(!isCollective);
            
            // Si Individuelle : On montre Tarif et Notes
            spTarif.setVisible(!isCollective);
            txtNotes.setVisible(!isCollective);
            if(lblTarif != null) lblTarif.setVisible(!isCollective);
            if(lblNotes != null) lblNotes.setVisible(!isCollective);
        });

        // Déclenche l'écouteur pour mettre l'état initial correct
        cbType.getSelectionModel().selectFirst(); 

        // Salles
        cbSalle.setItems(FXCollections.observableArrayList(salleService.getToutesLesSalles()));

        // Boutons
        btnSave.setOnAction(e -> enregistrer());
        btnCancel.setOnAction(e -> fermerFenetre());
    }

    /* ===============================
       MODE ÉDITION
       =============================== */
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
        } else if (s instanceof SeanceIndividuelle si) { 
            // 3. MODIFICATION ICI : Charger les données individuelles existantes
            cbType.setValue("Individuelle");
            spMaxMembres.setDisable(true);
            
            // Charger le tarif
            if (si.getTarif() != null) {
                spTarif.getValueFactory().setValue(si.getTarif());
            }
            // Charger les notes
            txtNotes.setText(si.getNotesCoach());
        }
    }

    /* ===============================
       ENREGISTREMENT
       =============================== */
    private void enregistrer() {
        if (txtNom.getText().isEmpty()
                || dpDate.getValue() == null
                || cbTypeCours.getValue() == null
                || cbSalle.getValue() == null) {
            showAlert("Erreur", "Veuillez remplir tous les champs obligatoires.");
            return;
        }

        LocalDate date = dpDate.getValue();
        LocalTime time = LocalTime.of(spHeure.getValue(), spMinute.getValue());
        LocalDateTime dateHeure = LocalDateTime.of(date, time);

        // AJOUT
        if (seance == null) {
            if ("Collective".equals(cbType.getValue())) {
                SeanceCollective sc = new SeanceCollective();
                // ... (Propriétés communes)
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
                // ... (Propriétés communes)
                si.setNom(txtNom.getText());
                si.setDateHeure(dateHeure);
                si.setDuree(spDuree.getValue());
                si.setTypeCours(cbTypeCours.getValue());
                si.setSalle(cbSalle.getValue());
                si.setEntraineur(coach);
                si.setTypeSeance(TypeSeance.INDIVIDUELLE);

                // 4. MODIFICATION ICI : Ajouter les propriétés spécifiques !
                // C'est ce qui manquait pour écrire dans la table seanceindividuelle
                si.setTarif(spTarif.getValue()); 
                si.setNotesCoach(txtNotes.getText());

                indivService.ajouterSeance(si);
                seance = si;
            }
        } 
        // NOTE: Si tu gères la modification (update), tu devras aussi ajouter les setters ici dans un bloc 'else'

        fermerFenetre();
    }

    // ... Le reste (showAlert, fermerFenetre) ne change pas ...
    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void fermerFenetre() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
}