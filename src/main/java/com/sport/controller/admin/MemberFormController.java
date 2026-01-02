package com.sport.controller.admin;

import com.sport.model.Membre;
import com.sport.model.TypeObjectif;
import com.sport.model.TypePreference;
import com.sport.service.MembreService;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class MemberFormController {

    @FXML private TextField tfNom;
    @FXML private TextField tfPrenom;
    @FXML private TextField tfEmail;
    @FXML private TextField tfTelephone;
    @FXML private ComboBox<TypeObjectif> cbObjectif;
    @FXML private ComboBox<TypePreference> cbPreference;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private final MembreService membreService = new MembreService();
    private Membre membre = new Membre(); // new member by default

    @FXML
    public void initialize() {
        cbObjectif.getItems().setAll(TypeObjectif.values());
        cbPreference.getItems().setAll(TypePreference.values());

        btnSave.setOnAction(e -> saveMember());
        btnCancel.setOnAction(e -> btnCancel.getScene().getWindow().hide());
    }

    private void saveMember() {
        membre.setNom(tfNom.getText());
        membre.setPrenom(tfPrenom.getText());
        membre.setEmail(tfEmail.getText());
        membre.setTelephone(tfTelephone.getText());
        membre.setObjectifSportif(cbObjectif.getValue());
        membre.setPreferences(cbPreference.getValue());

        // REQUIRED FIELDS
        membre.setMotDePasse("123456"); // default password
        membre.setRole("MEMBRE");       // role column in UTILISATEUR


        membreService.creerMembre(membre);
        btnSave.getScene().getWindow().hide();
    }

    public void setMember(Membre member) {
        this.membre = member;

        // Fill fields with existing member data
        tfNom.setText(member.getNom());
        tfPrenom.setText(member.getPrenom());
        tfEmail.setText(member.getEmail());
        tfTelephone.setText(member.getTelephone());
        cbObjectif.setValue(member.getObjectifSportif());
        cbPreference.setValue(member.getPreferences());
       
    }

}
