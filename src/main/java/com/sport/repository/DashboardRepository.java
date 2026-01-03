package com.sport.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import com.sport.utils.DBConnection;

import javafx.scene.chart.XYChart;

public class DashboardRepository {



    // 1️⃣ Total members
    public int countMembers() {
        String sql = "SELECT COUNT(*) FROM membre";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 1️⃣ Total coaches
    public int countCoaches() {
        String sql = "SELECT COUNT(*) FROM coach";
        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    // 2️⃣ Active subscriptions
    public int countActiveSubscriptions() {
        String sql = """
            SELECT COUNT(*) 
            FROM abonnement 
            WHERE date_fin >= CURDATE()
        """;

        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 3️⃣ Today reservations
    public int countTodayReservations() {
        String sql = """
            SELECT COUNT(*) 
            FROM reservation 
            WHERE DATE(dateReservation) = ?
        """;

        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(LocalDate.now()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 4️⃣ Occupancy %
    public int calculateOccupancyPercentage() {
        String sql = """
            SELECT 
            (COUNT(r.id) * 100 / (SELECT COUNT(*) FROM salle)) AS occupancy
            FROM reservation r
            WHERE DATE(r.dateReservation) = CURDATE()
        """;

        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt("occupancy");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    
    // 📈 Reservations per day
    public XYChart.Series<String, Number> reservationsPerDay() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reservations per Day");

        String sql = """
            SELECT DATE(dateReservation) AS jour, COUNT(*) AS total
            FROM reservation
            GROUP BY DATE(dateReservation)
            ORDER BY jour
        """;

        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                series.getData().add(
                    new XYChart.Data<>(
                        rs.getString("jour"),
                        rs.getInt("total")
                    )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return series;
    }

    // 📈 Members sign-up per month
    public XYChart.Series<String, Number> membersPerMonth() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Members per Month");

        String sql = """
            SELECT DATE_FORMAT(date_inscription, '%Y-%m') AS mois, COUNT(*) AS total
            FROM membre
            GROUP BY mois
            ORDER BY mois
        """;

        try (Connection cn = DBConnection.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                series.getData().add(
                    new XYChart.Data<>(
                        rs.getString("mois"),
                        rs.getInt("total")
                    )
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return series;
    }
}
