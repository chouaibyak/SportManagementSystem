package com.sport.repository;

import com.sport.model.Notification;
import com.sport.model.PrioriteNotification;
import com.sport.model.Reservation;
import com.sport.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {

    // Ajouter une notification
   public void ajouter(Notification notif) {
    String sql = "INSERT INTO Notification (destinataire_id, message, type, priorite, dateEnvoi, reservation_id) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, notif.getDestinataireId());
        stmt.setString(2, notif.getMessage());
        stmt.setString(3, notif.getType());
        stmt.setString(4, notif.getPriorite().name());
        stmt.setTimestamp(5, Timestamp.valueOf(notif.getDateEnvoi()));
        stmt.setInt(6, notif.getReservationId()); // le nouveau champ

        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    // Récupérer notifications pour un coach
    public List<Notification> getNotificationsByDestinataire(int coachId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE destinataire_id = ? ORDER BY dateEnvoi DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, coachId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setDestinataireId(rs.getInt("destinataire_id"));
                n.setMessage(rs.getString("message"));
                n.setType(rs.getString("type"));

             
                String prioriteStr = rs.getString("priorite");
                n.setPriorite(PrioriteNotification.valueOf(prioriteStr));

                n.setDateEnvoi(rs.getTimestamp("dateEnvoi").toLocalDateTime());
                n.setLue(rs.getBoolean("lue"));
                n.setReservationId(rs.getInt("reservation_id")); 


                list.add(n);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Marquer comme lue
    public void marquerCommeLue(int notifId) {
        String sql = "UPDATE notification SET lue = true WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, notifId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public Reservation getReservationForNotification(Notification notif) {
    ReservationRepository resRepo = new ReservationRepository();
    // On suppose que le message contient l'ID de la réservation ou qu'on a un champ reservation_id
    if (notif.getReservationId() > 0) {
        return resRepo.trouverReservationParId(notif.getReservationId());
    }
    return null;
}

}
