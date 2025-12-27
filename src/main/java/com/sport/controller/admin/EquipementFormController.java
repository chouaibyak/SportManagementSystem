package com.sport.controller.admin;

import com.sport.model.Equipement;
import com.sport.model.EtatEquipement;
import com.sport.model.TypeEquipement;
import com.sport.service.EquipementService;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EquipementFormController {

    @FXML private TextField txtNom;
    @FXML private ComboBox<TypeEquipement> cbType;
    @FXML private ComboBox<EtatEquipement> cbEtat;
    @FXML private DatePicker dpDateAchat;

    private Equipement equipement;
    private final EquipementService equipementService = new EquipementService(new com.sport.repository.EquipementRepository());

    @FXML
    public void initialize() {
        cbType.getItems().setAll(TypeEquipement.values());
        cbEtat.getItems().setAll(EtatEquipement.values());
    }

    public void setEquipement(Equipement eq) {
        this.equipement = eq;
        if (eq != null) {
            txtNom.setText(eq.getNom());
            cbType.setValue(eq.getType());
            cbEtat.setValue(eq.getEtat());
            dpDateAchat.setValue(new java.sql.Date(eq.getDateAchat().getTime()).toLocalDate());
        }
    }

    @FXML
    private void onSave() {
        if (equipement == null) {
            equipement = new Equipement(
                txtNom.getText(),
                cbType.getValue(),
                cbEtat.getValue(),
                java.sql.Date.valueOf(dpDateAchat.getValue())
            );
            equipementService.ajouterEquipement(equipement);
        } else {
            equipement.setNom(txtNom.getText());
            equipement.setType(cbType.getValue());
            equipement.setEtat(cbEtat.getValue());
            equipement.setDateAchat(java.sql.Date.valueOf(dpDateAchat.getValue()));
            equipementService.modifierEquipement(equipement);
        }
        close();
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        ((Stage) txtNom.getScene().getWindow()).close();
    }
}
