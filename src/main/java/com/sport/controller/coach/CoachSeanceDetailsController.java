package com.sport.controller.coach;

import com.sport.model.Seance;
import com.sport.model.SeanceCollective;
import com.sport.model.SeanceIndividuelle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class CoachSeanceDetailsController {

    // Labels généraux
    @FXML private Label lblNom;
    @FXML private Label lblType;
    @FXML private Label lblSalle;
    @FXML private Label lblDate;
    @FXML private Label lblHeure;
    @FXML private Label lblDuree;
    @FXML private Label lblCoach;
    @FXML private Label lblTypeCours;

    // Labels pour séances collectives
    @FXML private Label lblCapacite;
    @FXML private Label lblPlaces;

    // Labels pour séances individuelles
    @FXML private Label lblMembre;
    @FXML private Label lblTarif;
    @FXML private Label lblNotes;

    // Sections VBox
    @FXML private VBox vboxCollective;
    @FXML private VBox vboxIndividuelle;

    // Bouton Fermer
    @FXML private Button btnFermer;

    private Seance seance;

    public void setSeance(Seance seance) {
        this.seance = seance;
        chargerDetails();
    }

    private void chargerDetails() {
        if (seance == null) return;

        // =====================
        // Infos générales
        // =====================
        lblNom.setText(seance.getNom());
        lblType.setText(seance.getTypeSeance().name());
        lblSalle.setText(seance.getSalle() != null ? seance.getSalle().getNom() : "-");
        lblDate.setText(seance.getDateHeure() != null
                ? seance.getDateHeure().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "-");
        lblHeure.setText(seance.getDateHeure() != null
                ? seance.getDateHeure().format(DateTimeFormatter.ofPattern("HH:mm"))
                : "-");
        lblDuree.setText(seance.getDuree() + " min");
        lblCoach.setText(seance.getEntraineur() != null ? seance.getEntraineur().getNom() : "-");
        lblTypeCours.setText(seance.getTypeCours() != null ? seance.getTypeCours().name() : "-");

        // =====================
        // Séance Collective
        // =====================
        if (seance instanceof SeanceCollective sc) {
            vboxCollective.setVisible(true);
            vboxCollective.setManaged(true);

            vboxIndividuelle.setVisible(false);
            vboxIndividuelle.setManaged(false);

            lblCapacite.setText(String.valueOf(sc.getCapaciteMax()));
            lblPlaces.setText(String.valueOf(sc.getPlacesDisponibles()));
        }

        // =====================
        // Séance Individuelle
        // =====================
        if (seance instanceof SeanceIndividuelle si) {
            vboxIndividuelle.setVisible(true);
            vboxIndividuelle.setManaged(true);

            vboxCollective.setVisible(false);
            vboxCollective.setManaged(false);

            lblMembre.setText(si.getMembre() != null
                    ? si.getMembre().getNom() + " " + si.getMembre().getPrenom()
                    : "Aucun membre assigné");
            lblTarif.setText(si.getTarif() != null ? si.getTarif() + " DH" : "-");
            lblNotes.setText(si.getNotesCoach() != null && !si.getNotesCoach().isEmpty()
                    ? si.getNotesCoach()
                    : "-");
        }
    }

    @FXML
    private void fermer() {
        Stage stage = (Stage) btnFermer.getScene().getWindow();
        stage.close();
    }

    // ====== Effets sur le bouton ======
    @FXML
    private void hoverFermer(MouseEvent event) {
        btnFermer.setStyle("-fx-background-color:#0b5ed7; -fx-text-fill:white; -fx-font-weight:bold; -fx-font-size:14; -fx-padding:10 25; -fx-background-radius:8;");
    }

    @FXML
    private void exitFermer(MouseEvent event) {
        btnFermer.setStyle("-fx-background-color:#0d6efd; -fx-text-fill:white; -fx-font-weight:bold; -fx-font-size:14; -fx-padding:10 25; -fx-background-radius:8;");
    }
}
