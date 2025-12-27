package com.sport.controller.coach;

import com.sport.model.Coach;
import com.sport.repository.CoachRepository;
import com.sport.utils.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class CoachProfilController {

    // =============================
    // FXML Components
    // =============================
    @FXML private ImageView imgProfile;
    @FXML private Label lblNom;
    @FXML private Label lblEmail;
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private TextField txtTelephone;
    @FXML private TextField txtAdresse;
    @FXML private FlowPane flowSpecialites;
    @FXML private FlowPane flowCertifications;
    @FXML private Button btnModifier;
    @FXML private Button btnAnnuler;
    @FXML private Button btnChangerPhoto;

    @FXML private PasswordField txtAncienMdp;
    @FXML private PasswordField txtNouveauMdp;
    @FXML private PasswordField txtConfirmMdp;
    @FXML private Button btnChangerMdp;

    @FXML private TextField txtNouvelleCertif;
    @FXML private Button btnAjouterCertif;

    @FXML private VBox vboxMdp;
    @FXML private Button btnAfficherMdp;

    // =============================
    // Variables
    // =============================
    private CoachRepository coachRepo = new CoachRepository();
    private Coach coach;

    // =============================
    // INITIALIZE
    // =============================
    @FXML
    public void initialize() {
        int id = UserSession.getInstance().getUtilisateur().getId();
        coach = coachRepo.getCoachById(id);

        // Avatar par défaut
        try {
            imgProfile.setImage(new Image(getClass().getResourceAsStream("/images/avatar.png")));
        } catch (Exception e) {
            System.out.println("Image par défaut non trouvée !");
        }

        afficherInfosCoach();
        activerEdition(false);

        // ===== Boutons principaux =====
        btnModifier.setOnAction(e -> activerEdition(true));
        btnAnnuler.setOnAction(e -> {
            activerEdition(false);
            afficherInfosCoach();
        });
        btnChangerPhoto.setOnAction(e -> handleChangerPhoto());
        btnChangerMdp.setOnAction(e -> handleChangerMotDePasse());

        // ===== Toggle formulaire mot de passe =====
        btnAfficherMdp.setOnAction(e -> {
            boolean visible = vboxMdp.isVisible();
            vboxMdp.setVisible(!visible);
            vboxMdp.setManaged(!visible); // Pour réorganiser le layout
        });

        // ===== Ajouter une certification (UI only) =====
        btnAjouterCertif.setOnAction(e -> {
            String nouvelle = txtNouvelleCertif.getText().trim();
            if(!nouvelle.isEmpty()) {
                Label tag = new Label(nouvelle);
                tag.setStyle("-fx-background-color:#FFB74D; -fx-text-fill:white; -fx-padding:4 10; -fx-background-radius:15; -fx-font-size:12;");
                flowCertifications.getChildren().add(tag);
                txtNouvelleCertif.clear();
            }
        });
    }

    // =============================
    // AFFICHAGE DES INFOS
    // =============================
    private void afficherInfosCoach() {
        if (coach != null) {
            lblNom.setText(coach.getNom() + " " + coach.getPrenom());
            lblEmail.setText(coach.getEmail());

            txtNom.setText(coach.getNom());
            txtPrenom.setText(coach.getPrenom());
            txtTelephone.setText(coach.getTelephone());
            txtAdresse.setText(coach.getAdresse());

            // Spécialités
            flowSpecialites.getChildren().clear();
            if (coach.getSpecialites() != null) {
                for (String spec : coach.getSpecialites()) {
                    Label tag = new Label(spec);
                    tag.setStyle("-fx-background-color:#4FC3F7; -fx-text-fill:white; -fx-padding:4 10; -fx-background-radius:15; -fx-font-size:12;");
                    flowSpecialites.getChildren().add(tag);
                }
            }

            // Certifications fixes
            flowCertifications.getChildren().clear();
            String[] certifs = {"Coaching Sportif", "Fitness & Nutrition", "Yoga"};
            for (String certif : certifs) {
                Label tag = new Label(certif);
                tag.setStyle("-fx-background-color:#FFB74D; -fx-text-fill:white; -fx-padding:4 10; -fx-background-radius:15; -fx-font-size:12;");
                flowCertifications.getChildren().add(tag);
            }
        }
    }

    // =============================
    // MODE ÉDITION
    // =============================
    private void activerEdition(boolean editable) {
        txtNom.setDisable(!editable);
        txtPrenom.setDisable(!editable);
        txtTelephone.setDisable(!editable);
        txtAdresse.setDisable(!editable);

        btnAnnuler.setVisible(editable);
        btnAnnuler.setManaged(editable);

        if (!editable) {
            coach.setNom(txtNom.getText());
            coach.setPrenom(txtPrenom.getText());
            coach.setTelephone(txtTelephone.getText());
            coach.setAdresse(txtAdresse.getText());
            coachRepo.modifierCoach(coach);
        }
    }

    // =============================
    // CHANGER PHOTO
    // =============================
    private void handleChangerPhoto() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Fonctionnalité de changement de photo non implémentée.");
        alert.showAndWait();
    }

    // =============================
    // CHANGER MOT DE PASSE
    // =============================
    private void handleChangerMotDePasse() {
        String ancien = txtAncienMdp.getText();
        String nouveau = txtNouveauMdp.getText();
        String confirm = txtConfirmMdp.getText();

        if (!nouveau.equals(confirm)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Les mots de passe ne correspondent pas !");
            alert.showAndWait();
            return;
        }

        coach.setMotDePasse(nouveau);
        coachRepo.modifierCoach(coach);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Mot de passe changé avec succès !");
        alert.showAndWait();

        txtAncienMdp.clear();
        txtNouveauMdp.clear();
        txtConfirmMdp.clear();
    }
}
