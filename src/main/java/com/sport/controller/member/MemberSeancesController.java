package com.sport.controller.member;

import com.sport.model.Membre;
import com.sport.model.Reservation;
import com.sport.model.Seance;
import com.sport.model.TypeSeance;
import com.sport.service.ReservationService;
import com.sport.service.SeanceCollectiveService;
import com.sport.utils.UserSession;
import com.sport.model.Utilisateur;
import com.sport.repository.SeanceCollectiveRepository;

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

    private void handleAnnulation(Reservation r) {
        // 1. Libérer la place si c'est une séance collective
        Seance s = r.getSeance();
        if (s != null && s.getTypeSeance() == TypeSeance.COLLECTIVE) {
            // On recrée l'objet membre nécessaire
            Membre m = new Membre();
            m.setId(UserSession.getInstance().getUtilisateur().getId());
            
            // Appel à ton service qui fait +1 sur placesDisponibles
            collectiveService.annulerReservation(s.getId(), m);
        }

        // 2. Mettre à jour le statut dans la table RESERVATION (Historique)
        // Note : On ne supprime pas la ligne, on met "ANNULEE" pour garder une trace
        reservationService.annulerReservation(r.getId());
        
        // 3. Rafraîchir
        chargerDonnees();
    }
    }