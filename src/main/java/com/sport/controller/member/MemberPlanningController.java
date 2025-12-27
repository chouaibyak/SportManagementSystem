package com.sport.controller.member;

import com.sport.model.Reservation;
import com.sport.model.Seance;
import com.sport.model.StatutReservation;
import com.sport.model.Utilisateur;
import com.sport.model.Membre;
import com.sport.repository.SeanceRepository;
import com.sport.service.ReservationService;
import com.sport.utils.UserSession;
import com.sport.model.TypeSeance;
import com.sport.service.SeanceCollectiveService;
import com.sport.repository.SeanceCollectiveRepository;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class MemberPlanningController {

    @FXML private TableView<Seance> tablePlanning;
    @FXML private TableColumn<Seance, String> colDate;
    @FXML private TableColumn<Seance, String> colCours;
    @FXML private TableColumn<Seance, String> colCoach;
    @FXML private TableColumn<Seance, String> colSalle;
    @FXML private TableColumn<Seance, String> colPlaces;
    @FXML private TableColumn<Seance, Void> colAction;

    private SeanceRepository seanceRepo = new SeanceRepository();
    private ReservationService resService = new ReservationService();
    private SeanceCollectiveService collectiveService = new SeanceCollectiveService(new SeanceCollectiveRepository());
    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    @FXML
    public void initialize() {
        configurerColonnes();
        chargerDonnees();
    }

    private void chargerDonnees() {
        // Charge les séances des 7 prochains jours
        List<Seance> liste = seanceRepo.getSeancesParPeriode(LocalDate.now(), LocalDate.now().plusDays(7));
        tablePlanning.setItems(FXCollections.observableArrayList(liste));
    }

    private void configurerColonnes() {
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDateHeure().format(fmt)));
        colCours.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNom()));
        
        // Coach (protection null)
        colCoach.setCellValueFactory(cell -> {
            if(cell.getValue().getEntraineur() != null) return new SimpleStringProperty(cell.getValue().getEntraineur().getNom());
            return new SimpleStringProperty("-");
        });

        // Salle (protection null) - ICI on profite du JOIN fait à l'étape 1
        colSalle.setCellValueFactory(cell -> {
            if(cell.getValue().getSalle() != null) return new SimpleStringProperty(cell.getValue().getSalle().getNom());
            return new SimpleStringProperty("-");
        });

        // Places (Calcul dynamique)
        colPlaces.setCellValueFactory(cell -> {
            Seance s = cell.getValue();
            int pris = resService.getNombrePlacesReservees(s.getId());
            // 2. Calcul du nombre de places restantes
            int reste = s.getCapaciteMax() - pris;
            // 3. Affichage différent selon le type
            if (s.getTypeSeance() == TypeSeance.COLLECTIVE) {
                if (reste <= 0) {
                    return new SimpleStringProperty("COMPLET");
                }
                return new SimpleStringProperty(reste + " places disp.");
            } 
            else if (s.getTypeSeance() == TypeSeance.INDIVIDUELLE) {
                // Pour une séance individuelle (Capacité max est souvent 1)
                if (pris >= s.getCapaciteMax()) {
                    return new SimpleStringProperty("NON DISPO");
                }
                return new SimpleStringProperty("DISPONIBLE");
            }
            
            return new SimpleStringProperty(pris + " / " + s.getCapaciteMax());
        });

        // ACTION (Le bouton intelligent)
        colAction.setCellFactory(creerBoutonAction());
    }

    private Callback<TableColumn<Seance, Void>, TableCell<Seance, Void>> creerBoutonAction() {
        return param -> new TableCell<>() {
            private final Button btn = new Button("RÉSERVER");
            private final Label lbl = new Label();

            {
                btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
                btn.setOnAction(e -> reserver(getTableView().getItems().get(getIndex())));
                
                lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Seance s = getTableView().getItems().get(getIndex());
                Utilisateur user = UserSession.getInstance().getUtilisateur();

                // 1. Vérifications
                int nbInscrits = resService.getNombrePlacesReservees(s.getId());
                boolean dejaInscrit = resService.isMembreDejaInscrit(user.getId(), s.getId());
                boolean estPleine = nbInscrits >= s.getCapaciteMax();

                // 2. Logique d'affichage
                if (dejaInscrit) {
                    lbl.setText("✅ DÉJÀ INSCRIT");
                    lbl.setStyle("-fx-text-fill: #2980b9;");
                    setGraphic(lbl);
                } 
                else if (estPleine) {
                    lbl.setText("❌ COMPLET");
                    lbl.setStyle("-fx-text-fill: #e74c3c;"); // Rouge
                    setGraphic(lbl);
                } 
                else {
                    setGraphic(btn); // Affiche le bouton vert
                }
            }
        };
    }

    private void reserver(Seance s) {
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        
        // Convertir l'utilisateur générique en Membre (nécessaire pour tes services)
        Membre membre = new Membre();
        membre.setId(user.getId());
        membre.setNom(user.getNom());
        membre.setPrenom(user.getPrenom());
        membre.setEmail(user.getEmail());

        boolean succes = false;

        // 1. Logique selon le type de séance
        if (s.getTypeSeance() == TypeSeance.COLLECTIVE) {
            // C'est ici qu'on utilise ton service spécifique !
            // Cette méthode va vérifier s'il reste de la place et décrémenter le compteur
            succes = collectiveService.reserverPlace(s.getId(), membre);
            
            if (!succes) {
                System.out.println("Erreur : Plus de place ou problème technique.");
                return; // On arrête tout si ça a échoué
            }
        } 
        else if (s.getTypeSeance() == TypeSeance.INDIVIDUELLE) {
            // Logique pour individuelle (souvent différente, peut-être pas de réservation directe ici)
            System.out.println("Réservation individuelle à gérer différemment.");
            return;
        }

        // 2. Si la place a été prise dans la séance, on crée l'historique dans RESERVATION
        if (succes) {
            Reservation r = new Reservation();
            r.setDateReservation(new java.util.Date());
            r.setStatut(StatutReservation.CONFIRMEE); // On confirme direct car placesDisponibles a été décrémenté
            r.setSeance(s);
            r.setMembre(membre);

            resService.creerReservation(r);
            
            // 3. Rafraichir l'affichage
            tablePlanning.refresh();
            System.out.println("Réservation réussie et compteur décrémenté !");
        }
    }
}