package com.sport.controller.member;

import com.sport.model.*;
import com.sport.repository.SeanceRepository;
import com.sport.repository.SeanceCollectiveRepository;
import com.sport.repository.SeanceIndividuelleRepository;
import com.sport.service.ReservationService;
import com.sport.service.SeanceCollectiveService;
import com.sport.service.SeanceIndividuelleService;
import com.sport.utils.UserSession;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MemberPlanningController {

    @FXML private TableView<Seance> tablePlanning;
    @FXML private TableColumn<Seance, String> colDate;
    @FXML private TableColumn<Seance, String> colCours;
    @FXML private TableColumn<Seance, String> colCoach;
    @FXML private TableColumn<Seance, String> colSalle;
    @FXML private TableColumn<Seance, String> colPlaces;
    @FXML private TableColumn<Seance, Void> colAction;

    // Vérifiez que ce nom correspond exactement au fx:id dans le FXML
    @FXML private ComboBox<TypeSeance> cbFiltreType;

    // Repository global pour l'affichage (table 'seance')
    private SeanceRepository seanceRepo = new SeanceRepository();
    
    // Services spécifiques pour la logique métier (tables 'seancecollective' / 'seanceindividuelle')
    private SeanceCollectiveService collectiveService = new SeanceCollectiveService(new SeanceCollectiveRepository());
    private SeanceIndividuelleService indivService = new SeanceIndividuelleService(new SeanceIndividuelleRepository());
    
    private ReservationService resService = new ReservationService();

    private DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");

    @FXML
    public void initialize() {
        System.out.println("--- INITIALISATION ---");

        if (cbFiltreType != null) {
            // 1. Remplir la liste
            cbFiltreType.setItems(FXCollections.observableArrayList(TypeSeance.values()));

            // --- AJOUT CORRECTIF : Dire à JavaFX comment afficher l'Enum ---
            
            // A. Pour la liste déroulante
            cbFiltreType.setCellFactory(param -> new ListCell<TypeSeance>() {
                @Override
                protected void updateItem(TypeSeance item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.toString()); // Affiche "COLLECTIVE" ou "INDIVIDUELLE"
                    }
                }
            });

            // B. Pour le bouton sélectionné (quand la liste est fermée)
            cbFiltreType.setButtonCell(new ListCell<TypeSeance>() {
                @Override
                protected void updateItem(TypeSeance item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                }
            });
            // -------------------------------------------------------------

            // 2. Sélectionner par défaut
            cbFiltreType.getSelectionModel().selectFirst();

            // 3. Ecouteur
            cbFiltreType.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) chargerDonnees(newVal);
            });

            // Charger les données initiales
            if(cbFiltreType.getValue() != null) {
                chargerDonnees(cbFiltreType.getValue());
            }

        } else {
            System.err.println("ERREUR : cbFiltreType est NULL");
        }

        configurerColonnes();
    }

    private void chargerDonnees(TypeSeance typeFiltre) {
        System.out.println("--- DEBUG CHARGEMENT ---");
        
        // 1. Récupération brute
        List<Seance> toutesLesSeances = seanceRepo.getSeancesParPeriode(LocalDate.now(), LocalDate.now().plusDays(7));
        
        System.out.println("Nombre total trouvé (avant filtre) : " + toutesLesSeances.size());

        // 2. Vérification du contenu
        for (Seance s : toutesLesSeances) {
            System.out.println("Seance trouvée : " + s.getNom());
            System.out.println(" - Date : " + s.getDateHeure());
            System.out.println(" - TypeSeance (Java) : " + s.getTypeSeance()); // <--- SI C'EST NULL, C'EST LE PROBLÈME
        }

        // 3. Le Filtre
        List<Seance> seancesFiltrees = toutesLesSeances.stream()
                .filter(s -> s.getTypeSeance() == typeFiltre)
                .collect(Collectors.toList());
                
        System.out.println("Reste après filtre (" + typeFiltre + ") : " + seancesFiltrees.size());

        tablePlanning.setItems(FXCollections.observableArrayList(seancesFiltrees));
    }

    private void configurerColonnes() {
        colDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDateHeure().format(fmt)));
        colCours.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getNom()));
        
        colCoach.setCellValueFactory(cell -> {
            if(cell.getValue().getEntraineur() != null) return new SimpleStringProperty(cell.getValue().getEntraineur().getNom());
            return new SimpleStringProperty("-");
        });

        colSalle.setCellValueFactory(cell -> {
            if(cell.getValue().getSalle() != null) return new SimpleStringProperty(cell.getValue().getSalle().getNom());
            return new SimpleStringProperty("-");
        });

        // Colonne Disponibilité (Logique différente selon le type)
        colPlaces.setCellValueFactory(cell -> {
            Seance s = cell.getValue();
            
            // Pour COLLECTIVE : On vérifie la capacité max vs nb inscrits
            if (s.getTypeSeance() == TypeSeance.COLLECTIVE) {
                int pris = resService.getNombrePlacesReservees(s.getId());
                int reste = s.getCapaciteMax() - pris;
                if (reste <= 0) return new SimpleStringProperty("COMPLET");
                return new SimpleStringProperty(reste + " places");
            } 
            // Pour INDIVIDUELLE : C'est binaire (Libre ou Pris)
            else {
                // On vérifie si la séance individuelle a déjà été réservée dans la table de liaison
                // (Astuce : on regarde si capacityMax (souvent 1) est atteinte)
                int pris = resService.getNombrePlacesReservees(s.getId());
                if (pris > 0) return new SimpleStringProperty("RÉSERVÉE");
                return new SimpleStringProperty("LIBRE");
            }
        });

        colAction.setCellFactory(creerBoutonAction());
    }

    private Callback<TableColumn<Seance, Void>, TableCell<Seance, Void>> creerBoutonAction() {
        return param -> new TableCell<>() {
            private final Button btn = new Button("RÉSERVER");
            private final Label lbl = new Label();

            {
                btn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-font-weight: bold;");
                btn.setOnAction(e -> handleReservation(getTableView().getItems().get(getIndex())));
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

                // Vérifications de base
                int nbInscrits = resService.getNombrePlacesReservees(s.getId());
                boolean dejaInscrit = resService.isMembreDejaInscrit(user.getId(), s.getId());
                boolean estPleine = nbInscrits >= s.getCapaciteMax();

                if (dejaInscrit) {
                    lbl.setText("✅ INSCRIT");
                    lbl.setStyle("-fx-text-fill: #2980b9;");
                    setGraphic(lbl);
                } 
                else if (estPleine) {
                    lbl.setText("❌ INDISPONIBLE");
                    lbl.setStyle("-fx-text-fill: #e74c3c;");
                    setGraphic(lbl);
                } 
                else {
                    setGraphic(btn);
                }
            }
        };
    }

    // --- C'est ici que l'on gère la différence entre les tables ---
    private void handleReservation(Seance s) {
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        
        // Convertir l'Utilisateur en Membre (nécessaire pour les repositories)
        Membre membre = new Membre();
        membre.setId(user.getId());
        membre.setNom(user.getNom());
        membre.setPrenom(user.getPrenom());
        membre.setEmail(user.getEmail());

        boolean succes = false;

        // CAS 1 : SÉANCE COLLECTIVE
        if (s.getTypeSeance() == TypeSeance.COLLECTIVE) {
            System.out.println("Tentative réservation Collective ID: " + s.getId());
            // Appel au repository spécifique (table seancecollective)
            // Cela va décrémenter 'placesDisponibles'
            succes = collectiveService.reserverPlace(s.getId(), membre);
        } 
        
        // CAS 2 : SÉANCE INDIVIDUELLE
        else if (s.getTypeSeance() == TypeSeance.INDIVIDUELLE) {
            System.out.println("Tentative réservation Individuelle ID: " + s.getId());
            // Appel au repository spécifique (table seanceindividuelle)
            // Cela va assigner le membre à la colonne 'membre_id'
            succes = indivService.assignerMembre(s.getId(), membre);
        }

        if (succes) {
            // Dans les DEUX cas, on crée aussi une trace dans la table globale RESERVATION
            // Cela permet d'avoir un historique unifié
            Reservation r = new Reservation();
            r.setDateReservation(new java.util.Date());
            r.setStatut(StatutReservation.CONFIRMEE);
            r.setSeance(s);
            r.setMembre(membre);

            resService.creerReservation(r);
            
            // Message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Réservation effectuée !");
            alert.setContentText("Vous êtes inscrit à la séance : " + s.getNom());
            alert.showAndWait();

            // Rafraîchir le tableau pour mettre à jour les boutons et places
            chargerDonnees(cbFiltreType.getValue());
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Échec de la réservation");
            alert.setContentText("Impossible de réserver. La séance est peut-être complète ou une erreur est survenue.");
            alert.showAndWait();
        }
    }
}