package com.sport.controller.admin;

import java.time.LocalDate;
import java.util.List;

import com.sport.model.Abonnement;
import com.sport.model.Membre;
import com.sport.model.StatutAbonnement;
import com.sport.model.TypeAbonnement;
import com.sport.service.AbonnementService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddAbonnementFormController {

    @FXML
    private ComboBox<Membre> memberComboBox;
    @FXML
    private ComboBox<TypeAbonnement> typeComboBox;
    @FXML
    private ComboBox<StatutAbonnement> statutComboBox;
    @FXML
    private DatePicker dateDebutPicker;
    @FXML
    private DatePicker dateFinPicker;
    @FXML
    private CheckBox autoCheckBox;
    @FXML
    private TextField montantField;

    private AbonnementService abonnementService = new AbonnementService();

    public void setMembres(List<Membre> membres) {
        memberComboBox.setItems(FXCollections.observableArrayList(membres));
    }

    @FXML
    public void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList(TypeAbonnement.values()));
        statutComboBox.setItems(FXCollections.observableArrayList(StatutAbonnement.values()));
    }

    @FXML
    private void onAdd() {
        try {
            Membre membre = memberComboBox.getSelectionModel().getSelectedItem();
            TypeAbonnement type = typeComboBox.getSelectionModel().getSelectedItem();
            StatutAbonnement statut = statutComboBox.getSelectionModel().getSelectedItem();
            LocalDate dateDebut = dateDebutPicker.getValue();
            LocalDate dateFin = dateFinPicker.getValue();
            boolean auto = autoCheckBox.isSelected();
            double montant = Double.parseDouble(montantField.getText());

            if (membre == null || type == null || statut == null || dateDebut == null || dateFin == null) {
                new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs").show();
                return;
            }

            Abonnement ab = new Abonnement();
            ab.setMembre(membre);
            ab.setTypeAbonnement(type);
            ab.setStatutAbonnement(statut);
            ab.setAutorenouvellement(auto);
            ab.setMontant(montant);
            ab.setDateDebut(java.sql.Date.valueOf(dateDebut));
            ab.setDateFin(java.sql.Date.valueOf(dateFin));

            abonnementService.souscrireAbonnement(ab);

           // new Alert(Alert.AlertType.INFORMATION, "Abonnement ajouté avec succès !").show();

            // Close window
            Stage stage = (Stage) memberComboBox.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Montant invalide").show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout : " + e.getMessage()).show();
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) memberComboBox.getScene().getWindow();
        stage.close();
    }
}
