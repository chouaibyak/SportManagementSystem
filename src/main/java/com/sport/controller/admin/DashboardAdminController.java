package com.sport.controller.admin;

import com.sport.repository.DashboardRepository;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;

public class DashboardAdminController {

    @FXML private Label membersCountLabel;
    @FXML private Label coachesCountLabel;
    @FXML private LineChart<String, Number> reservationChart;
    @FXML private LineChart<String, Number> memberChart;
    
    @FXML private Label subscriptionsCountLabel;
    @FXML private Label todayReservationsLabel;
    @FXML private Label occupancyLabel;

    private final DashboardRepository dashboardRepo = new DashboardRepository();


    @FXML
    public void initialize() {
        loadKPIs();
        loadCharts();
    }

   private void loadKPIs() {
        membersCountLabel.setText(
            String.valueOf(dashboardRepo.countMembers())
        );

        coachesCountLabel.setText(
            String.valueOf(dashboardRepo.countCoaches())
        );

        subscriptionsCountLabel.setText(
            String.valueOf(dashboardRepo.countActiveSubscriptions())
        );

        todayReservationsLabel.setText(
            String.valueOf(dashboardRepo.countTodayReservations())
        );

        occupancyLabel.setText(
            dashboardRepo.calculateOccupancyPercentage() + "%"
        );
    }

     private void loadCharts() {
        reservationChart.getData().clear();
        memberChart.getData().clear();

        reservationChart.getData().add(
            dashboardRepo.reservationsPerDay()
        );

        memberChart.getData().add(
            dashboardRepo.membersPerMonth()
        );
    }
}
