package com.sport.controller.admin;

import java.time.LocalDate;
import java.util.List;

import com.sport.model.Abonnement;
import com.sport.model.Membre;
import com.sport.model.StatutAbonnement;
import com.sport.model.TypeAbonnement;
import com.sport.repository.AbonnementRepository;
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
    private CheckBox autoCheckBox;
    @FXML
    private TextField montantField;

    private AbonnementService abonnementService = new AbonnementService();
    private AbonnementRepository abonnementRepository = new AbonnementRepository();

    public void setMembres(List<Membre> membres) {
        memberComboBox.setItems(FXCollections.observableArrayList(membres));
    }

    @FXML
    public void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList(TypeAbonnement.values()));
        statutComboBox.setItems(FXCollections.observableArrayList(StatutAbonnement.values()));
        statutComboBox.getSelectionModel().select(StatutAbonnement.ACTIF);

        // Show member full name in ComboBox
        memberComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Membre membre) {
                return membre != null ? membre.getNomComplet() : "";
            }

            @Override
            public Membre fromString(String string) {
                return null; // Not needed
            }
        });

        // Auto-calc montant when type changes
        // When type changes, recalc montant automatically
        typeComboBox.valueProperty().addListener((obs, oldType, newType) -> {
            if (newType != null) {
                montantField.setText(String.valueOf(abonnementRepository.calculerMontant(newType)));
            } else {
                montantField.setText("");
            }
        });
       
    }

    @FXML
    private void onAdd() {
        Membre membre = memberComboBox.getValue();
        TypeAbonnement type = typeComboBox.getValue();
        StatutAbonnement statut = statutComboBox.getValue();
        LocalDate dateDebut = dateDebutPicker.getValue();

        if (membre == null || type == null || statut == null || dateDebut == null) {
            new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs").show();
            return;
        }

        // Automatically calculate end date
        LocalDate dateFin = dateDebut.plusMonths(
            switch (type) {
                case MENSUEL -> 1;
                case TRIMESTRIEL -> 3;
                case ANNUEL -> 12;
            }
        );
         Membre selected = memberComboBox.getSelectionModel().getSelectedItem();
           if (selected == null) {
        System.out.println("Veuillez sélectionner un membre !");
        return;
    }
         
        try {
            Abonnement ab = new Abonnement();
            // ab.setMembre(membre);
            ab.setMembre(selected); // THIS IS CRUCIAL
            ab.setTypeAbonnement(type);
            ab.setStatutAbonnement(statut);
            ab.setAutorenouvellement(autoCheckBox.isSelected());
            ab.setMontant(abonnementRepository.calculerMontant(type));
            ab.setDateDebut(java.sql.Date.valueOf(dateDebut));
            ab.setDateFin(java.sql.Date.valueOf(dateFin));

            abonnementService.souscrireAbonnement(ab);

            Stage stage = (Stage) memberComboBox.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
           // new Alert(Alert.AlertType.ERROR,
           //         "Erreur lors de l'ajout de l'abonnement\n" + e.getMessage()).show();
                     e.printStackTrace();
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) memberComboBox.getScene().getWindow();
        stage.close();
    }

    
}
