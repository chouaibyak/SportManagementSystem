package com.sport.controller.coach;

import com.sport.model.Membre;
import com.sport.model.SeanceIndividuelle;
import com.sport.service.MembreService;
import com.sport.service.SeanceIndividuelleService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

/** Controller pour le détail d'une séance individuelle */
public class CoachSeanceIndivDetailsController {

    @FXML private Label lblNom;
    @FXML private Label lblEmail;
    @FXML private TextArea txtFeedback;
    @FXML private TextField txtTarif;
    @FXML private Button btnSave;
    @FXML private Button btnClose;
    @FXML private Button btnAssignerMembre;

    private SeanceIndividuelle seance;
    private final SeanceIndividuelleService indivService = new SeanceIndividuelleService(
            new com.sport.repository.SeanceIndividuelleRepository()
    );
    private final MembreService membreService = new MembreService();

    /** Callback pour notifier le parent que la séance a été modifiée */
    private Runnable onSeanceUpdated;

    public void setOnSeanceUpdated(Runnable callback) {
        this.onSeanceUpdated = callback;
    }

    /** Setter pour la séance individuelle à afficher */
    public void setSeance(SeanceIndividuelle seance) {
        this.seance = seance;
        loadData();
    }

    /** Charge les infos du membre, notes et tarif */
    private void loadData() {
        Membre m = seance.getMembre();

        if (m == null) {
            lblNom.setText("-");
            lblEmail.setText("Aucun membre assigné");
            txtFeedback.clear();
            txtTarif.clear();
            txtFeedback.setDisable(true);
            txtTarif.setDisable(true);
            btnSave.setDisable(true);
            btnAssignerMembre.setVisible(true);
        } else {
            lblNom.setText(m.getNom() + " " + m.getPrenom());
            lblEmail.setText(m.getEmail());
            txtFeedback.setText(seance.getNotesCoach() != null ? seance.getNotesCoach() : "");
            txtTarif.setText(seance.getTarif() != null ? String.valueOf(seance.getTarif()) : "");
            txtFeedback.setDisable(false);
            txtTarif.setDisable(false);
            btnSave.setDisable(false);
            btnAssignerMembre.setVisible(false);
        }
    }

    /** Assigner un membre à la séance individuelle */
    @FXML
    private void assignerMembre() {
        if (seance == null) return;

        List<Membre> tousLesMembres = membreService.getAllMembres();
        if (tousLesMembres.isEmpty()) {
            showAlert("Info", "Aucun membre disponible pour l'assignation.");
            return;
        }

        List<String> choix = tousLesMembres.stream()
                .map(m -> m.getNom() + " " + m.getPrenom() + " (" + m.getEmail() + ")")
                .toList();

        ChoiceDialog<String> dialog = new ChoiceDialog<>(choix.get(0), choix);
        dialog.setTitle("Assigner un membre");
        dialog.setHeaderText("Sélectionner un membre à assigner");
        dialog.setContentText("Membre :");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selection -> {
            Membre membreChoisi = tousLesMembres.stream()
                    .filter(m -> (m.getNom() + " " + m.getPrenom() + " (" + m.getEmail() + ")").equals(selection))
                    .findFirst()
                    .orElse(null);

            if (membreChoisi != null) {
                seance.setMembre(membreChoisi);
                indivService.update(seance);  // mise à jour DB
                loadData();                    // mettre à jour l'UI
                showAlert("Succès", "Membre assigné à la séance individuelle.");

                if (onSeanceUpdated != null) onSeanceUpdated.run(); // notifier le parent
            }
        });
    }

    /** Enregistre notes et tarif */
    @FXML
    private void enregistrer() {
        if (seance == null || seance.getMembre() == null) {
            showAlert("Erreur", "Veuillez assigner un membre avant d'enregistrer le feedback et le tarif.");
            return;
        }

        seance.setNotesCoach(txtFeedback.getText());

        try {
            double tarif = Double.parseDouble(txtTarif.getText());
            seance.setTarif(tarif);
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le tarif doit être un nombre valide.");
            return;
        }

        indivService.update(seance); // mise à jour DB
        showAlert("Succès", "Feedback et tarif enregistrés avec succès");

        if (onSeanceUpdated != null) onSeanceUpdated.run(); // notifier le parent
    }

    /** Fermer la fenêtre */
    @FXML
    private void fermer() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    /** Affiche une alerte simple */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
