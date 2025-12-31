package com.sport.controller.coach;

import com.sport.model.Coach;
import com.sport.model.Seance;
import com.sport.model.SeanceCollective;
import com.sport.model.Utilisateur;
import com.sport.repository.SeanceCollectiveRepository;
import com.sport.utils.UserSession;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

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

    @FXML
    public void initialize() {
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        if (user != null && user instanceof Coach) {
            coach = (Coach) user;
            seanceRepo = new SeanceCollectiveRepository();
            chargerStats();


          
        }
    }

    private void chargerStats() {
        List<SeanceCollective> toutesSeances = seanceRepo.getAll().stream()
                .filter(s -> s.getEntraineur().getId() == coach.getId())
                .collect(Collectors.toList());

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        // Séances aujourd'hui
        long countToday = toutesSeances.stream()
                .filter(s -> s.getDateHeure().toLocalDate().isEqual(today))
                .count();
        lblSeances.setText(String.valueOf(countToday));

        // Membres suivis
        long membresCount = toutesSeances.stream()
                .mapToLong(sc -> sc.getListeMembers() != null ? sc.getListeMembers().size() : 0)
                .sum();
        lblMembres.setText(String.valueOf(membresCount));

        // Prochaine séance
        SeanceCollective prochaine = toutesSeances.stream()
                .filter(s -> s.getDateHeure().isAfter(now))
                .min((s1, s2) -> s1.getDateHeure().compareTo(s2.getDateHeure()))
                .orElse(null);
        lblProchaine.setText(prochaine != null ?
                prochaine.getNom() + " à " + prochaine.getDateHeure().toLocalTime() : "Aucune");

        // Séances terminées aujourd'hui
        long countTerminees = toutesSeances.stream()
                .filter(s -> s.getDateHeure().toLocalDate().isEqual(today))
                .filter(s -> s.getDateHeure().isBefore(now))
                .count();
        lblSeancesTerminees.setText(String.valueOf(countTerminees));

       

        // Graphique : Séances par jour (7 derniers jours)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(6 - i);
            long count = toutesSeances.stream()
                    .filter(s -> s.getDateHeure().toLocalDate().isEqual(date))
                    .count();
            series.getData().add(new XYChart.Data<>(date.getDayOfWeek().toString(), count));
        }
        barChart.setData(FXCollections.observableArrayList(series));

        // Notifications : séances dans l'heure
        notificationsBox.getChildren().clear();
        toutesSeances.stream()
                .filter(s -> s.getDateHeure().isAfter(now) && s.getDateHeure().isBefore(now.plusHours(1)))
                .forEach(s -> notificationsBox.getChildren().add(
                        new Label("Prochaine séance: " + s.getNom() + " à " + s.getDateHeure().toLocalTime())
                ));
    }

    private void refreshDashboard() {
        chargerStats();
        System.out.println("Dashboard rafraîchi");
    }
}
