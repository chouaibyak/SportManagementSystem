package com.sport.controller.member;

import com.sport.model.*;
import com.sport.repository.SeanceRepository;
import com.sport.repository.SeanceCollectiveRepository;
import com.sport.repository.SeanceIndividuelleRepository;
import com.sport.service.ReservationService;
import com.sport.service.SeanceCollectiveService;
import com.sport.service.SeanceIndividuelleService;
import com.sport.utils.DBConnection;
import com.sport.utils.UserSession;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
// Pour les icônes (SVG simple)
import javafx.scene.shape.SVGPath;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class MemberPlanningController {

    @FXML private FlowPane cardsContainer; // Remplacement du TableView

    private SeanceRepository seanceRepo = new SeanceRepository();
    private SeanceCollectiveService collectiveService = new SeanceCollectiveService(new SeanceCollectiveRepository());
    private SeanceIndividuelleService indivService = new SeanceIndividuelleService(new SeanceIndividuelleRepository());
    private ReservationService resService = new ReservationService();

    private DateTimeFormatter fmtDate = DateTimeFormatter.ofPattern("EEE dd MMM");
    private DateTimeFormatter fmtHeure = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        chargerPlanning();
    }

    private void chargerPlanning() {
        cardsContainer.getChildren().clear();

        // 1. Récupération optimisée : On cherche le profil Membre une seule fois
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        Membre membreConnecte = resService.trouverMembreParUtilisateurId(user.getId());

        if (membreConnecte == null) {
            System.err.println("Attention : Utilisateur connecté sans profil Membre associé !");
        }

        // 2. Récupérer les séances
        List<Seance> toutesLesSeances = seanceRepo.getSeancesParPeriode(LocalDate.now(), LocalDate.now().plusDays(14));

        // 3. Trier par date (la plus proche en premier)
        toutesLesSeances.sort(Comparator.comparing(Seance::getDateHeure));

        // 4. Génération des cartes
        if (toutesLesSeances.isEmpty()) {
            Label emptyLbl = new Label("Aucune séance disponible pour le moment.");
            emptyLbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #95a5a6; -fx-padding: 20;");
            cardsContainer.getChildren().add(emptyLbl);
        } else {
            for (Seance s : toutesLesSeances) {
                // IMPORTANT : On passe la séance ET le membre connecté
                cardsContainer.getChildren().add(creerCarteSession(s, membreConnecte));
            }
        }
    }

    // Notez l'ajout du paramètre 'Membre membre' ici
    private VBox creerCarteSession(Seance s, Membre membre) {
        // --- STRUCTURE DE LA CARTE ---
        VBox card = new VBox();
        card.getStyleClass().add("session-card");

        // --- 1. HEADER (Type + Icône) ---
        HBox header = new HBox();
        header.getStyleClass().add("card-header");
        
        Label typeLabel = new Label();
        typeLabel.getStyleClass().add("type-label");
        
        SVGPath icon = new SVGPath();
        icon.setStyle("-fx-fill: white;");
        
        if (s.getTypeSeance() == TypeSeance.COLLECTIVE) {
            header.getStyleClass().add("header-collective");
            typeLabel.setText("COLLECTIF");
            icon.setContent("M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z");
        } else {
            header.getStyleClass().add("header-individuelle");
            typeLabel.setText("COACHING PRIVÉ");
            icon.setContent("M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z");
        }
        
        typeLabel.setGraphic(icon);
        header.getChildren().add(typeLabel);

        // --- 2. BODY (Infos) ---
        VBox body = new VBox();
        body.getStyleClass().add("card-body");
        
        Label dateLbl = new Label(s.getDateHeure().format(fmtDate).toUpperCase() + " • " + s.getDateHeure().format(fmtHeure));
        dateLbl.setStyle("-fx-text-fill: #95a5a6; -fx-font-weight: bold; -fx-font-size: 12px;");
        
        Label titleLbl = new Label(s.getNom());
        titleLbl.getStyleClass().add("activity-title");
        titleLbl.setWrapText(true);

        HBox coachBox = new HBox(5);
        SVGPath coachIcon = new SVGPath();
        coachIcon.setContent("M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z");
        coachIcon.setScaleX(0.7); coachIcon.setScaleY(0.7); coachIcon.setStyle("-fx-fill: #7f8c8d;");
        Label coachLbl = new Label("Coach: " + (s.getEntraineur() != null ? s.getEntraineur().getNom() : "Non assigné"));
        coachLbl.getStyleClass().add("info-text");
        coachBox.getChildren().addAll(coachIcon, coachLbl);
        coachBox.setAlignment(Pos.CENTER_LEFT);

        HBox salleBox = new HBox(5);
        SVGPath salleIcon = new SVGPath();
        salleIcon.setContent("M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z");
        salleIcon.setScaleX(0.7); salleIcon.setScaleY(0.7); salleIcon.setStyle("-fx-fill: #7f8c8d;");
        Label salleLbl = new Label((s.getSalle() != null ? s.getSalle().getNom() : "Salle principale"));
        salleLbl.getStyleClass().add("info-text");
        salleBox.getChildren().addAll(salleIcon, salleLbl);
        salleBox.setAlignment(Pos.CENTER_LEFT);

        // --- GESTION INTELLIGENTE DE LA DISPONIBILITÉ ---
        boolean complet = false;
        boolean dejaInscrit = false;
        String texteDispo = "";

        if (s.getTypeSeance() == TypeSeance.COLLECTIVE) {
            // === LOGIQUE COLLECTIVE ===
            int pris = resService.getNombrePlacesReservees(s.getId());
            int reste = s.getCapaciteMax() - pris;
            complet = reste <= 0;
            texteDispo = reste + " places restantes";
            
            if (membre != null) {
                dejaInscrit = resService.isMembreDejaInscrit(membre.getId(), s.getId());
            }

        } else {
            // === LOGIQUE INDIVIDUELLE (COACHING) ===
            // On vérifie qui est inscrit sur ce créneau (0 = personne)
            int membreIdInscrit = indivService.getMembreInscrit(s.getId());
            
            if (membreIdInscrit == 0) {
                // Créneau libre
                complet = false;
                dejaInscrit = false;
                texteDispo = "Créneau Disponible";
            } else {
                // Créneau pris
                if (membre != null && membreIdInscrit == membre.getId()) {
                    // C'est MOI
                    dejaInscrit = true;
                    complet = true; // Complet pour les autres
                    texteDispo = "Votre Séance";
                } else {
                    // C'est QUELQU'UN D'AUTRE
                    dejaInscrit = false;
                    complet = true;
                    texteDispo = "Déjà Réservé";
                }
            }
        }

        // Affichage du label de disponibilité
        Label placesLbl = new Label(texteDispo);
        if (complet && !dejaInscrit) {
            placesLbl.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11px; -fx-font-weight: bold;");
        } else {
            placesLbl.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px; -fx-font-weight: bold;");
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        body.getChildren().addAll(dateLbl, titleLbl, spacer, coachBox, salleBox, placesLbl);
        VBox.setVgrow(body, Priority.ALWAYS);


        // --- 3. FOOTER (Action) ---
        HBox footer = new HBox();
        footer.getStyleClass().add("card-footer");
        Button actionBtn = new Button();
        actionBtn.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(actionBtn, Priority.ALWAYS);

        if (dejaInscrit) {
            actionBtn.setText("INSCRIT ✓");
            actionBtn.getStyleClass().addAll("badge-joined");
            actionBtn.setDisable(true); 
            actionBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #27ae60; -fx-border-radius: 20;");
        } else if (complet) {
            actionBtn.setText(s.getTypeSeance() == TypeSeance.COLLECTIVE ? "COMPLET" : "RÉSERVÉ");
            actionBtn.getStyleClass().addAll("badge-full");
            actionBtn.setDisable(true);
            actionBtn.setStyle("-fx-background-color: transparent; -fx-border-color: #e74c3c; -fx-border-radius: 20;");
        } else {
            actionBtn.setText("RÉSERVER");
            actionBtn.getStyleClass().addAll("btn-reserve", "btn-reserve-active");
            actionBtn.setOnAction(e -> handleReservation(s));
        }

        footer.getChildren().add(actionBtn);
        card.getChildren().addAll(header, body, footer);
        
        return card;
    }

    private void handleReservation(Seance s) {
        // 1. Récupération User & Membre
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        Membre membre = resService.trouverMembreParUtilisateurId(user.getId());

        if (membre == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de profil");
            alert.setHeaderText("Profil membre introuvable");
            alert.setContentText("Votre compte utilisateur n'est pas lié à un profil membre.");
            alert.showAndWait();
            return;
        }

        boolean success = false;

        // 2. Logique selon le type de séance
        if (s.getTypeSeance() == TypeSeance.COLLECTIVE) {
            // Logique Collective : Table de liaison
            success = collectiveService.reserverPlace(s.getId(), membre);
        } else {
            // === LOGIQUE INDIVIDUELLE (COACHING) ===
            // On appelle le service pour faire l'UPDATE dans la table seanceindividuelle
            // Cela mettra notre ID à la place de NULL
            success = indivService.reserverSession(s.getId(), membre);
        }

        // 3. Résultat
        if (success) {
            // A. Créer l'historique dans la table RESERVATION
            Reservation r = new Reservation();
            r.setDateReservation(new java.util.Date());
            r.setStatut(StatutReservation.CONFIRMEE);
            r.setSeance(s);
            r.setMembre(membre);
            resService.creerReservation(r);

            // B. Message succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Félicitations");
            alert.setHeaderText("Réservation confirmée !");
            alert.setContentText("Vous êtes bien inscrit à la séance : " + s.getNom());
            alert.showAndWait();

            // C. Rafraîchir l'affichage
            chargerPlanning(); 
        } else {
            // D. Erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Oups");
            alert.setHeaderText("Impossible de réserver");
            alert.setContentText("Ce créneau vient peut-être d'être pris par quelqu'un d'autre.");
            alert.showAndWait();
            chargerPlanning(); // On rafraîchit quand même pour voir le nouvel état
        }
    }
}