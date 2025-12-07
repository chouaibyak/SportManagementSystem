package com.sport.repository;

import com.sport.model.Reservation;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepositoryImpl implements ReservationRepository {

    // Simule la base de données
    private List<Reservation> database = new ArrayList<>();
    private int idCounter = 1;

    @Override
    public Reservation save(Reservation reservation) {
        // Simule l'auto-incrément de l'ID
        reservation.setId(idCounter++); 
        database.add(reservation);
        return reservation;
    }

    @Override
    public Reservation findById(int id) {
        for (Reservation r : database) {
            if (r.getId() == id) {
                return r;
            }
        }
        return null;
    }

    @Override
    public List<Reservation> findAll() {
        return new ArrayList<>(database);
    }

    @Override
    public void update(Reservation reservation) {
        Reservation existing = findById(reservation.getId());
        if (existing != null) {
            // Dans une liste en mémoire, l'objet est mis à jour par référence,
            // mais voici la logique pour remplacer l'objet si nécessaire :
            int index = database.indexOf(existing);
            database.set(index, reservation);
        }
    }

    @Override
    public void delete(int id) {
        Reservation r = findById(id);
        if (r != null) {
            database.remove(r);
        }
    }
}