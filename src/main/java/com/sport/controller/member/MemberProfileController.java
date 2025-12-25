package com.sport.controller.member;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.sport.model.Membre;
import com.sport.model.TypeObjectif;
import com.sport.model.TypePreference;
import com.sport.service.MembreService;
import com.sport.utils.UserSession;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;



public class MemberProfileController {

    // Informations personnelles
    @FXML private Label lblNomComplet;
    @FXML private Label lblEmail;
    @FXML private ImageView imgProfile;
    
    // Formulaire d'édition
    @FXML private TextField txtNom;
    @FXML private TextField txtPrenom;
    @FXML private DatePicker dateNaissance;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelephone;
    @FXML private TextField txtAdresse;
    
    // Objectifs et préférences
    @FXML private ComboBox<TypeObjectif> comboObjectif;
    @FXML private ComboBox<TypePreference> comboPreference;
    
    // Changement de mot de passe
    @FXML private PasswordField txtAncienMdp;
    @FXML private PasswordField txtNouveauMdp;
    @FXML private PasswordField txtConfirmMdp;
    
    // Boutons
    @FXML private Button btnModifier;
    @FXML private Button btnAnnuler;
    @FXML private Button btnChangerPhoto;
    @FXML private Button btnChangerMdp;
    
    // Statistiques
    @FXML private Label lblDateInscription;
    @FXML private Label lblNombreSeances;
    @FXML private Label lblDerniereVisite;
    
    // Mode édition
    @FXML private TabPane tabPane;
    
    private MembreService membreService;
    private Membre membreConnecte;
    private boolean modeEdition = false;

    @FXML
public void initialize() {
    membreService = new MembreService();
    membreConnecte = (Membre) UserSession.getInstance().getUtilisateur();

    configurerComboBox(); // (je te conseille ce placement)

    if (membreConnecte != null) {
        chargerDonneesMembre();
        desactiverEdition();
        chargerPhotoProfil();
    }
}


    private void chargerDonneesMembre() {
        // Recharger les données depuis la BDD pour être sûr d'avoir les dernières infos
        Membre membreActuel = membreService.recupererMembreParId(membreConnecte.getId());
        if (membreActuel != null) {
            membreConnecte = membreActuel;
            UserSession.getInstance().setUtilisateur(membreConnecte);
        }
        
        // Afficher les infos dans l'en-tête
        lblNomComplet.setText(membreConnecte.getNom() + " " + membreConnecte.getPrenom());
        lblEmail.setText(membreConnecte.getEmail());
        
        // Remplir les champs du formulaire
        txtNom.setText(membreConnecte.getNom());
        txtPrenom.setText(membreConnecte.getPrenom());
        txtEmail.setText(membreConnecte.getEmail());
        txtTelephone.setText(membreConnecte.getTelephone());
        txtAdresse.setText(membreConnecte.getAdresse());
        
        // Date de naissance
        if (membreConnecte.getDateNaissance() != null && !membreConnecte.getDateNaissance().isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date = LocalDate.parse(membreConnecte.getDateNaissance(), formatter);
                dateNaissance.setValue(date);
            } catch (Exception e) {
                System.err.println("Erreur format date : " + e.getMessage());
            }
        }
        
        // Objectifs et préférences
        comboObjectif.setValue(membreConnecte.getObjectifSportif());
        comboPreference.setValue(membreConnecte.getPreferences());
        
        
    }

    private void configurerComboBox() {
        comboObjectif.setItems(FXCollections.observableArrayList(TypeObjectif.values()));
        comboPreference.setItems(FXCollections.observableArrayList(TypePreference.values()));
    }

    @FXML
    private void handleModifier() {
        if (!modeEdition) {
            // Activer le mode édition
            activerEdition();
            btnModifier.setText("Enregistrer");
            modeEdition = true;
        } else {
            // Sauvegarder les modifications
            if (validerFormulaire()) {
                sauvegarderModifications();
                desactiverEdition();
                btnModifier.setText("Modifier");
                modeEdition = false;
                
                afficherMessage("Succès", "Profil mis à jour avec succès !", Alert.AlertType.INFORMATION);
            }
        }
    }

    @FXML
    private void handleAnnuler() {
        desactiverEdition();
        chargerDonneesMembre(); // Recharger les données originales
        btnModifier.setText("Modifier");
        modeEdition = false;
    }

   @FXML
private void handleChangerPhoto() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choisir une photo de profil");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
    );

    File selectedFile = fileChooser.showOpenDialog(btnChangerPhoto.getScene().getWindow());
    if (selectedFile == null) return;

    try {
        ensureProfileDirExists();

        Path target = getProfileImagePath(membreConnecte.getId());

        // Copie et remplace l’ancienne si elle existe
        Files.copy(selectedFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);

        // Recharge l’image dans l’UI
        imgProfile.setImage(new Image(target.toUri().toString(), true));

        afficherMessage("Succès", "Photo de profil mise à jour !", Alert.AlertType.INFORMATION);

    } catch (IOException e) {
        afficherMessage("Erreur", "Impossible d'enregistrer la photo : " + e.getMessage(), Alert.AlertType.ERROR);
    }
}


    @FXML
    private void handleChangerMotDePasse() {
        if (!validerMotDePasse()) {
            return;
        }
        
        // Pour l'instant, on simule
        
        if (txtAncienMdp.getText().equals(membreConnecte.getMotDePasse())) {
            membreConnecte.setMotDePasse(txtNouveauMdp.getText());
            membreService.mettreAJourMembre(membreConnecte);
            
            // Vider les champs
            txtAncienMdp.clear();
            txtNouveauMdp.clear();
            txtConfirmMdp.clear();
            
            afficherMessage("Succès", "Mot de passe modifié avec succès !", Alert.AlertType.INFORMATION);
        } else {
            afficherMessage("Erreur", "L'ancien mot de passe est incorrect", Alert.AlertType.ERROR);
        }
    }

    private void activerEdition() {
        txtNom.setDisable(false);
        txtPrenom.setDisable(false);
        dateNaissance.setDisable(false);
        txtEmail.setDisable(false);
        txtTelephone.setDisable(false);
        txtAdresse.setDisable(false);
        comboObjectif.setDisable(false);
        comboPreference.setDisable(false);
        
        btnAnnuler.setVisible(true);
        btnAnnuler.setManaged(true);
    }

    private void desactiverEdition() {
        txtNom.setDisable(true);
        txtPrenom.setDisable(true);
        dateNaissance.setDisable(true);
        txtEmail.setDisable(true);
        txtTelephone.setDisable(true);
        txtAdresse.setDisable(true);
        comboObjectif.setDisable(true);
        comboPreference.setDisable(true);
        
        btnAnnuler.setVisible(false);
        btnAnnuler.setManaged(false);
    }

    private void sauvegarderModifications() {
        membreConnecte.setNom(txtNom.getText().trim());
        membreConnecte.setPrenom(txtPrenom.getText().trim());
        membreConnecte.setEmail(txtEmail.getText().trim());
        membreConnecte.setTelephone(txtTelephone.getText().trim());
        membreConnecte.setAdresse(txtAdresse.getText().trim());
        membreConnecte.setDateNaissance(dateNaissance.getValue().toString());
        membreConnecte.setObjectifSportif(comboObjectif.getValue());
        membreConnecte.setPreferences(comboPreference.getValue());
        
        membreService.mettreAJourMembre(membreConnecte);
        
        // Mettre à jour la session
        UserSession.getInstance().setUtilisateur(membreConnecte);
        
        // Rafraîchir l'affichage
        lblNomComplet.setText(membreConnecte.getNom() + " " + membreConnecte.getPrenom());
        lblEmail.setText(membreConnecte.getEmail());
    }

    private boolean validerFormulaire() {
        StringBuilder erreurs = new StringBuilder();
        
        if (txtNom.getText().trim().isEmpty()) {
            erreurs.append("• Le nom est obligatoire\n");
        }
        if (txtPrenom.getText().trim().isEmpty()) {
            erreurs.append("• Le prénom est obligatoire\n");
        }
        if (txtEmail.getText().trim().isEmpty()) {
            erreurs.append("• L'email est obligatoire\n");
        } else if (!txtEmail.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            erreurs.append("• L'email n'est pas valide\n");
        }
        if (dateNaissance.getValue() == null) {
            erreurs.append("• La date de naissance est obligatoire\n");
        }
        if (comboObjectif.getValue() == null) {
            erreurs.append("• L'objectif sportif est obligatoire\n");
        }
        if (comboPreference.getValue() == null) {
            erreurs.append("• La préférence est obligatoire\n");
        }
        
        if (erreurs.length() > 0) {
            afficherMessage("Erreurs de validation", erreurs.toString(), Alert.AlertType.WARNING);
            return false;
        }
        
        return true;
    }

    private boolean validerMotDePasse() {
        StringBuilder erreurs = new StringBuilder();
        
        if (txtAncienMdp.getText().isEmpty()) {
            erreurs.append("• L'ancien mot de passe est obligatoire\n");
        }
        if (txtNouveauMdp.getText().isEmpty()) {
            erreurs.append("• Le nouveau mot de passe est obligatoire\n");
        } else if (txtNouveauMdp.getText().length() < 6) {
            erreurs.append("• Le mot de passe doit contenir au moins 6 caractères\n");
        }
        if (txtConfirmMdp.getText().isEmpty()) {
            erreurs.append("• La confirmation est obligatoire\n");
        } else if (!txtNouveauMdp.getText().equals(txtConfirmMdp.getText())) {
            erreurs.append("• Les mots de passe ne correspondent pas\n");
        }
        
        if (erreurs.length() > 0) {
            afficherMessage("Erreurs de validation", erreurs.toString(), Alert.AlertType.WARNING);
            return false;
        }
        
        return true;
    }

    private void afficherMessage(String titre, String contenu, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    // Dossier local: C:\Users\<toi>\SportMS\profile_pics\  (Windows)
// ou /home/<toi>/SportMS/profile_pics/ (Linux)
private Path getProfileDir() {
    return Paths.get(System.getProperty("user.home"), "SportMS", "profile_pics");
}

// On force un nom stable: user_<id>.png
private Path getProfileImagePath(int userId) {
    return getProfileDir().resolve("user_" + userId + ".png");
}

private void ensureProfileDirExists() throws IOException {
    Files.createDirectories(getProfileDir());
}

private void chargerPhotoProfil() {
    try {
        Path path = getProfileImagePath(membreConnecte.getId());

        if (Files.exists(path)) {
            imgProfile.setImage(new Image(path.toUri().toString(), true));
        } else {
            imgProfile.setImage(new Image(
                getClass().getResource("/images/default_profile.png").toExternalForm()
            ));
        }

    } catch (Exception e) {
        System.err.println("Erreur chargement photo profil : " + e.getMessage());
    }
}

}