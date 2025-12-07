package com.sport.repository;

import com.sport.model.Reservation;
import java.util.List;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Reservation findById(int id);
    List<Reservation> findAll();
    void update(Reservation reservation);
    void delete(int id);
}