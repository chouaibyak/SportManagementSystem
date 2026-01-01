package com.sport.controller.coach;

import com.sport.model.*;
import com.sport.repository.SeanceCollectiveRepository;
import com.sport.repository.NotificationRepository;
import com.sport.repository.ReservationRepository;
import com.sport.service.MembreService;
import com.sport.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CoachDashboardController {

    @FXML private Label lblSeances;
    @FXML private Label lblMembres;
    @FXML private Label lblProchaine;
    @FXML private Label lblSeancesTerminees;
    @FXML private BarChart<String, Number> barChart;
    @FXML private VBox notificationsBox;

    private Coach coach;
    private SeanceCollectiveRepository seanceRepo;
    private NotificationRepository notifRepo;
    private MembreService membreService;

    @FXML
    public void initialize() {
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        if (user instanceof Coach) {
            coach = (Coach) user;
            seanceRepo = new SeanceCollectiveRepository();
            notifRepo = new NotificationRepository();
            membreService = new MembreService();
            chargerStats();
            afficherNotifications();

          
        }
    }

    private void afficherNotifications() {
        notificationsBox.getChildren().clear();
        int coachId = coach.getId();
        List<Notification> notifications = notifRepo.getNotificationsByDestinataire(coachId);

        for (Notification n : notifications) {
            Label label = new Label(n.getMessage());
            switch (n.getPriorite()) {
                case BASSE -> label.setStyle("-fx-background-color:#B0BEC5; -fx-text-fill:black; -fx-padding:5 10; -fx-background-radius:5;");
                case NORMALE -> label.setStyle("-fx-background-color:#2196F3; -fx-text-fill:white; -fx-padding:5 10; -fx-background-radius:5;");
                case HAUTE -> label.setStyle("-fx-background-color:#FF5722; -fx-text-fill:white; -fx-padding:5 10; -fx-background-radius:5;");
                case URGENTE -> label.setStyle("-fx-background-color:#F44336; -fx-text-fill:white; -fx-padding:5 10; -fx-background-radius:5;");
                default -> label.setStyle("-fx-background-color:#2196F3; -fx-text-fill:white; -fx-padding:5 10; -fx-background-radius:5;");
            }

            Label dateLabel = new Label(n.getDateEnvoi().toLocalTime().toString());
            dateLabel.setStyle("-fx-text-fill:gray; -fx-font-size:10px;");

            VBox notifBox = new VBox(label, dateLabel);
            notifBox.setSpacing(2);

            // --- Bouton Assigner pour réservation ---
            if ("RESERVATION".equals(n.getType())) {
                Button btnAssigner = new Button("Assigner");
            btnAssigner.setOnAction(e -> {
    Reservation reservation = new NotificationRepository().getReservationForNotification(n);
    if (reservation == null) return;

    SeanceCollective sc = seanceRepo.getById(reservation.getSeance().getId());
    Membre membre = membreService.recupererMembreParId(reservation.getMembre().getId());

    if (sc != null && membre != null) {
        if (sc.getListeMembers().stream().noneMatch(m -> m.getId() == membre.getId())) {
            sc.getListeMembers().add(membre);
            seanceRepo.update(sc);
            btnAssigner.setDisable(true);
            refreshDashboard();
        } else {
            btnAssigner.setDisable(true); // déjà assigné
        }
    }
});


                notifBox.getChildren().add(btnAssigner);
            }

            notificationsBox.getChildren().add(notifBox);
        }
    }

    public void envoyerNotification(int destinataireId, String message, String type, PrioriteNotification priorite, int seanceId, int membreId) {
        Notification notif = new Notification();
        notif.setDestinataireId(destinataireId);
        notif.setMessage(message);
        notif.setType(type);
        notif.setPriorite(priorite);
        notif.setDateEnvoi(LocalDateTime.now());
      // nouveau champ
        notifRepo.ajouter(notif);
        afficherNotifications();

       
    }

    private void chargerStats() {
        List<SeanceCollective> toutesSeances = seanceRepo.getAll().stream()
                .filter(s -> s.getEntraineur().getId() == coach.getId())
                .collect(Collectors.toList());

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        lblSeances.setText(String.valueOf(
                toutesSeances.stream().filter(s -> s.getDateHeure().toLocalDate().isEqual(today)).count()
        ));

        lblMembres.setText(String.valueOf(
                toutesSeances.stream().mapToLong(sc -> sc.getListeMembers() != null ? sc.getListeMembers().size() : 0).sum()
        ));

        SeanceCollective prochaine = toutesSeances.stream()
                .filter(s -> s.getDateHeure().isAfter(now))
                .min((s1, s2) -> s1.getDateHeure().compareTo(s2.getDateHeure()))
                .orElse(null);
        lblProchaine.setText(prochaine != null ? prochaine.getNom() + " à " + prochaine.getDateHeure().toLocalTime() : "Aucune");

        lblSeancesTerminees.setText(String.valueOf(
                toutesSeances.stream()
                        .filter(s -> s.getDateHeure().toLocalDate().isEqual(today))
                        .filter(s -> s.getDateHeure().isBefore(now))
                        .count()
        ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(6 - i);
            long count = toutesSeances.stream()
                    .filter(s -> s.getDateHeure().toLocalDate().isEqual(date))
                    .count();
            series.getData().add(new XYChart.Data<>(date.getDayOfWeek().toString(), count));
        }
        barChart.setData(FXCollections.observableArrayList(series));
    }

    public void refreshDashboard() {
        chargerStats();
        afficherNotifications();
        System.out.println("Dashboard rafraîchi");
    }



}
