package com.sport.controller.admin;

import com.sport.model.Salle;
import com.sport.model.TypeSalle;
import com.sport.service.SalleService;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SalleFormController {

    @FXML private TextField txtNom;
    @FXML private TextField txtCapacite;
    @FXML private ComboBox<TypeSalle> cbType;

    private Salle salle;
    private final SalleService salleService = new SalleService();

    @FXML
    public void initialize() {
        cbType.getItems().setAll(TypeSalle.values());
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
        if (salle != null) {
            txtNom.setText(salle.getNom());
            txtCapacite.setText(String.valueOf(salle.getCapacite()));
            cbType.setValue(salle.getType());
        }
    }

    @FXML
    private void onSave() {
        if (salle == null) {
            salle = new Salle(
                txtNom.getText(),
                Integer.parseInt(txtCapacite.getText()),
                cbType.getValue()
            );
            salleService.ajouterSalle(salle);
        } else {
            salle.setNom(txtNom.getText());
            salle.setCapacite(Integer.parseInt(txtCapacite.getText()));
            salle.setType(cbType.getValue());
            salleService.modifierSalle(salle);
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
