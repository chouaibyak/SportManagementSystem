package com.sport.model;

import java.time.LocalDateTime;

public class Notification {

    private int id;
    private int destinataireId;
    private String message;
    private String type;
    private PrioriteNotification priorite;
    private LocalDateTime dateEnvoi;

    // Nouveaux champs
   
    private boolean lue;    // Indique si la notification a été lue

private int reservationId; // nouveau champ

public int getReservationId() {
    return reservationId;
}

public void setReservationId(int reservationId) {
    this.reservationId = reservationId;
}

    // ----- Getters & Setters -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDestinataireId() { return destinataireId; }
    public void setDestinataireId(int destinataireId) { this.destinataireId = destinataireId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public PrioriteNotification getPriorite() { return priorite; }
    public void setPriorite(PrioriteNotification priorite) { this.priorite = priorite; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }

 

    public boolean isLue() { return lue; }
    public void setLue(boolean lue) { this.lue = lue; }

}
