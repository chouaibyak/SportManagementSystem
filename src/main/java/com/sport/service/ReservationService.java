package com.sport.service;

import com.sport.model.Reservation;
import com.sport.model.StatutReservation; // Assurez-vous d'avoir cet Enum
import com.sport.repository.ReservationRepository;

import java.util.Date;
import java.util.List;

public class ReservationService {

    private final ReservationRepository reservationRepository;

    // Injection du repository via le constructeur
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

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
        reservationRepository.save(reservation);
        System.out.println("Réservation créée avec l'ID : " + reservation.getId());
    }

    public List<Reservation> listerToutesLesReservations() {
        return reservationRepository.findAll();
    }

    public Reservation trouverReservation(int id) {
        return reservationRepository.findById(id);
    }

    /**
     * Change le statut à PRESENT (Logique du diagramme)
     */
    public void marquerPresence(int id) {
        Reservation reservation = reservationRepository.findById(id);

        if (reservation != null) {
            reservation.marquerPresence(); // Utilise la méthode de votre Model
            reservationRepository.update(reservation); // Sauvegarde le changement
            System.out.println("Présence validée pour la réservation " + id);
        } else {
            System.out.println("Erreur : Réservation introuvable.");
        }
    }

    /**
     * Annule une réservation
     */
    public void annulerReservation(int id) {
        Reservation reservation = reservationRepository.findById(id);

        if (reservation != null) {
            reservation.setStatut(StatutReservation.ANNULEE);
            reservationRepository.update(reservation);
            System.out.println("Réservation " + id + " annulée.");
        } else {
            System.out.println("Erreur : Réservation introuvable.");
        }
    }
}