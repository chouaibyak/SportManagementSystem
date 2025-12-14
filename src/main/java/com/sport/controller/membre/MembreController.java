package com.sport.controller.membre;

import com.sport.model.Membre;
import com.sport.model.TypeObjectif;
import com.sport.service.MembreService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MembreController {

    @FXML private TableView<Membre> tableMembres;
    @FXML private TableColumn<Membre, Integer> colId;
    @FXML private TableColumn<Membre, String> colNom;
    @FXML private TableColumn<Membre, String> colPrenom;
    @FXML private TableColumn<Membre, String> colEmail;
    @FXML private TableColumn<Membre, TypeObjectif> colObjectif;

    // Appel à ton service existant
    private MembreService membreService = new MembreService();
    private ObservableList<Membre> membreList;

    @FXML
    public void initialize() {
        // 1. Configurer les colonnes (le nom entre guillemets doit correspondre EXACTEMENT au getter du modèle)
        // ex: "nom" cherchera getNom()
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colObjectif.setCellValueFactory(new PropertyValueFactory<>("objectifSportif"));

        // 2. Charger les données
        chargerDonnees();
    }

    private void chargerDonnees() {
        // Convertir la List normale en ObservableList pour JavaFX
        membreList = FXCollections.observableArrayList(membreService.recupererTousLesMembres());
        tableMembres.setItems(membreList);
    }

    @FXML
    private void handleAjouter() {
        // Ici, tu devras ouvrir une nouvelle fenêtre ou un dialogue pour saisir les infos
        System.out.println("Ouvrir fenêtre ajout...");
    }

    @FXML
    private void handleSupprimer() {
        Membre selection = tableMembres.getSelectionModel().getSelectedItem();
        if (selection != null) {
            // Appel au service pour supprimer
            membreService.supprimerMembre(selection.getId());
            // Mise à jour visuelle
            membreList.remove(selection);
        } else {
            afficherAlerte("Erreur", "Veuillez sélectionner un membre à supprimer.");
        }
    }

    private void afficherAlerte(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }
}