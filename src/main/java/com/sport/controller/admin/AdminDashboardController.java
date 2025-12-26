package com.sport.controller.admin;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.sport.model.Coach;
import com.sport.model.Equipement;
import com.sport.model.Membre;
import com.sport.model.Rapport;
import com.sport.model.Salle;
import com.sport.model.Utilisateur;
import com.sport.repository.CoachRepository;
import com.sport.repository.EquipementRepository;
import com.sport.repository.MembreRepository;
import com.sport.repository.RapportRepository;
import com.sport.repository.SalleRepository;
import com.sport.utils.UserSession;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class AdminDashboardController {

    @FXML private Label lblCoaches;
    @FXML private Label lblMembers;
    @FXML private Label lblRooms;
    @FXML private Label lblEquipment;
    @FXML private Label lblRapports;
    @FXML private BarChart<String, Number> barChart;
    @FXML private VBox notificationsBox;

    // @FXML private Button btnManageCoaches;
    @FXML private BorderPane mainPane;

    private CoachRepository coachRepo;
    private MembreRepository memberRepo;
    private SalleRepository salleRepo;
    private EquipementRepository equipmentRepo;
    private RapportRepository rapportRepo;

    @FXML
    public void initialize() {
        Utilisateur user = UserSession.getInstance().getUtilisateur();
        if (user != null && "admin".equalsIgnoreCase(user.getRole())) {
            coachRepo = new CoachRepository();
            memberRepo = new MembreRepository();
            salleRepo = new SalleRepository();
            equipmentRepo = new EquipementRepository();
            rapportRepo = new RapportRepository();

            chargerStats();
        }
       
    }

    private void chargerStats() {
        // 1️⃣ Total coaches
        List<Coach> allCoaches = coachRepo.listerCoachs();
        lblCoaches.setText(String.valueOf(allCoaches.size()));

        // 2️⃣ Total members
        List<Membre> allMembers = memberRepo.listerMembres();
        lblMembers.setText(String.valueOf(allMembers.size()));

        // 3️⃣ Total rooms
        List<Salle> allRooms = salleRepo.listerSalles();
        lblRooms.setText(String.valueOf(allRooms.size()));

        // 4️⃣ Total equipment
        List<Equipement> allEquip = equipmentRepo.listerEquipements();
        lblEquipment.setText(String.valueOf(allEquip.size()));

        // 5️⃣ Total rapports
        List<Rapport> allRapports = rapportRepo.listerRapports();
        lblRapports.setText(String.valueOf(allRapports.size()));




        // 6️⃣ Graph: Members added per day (last 7 days)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.minusDays(6 - i);
            long count = allMembers.stream()
               //     .filter(m -> m.getDateInscription().toLocalDate().isEqual(date))
                    .count();
            series.getData().add(new XYChart.Data<>(date.getDayOfWeek().toString(), count));
        }
        barChart.setData(FXCollections.observableArrayList(series));



        // 7️⃣ Notifications: Recent rapports (last 24h)
        notificationsBox.getChildren().clear();
        LocalDateTime now = LocalDateTime.now();
        allRapports.stream()
             //   .filter(r -> r.getDateHeure().isAfter(now.minusHours(24)))
                .forEach(r -> notificationsBox.getChildren().add(
                        new Label("Nouveau rapport: " + r.getType() )
                ));
    }


    


    private void loadSection(String fxmlFile) {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("/fxml/admin/" + fxmlFile + ".fxml"));

            mainPane.setCenter(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
