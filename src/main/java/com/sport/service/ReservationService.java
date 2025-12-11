package com.sport.service;

import com.sport.model.Reservation;
import com.sport.model.StatutReservation; // Assurez-vous d'avoir cet Enum
import com.sport.repository.ReservationRepository;

import java.util.Date;
import java.util.List;

public class ReservationService {

    private ReservationRepository reservationRepository = new ReservationRepository();

    // --- Méthodes Métier ---

    public void creerReservation(Reservation reservation) {
        // Règle métier : Date obligatoire
        if (reservation.getDateReservation() == null) {
            reservation.setDateReservation(new Date());
        }
        // Règle métier : Statut par défaut si vide
        if (reservation.getStatut() == null) {
            reservation.setStatut(StatutReservation.EN_ATTENT);
        }

        // Appel au repository pour sauvegarder
        reservationRepository.ajouterReservation(reservation);
        System.out.println("Réservation créée avec l'ID : " + reservation.getId());
    }

    public List<Reservation> listerToutesLesReservations() {
        return reservationRepository.listerReservations();
    }

    public Reservation trouverReservation(int id) {
        return reservationRepository.trouverReservationParId(id);
    }

    /**
     * Change le statut à PRESENT (Logique du diagramme)
     */
    public void marquerPresence(int id) {
        Reservation reservation = reservationRepository.trouverReservationParId(id);

        if (reservation != null) {
            reservation.marquerPresence(); // Utilise la méthode de votre Model
            reservationRepository.modifierReservation(reservation); // Sauvegarde le changement
            System.out.println("Présence validée pour la réservation " + id);
        } else {
            System.out.println("Erreur : Réservation introuvable.");
        }
    }

    /**
     * Annule une réservation
     */
    public void annulerReservation(int id) {
        Reservation reservation = reservationRepository.trouverReservationParId(id);

        if (reservation != null) {
            reservation.setStatut(StatutReservation.ANNULEE);
            reservationRepository.modifierReservation(reservation);
            System.out.println("Réservation " + id + " annulée.");
        } else {
            System.out.println("Erreur : Réservation introuvable.");
        }
    }
}