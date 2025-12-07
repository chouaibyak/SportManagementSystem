package com.sport.controller;

import com.sport.model.Membre;
import com.sport.model.TypeObjectif;
import com.sport.model.TypePreference;
import com.sport.service.MembreService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class MemberController implements Initializable {

    // --- Lien avec le FXML ---
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTel;
    @FXML private TextField txtAdresse;
    @FXML private DatePicker dpDateNaissance;
    @FXML private ComboBox<TypeObjectif> comboObjectif;
    @FXML private ComboBox<TypePreference> comboPreference;

    @FXML private TableView<Membre> tableMembres;
    @FXML private TableColumn<Membre, Integer> colId;
    @FXML private TableColumn<Membre, String> colNom;
    @FXML private TableColumn<Membre, String> colPrenom;
    @FXML private TableColumn<Membre, String> colEmail;
    @FXML private TableColumn<Membre, TypeObjectif> colObjectif;
    @FXML private TableColumn<Membre, TypePreference> colPreference;

    //Service et Liste pour la table
    private MembreService membreService;
    private ObservableList<Membre> membreList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.membreService = new MembreService();
        
        // 1. Initialiser les ComboBox avec les Enums
        comboObjectif.setItems(FXCollections.observableArrayList(TypeObjectif.values()));
        comboPreference.setItems(FXCollections.observableArrayList(TypePreference.values()));

        // 2. Configurer les colonnes du tableau (doit correspondre aux getters de Membre)
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colObjectif.setCellValueFactory(new PropertyValueFactory<>("objectifSportif"));
        colPreference.setCellValueFactory(new PropertyValueFactory<>("preferences"));

        // 3. Charger les données
        chargerDonnees();

        // 4. Ajouter un écouteur : Quand on clique sur une ligne, on remplit le formulaire
        tableMembres.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                afficherMembre(newSelection);
            }
        });
    }

    private void chargerDonnees() {
        // Convertit la liste normale du service en ObservableList pour JavaFX
        membreList = FXCollections.observableArrayList(membreService.recupererTousLesMembres());
        tableMembres.setItems(membreList);
    }

    @FXML
    private void handleAjouter() {
        try {
            Membre m = new Membre(
                txtNom.getText(),
                txtPrenom.getText(),
                (dpDateNaissance.getValue() != null) ? dpDateNaissance.getValue().toString() : "",
                txtEmail.getText(),
                txtTel.getText(),
                txtAdresse.getText(),
                comboObjectif.getValue(),
                comboPreference.getValue()
            );

            membreService.creerMembre(m);
            chargerDonnees(); // Rafraichir le tableau
            handleVider(); // Vider les champs
        } catch (Exception e) {
            afficherAlerte("Erreur", "Impossible d'ajouter : " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimer() {
        Membre selected = tableMembres.getSelectionModel().getSelectedItem();
        if (selected != null) {
            membreService.supprimerMembre(selected.getId());
            chargerDonnees();
            handleVider();
        } else {
            afficherAlerte("Attention", "Veuillez sélectionner un membre à supprimer.");
        }
    }

    @FXML
    private void handleModifier() {
         Membre selected = tableMembres.getSelectionModel().getSelectedItem();
         if (selected != null) {
             // Mise à jour des champs de l'objet sélectionné
             selected.setNom(txtNom.getText());
             selected.setPrenom(txtPrenom.getText());
             selected.setEmail(txtEmail.getText());
             selected.setTelephone(txtTel.getText());
             selected.setAdresse(txtAdresse.getText());
             selected.setObjectifSportif(comboObjectif.getValue());
             selected.setPreferences(comboPreference.getValue());
             // Date...

             membreService.mettreAJourMembre(selected);
             chargerDonnees();
             tableMembres.refresh();
         }
    }

    @FXML
    private void handleVider() {
        txtNom.clear();
        txtPrenom.clear();
        txtEmail.clear();
        txtTel.clear();
        txtAdresse.clear();
        dpDateNaissance.setValue(null);
        comboObjectif.setValue(null);
        comboPreference.setValue(null);
    }

    private void afficherMembre(Membre m) {
        txtNom.setText(m.getNom());
        txtPrenom.setText(m.getPrenom());
        txtEmail.setText(m.getEmail());
        txtTel.setText(m.getTelephone());
        txtAdresse.setText(m.getAdresse());
        comboObjectif.setValue(m.getObjectifSportif());
        comboPreference.setValue(m.getPreferences());
        // Pour la date, il faudrait convertir String -> LocalDate si votre modèle utilise String
    }

    private void afficherAlerte(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}