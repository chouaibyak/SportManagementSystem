package com.sport.controller.member;

import com.sport.model.Membre;
import com.sport.model.Reservation;
import com.sport.model.Seance;
import com.sport.model.TypeSeance;
import com.sport.service.ReservationService;
import com.sport.service.SeanceCollectiveService;
import com.sport.service.SeanceIndividuelleService;
import com.sport.utils.UserSession;
import com.sport.model.Utilisateur;
import com.sport.repository.SeanceCollectiveRepository;
import com.sport.repository.SeanceIndividuelleRepository;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class MemberSeancesController {

    @FXML private TableView<Reservation> tableSeances;
    @FXML private TableColumn<Reservation, String> colDate;
    @FXML private TableColumn<Reservation, String> colNom;
    @FXML private TableColumn<Reservation, String> colDuree;
    @FXML private TableColumn<Reservation, String> colStatut;
    @FXML private TableColumn<Reservation, Void> colAction;

    private ReservationService reservationService = new ReservationService();
    private SeanceCollectiveService collectiveService = new SeanceCollectiveService(new SeanceCollectiveRepository());
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private SeanceIndividuelleService indivService = new SeanceIndividuelleService(new SeanceIndividuelleRepository());

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerDonnees();
    }

    private void configurerColonnes() {
        // 1. Date (Formatée)
        colDate.setCellValueFactory(cellData -> {
            Seance s = cellData.getValue().getSeance();
            if (s != null && s.getDateHeure() != null) {
                return new SimpleStringProperty(s.getDateHeure().format(formatter));
            }
            return new SimpleStringProperty("-");
        });

        // 2. Nom du cours
        colNom.setCellValueFactory(cellData -> {
            Seance s = cellData.getValue().getSeance();
            return new SimpleStringProperty(s != null ? s.getNom() : "Inconnu");
        });

        // 3. Durée
        colDuree.setCellValueFactory(cellData -> {
            Seance s = cellData.getValue().getSeance();
            return new SimpleStringProperty(s != null ? s.getDuree() + " min" : "-");
        });

        // 4. Statut
        colStatut.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatut().toString())
        );
        
        // Couleur du texte selon le statut (Optionnel mais joli)
        colStatut.setCellFactory(column -> new TableCell<Reservation, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("ANNULEE")) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else if (item.equals("PRESENT")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // 5. Bouton Annuler (Colonne Action)
        ajouterBoutonAnnuler();
    }

    private void ajouterBoutonAnnuler() {
        Callback<TableColumn<Reservation, Void>, TableCell<Reservation, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btn = new Button("Annuler");

            {
                // --- MODIFICATION ICI : On utilise la classe CSS ---
                btn.getStyleClass().add("btn-cancel-table"); 
                // On retire le setStyle(...) en dur
                
                btn.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleAnnulation(reservation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reservation r = getTableView().getItems().get(getIndex());
                    if (!"ANNULEE".equals(r.getStatut().toString())) {
                        setGraphic(btn);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        };
        colAction.setCellFactory(cellFactory);
    }

    private void chargerDonnees() {
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        if (user != null) {
            // Appel à ton Service existant
            List<Reservation> liste = reservationService.getReservationsMembre(user.getId());
            ObservableList<Reservation> data = FXCollections.observableArrayList(liste);
            tableSeances.setItems(data);
        }
    }

    // Dans MemberSeancesController.java
    private void handleAnnulation(Reservation r) {
        Seance s = r.getSeance();
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        
        if (s != null && user != null) {
            // On récupère le profil membre (pour avoir l'ID 35 par exemple)
            Membre membreConnecte = reservationService.trouverMembreParUtilisateurId(user.getId());

            if (membreConnecte != null) {
                System.out.println("ANNULATION - Séance ID: " + s.getId() + " | Type: " + s.getTypeSeance());

                // 1. Libérer la place selon le type
                if (s.getTypeSeance() == TypeSeance.COLLECTIVE) {
                    collectiveService.annulerReservation(s.getId(), membreConnecte);
                } 
                else if (s.getTypeSeance() == TypeSeance.INDIVIDUELLE) {
                    // LIBÉRATION DE LA SÉANCE INDIVIDUELLE
                    // On passe 0 comme ID de membre pour que le repo mette NULL
                    indivService.reserverSession(s.getId(), null); 
                    System.out.println("-> Créneau individuel libéré (membre_id mis à NULL)");
                }

                // 2. SUPPRIMER l'historique de la table RESERVATION
                // Indispensable pour que l'app ne croie plus que vous êtes inscrit
                reservationService.supprimerReservation(r.getId());
                
                // 3. Rafraîchir l'affichage
                chargerDonnees();
            }
        }
    }
}