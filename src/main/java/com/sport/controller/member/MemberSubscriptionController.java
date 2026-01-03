package com.sport.controller.member;

import com.sport.model.Abonnement;
import com.sport.model.Utilisateur;
import com.sport.repository.AbonnementRepository;
import com.sport.utils.UserSession;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class MemberSubscriptionController {

    @FXML private FlowPane cardsContainer;

    private AbonnementRepository abonnementRepo = new AbonnementRepository();

    @FXML
    public void initialize() {
        chargerAbonnements();
    }

    private void chargerAbonnements() {
        Utilisateur currentUser = UserSession.getInstance().getUtilisateur();
        if (currentUser == null) return;

        // On récupère les abonnements via le nom complet (selon ta BDD)
        String nomComplet = currentUser.getNom() + " " + currentUser.getPrenom();
        List<Abonnement> abonnements = abonnementRepo.trouverParNomMembre(nomComplet);

        cardsContainer.getChildren().clear();

        if (abonnements.isEmpty()) {
            Label emptyLabel = new Label("Aucun abonnement trouvé.");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
            cardsContainer.getChildren().add(emptyLabel);
        } else {
            for (Abonnement ab : abonnements) {
                cardsContainer.getChildren().add(creerCarteAbonnement(ab));
            }
        }
    }

    private VBox creerCarteAbonnement(Abonnement ab) {
        // --- 1. CONVERSION DES DATES (CORRECTION DE L'ERREUR) ---
        // On convertit les dates SQL/Util en LocalDate pour faire les calculs
        LocalDate dateFin = new java.sql.Date(ab.getDateFin().getTime()).toLocalDate();
        LocalDate dateDebut = new java.sql.Date(ab.getDateDebut().getTime()).toLocalDate();
        LocalDate aujourdhui = LocalDate.now();

        // --- 2. CALCULS ---
        // Maintenant between() fonctionne car on compare LocalDate avec LocalDate
        long joursRestants = ChronoUnit.DAYS.between(aujourdhui, dateFin);
        boolean estActif = joursRestants >= 0 && "ACTIF".equalsIgnoreCase(ab.getStatutAbonnement().name());
        
        // --- 3. CONTENEUR CARTE ---
        VBox card = new VBox();
        card.setPrefSize(280, 380);
        card.getStyleClass().add("subscription-card");

        // --- 4. HEADER (Type + Prix) ---
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new javafx.geometry.Insets(20));
        
        // Couleur header selon statut
        String headerColor = estActif ? "linear-gradient(to right, #ff9966, #ff5e62)" : "linear-gradient(to right, #bdc3c7, #2c3e50)";
        header.setStyle("-fx-background-color: " + headerColor + "; -fx-background-radius: 15 15 0 0;");

        Label typeLabel = new Label(ab.getTypeAbonnement().name());
        typeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");

        Label priceLabel = new Label(ab.getMontant() + " MAD");
        priceLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label durationLabel = new Label("/ " + ab.getTypeAbonnement().name().toLowerCase());
        durationLabel.setStyle("-fx-text-fill: #f0f0f0; -fx-font-size: 12px;");

        header.getChildren().addAll(typeLabel, priceLabel, durationLabel);

        // --- 5. CORPS (Détails) ---
        VBox body = new VBox(15);
        body.setPadding(new javafx.geometry.Insets(20));
        body.setAlignment(Pos.CENTER_LEFT);

        // Statut Badge
        Label statusBadge = new Label(estActif ? "ACTIF" : "EXPIRÉ / RESILIÉ");
        statusBadge.getStyleClass().add(estActif ? "badge-active" : "badge-inactive");
        
        // Dates (Affichage simple)
        Label datesLabel = new Label("Du " + ab.getDateDebut() + " au " + ab.getDateFin());
        datesLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 13px;");

        // Info Jours Restants
        Label remainingLabel = new Label(estActif ? joursRestants + " jours restants" : "Abonnement terminé");
        remainingLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + (estActif ? "#27ae60" : "#e74c3c"));

        // Barre de progression
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(240);
        
        if (estActif) {
            // Calcul de la durée totale pour le ratio de la barre
            long dureeTotale = ChronoUnit.DAYS.between(dateDebut, dateFin);
            
            // Éviter la division par zéro
            double progress = dureeTotale > 0 ? (double) joursRestants / dureeTotale : 0;
            
            // Inverser la logique : Barre pleine au début, vide à la fin ? 
            // Ou Vide au début, pleine à la fin ? 
            // Ici : Pleine = Il reste beaucoup de temps. Vide = Bientôt fini.
            progressBar.setProgress(progress);
            progressBar.setStyle("-fx-accent: #ff7000;"); // Orange Basic-Fit
        } else {
            progressBar.setProgress(0);
        }

        // Features (Déco)
        VBox features = new VBox(5);
        features.getChildren().add(createFeatureRow("Accès Salle Musculation"));
        features.getChildren().add(createFeatureRow("Accès Cours Collectifs"));
        if(ab.getMontant() > 500) features.getChildren().add(createFeatureRow("Coaching Inclus"));

        body.getChildren().addAll(statusBadge, datesLabel, features, remainingLabel, progressBar);

        // Assemblage
        card.getChildren().addAll(header, body);
        return card;
    }
    private HBox createFeatureRow(String text) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label icon = new Label("✔");
        icon.setStyle("-fx-text-fill: #ff7000; -fx-font-weight: bold;");
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #34495e;");
        row.getChildren().addAll(icon, lbl);
        return row;
    }
}