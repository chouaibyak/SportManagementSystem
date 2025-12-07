package com.sport.repository;

import java.util.ArrayList;
import java.util.List;

import com.sport.model.SeanceIndividuelle;

public class SeanceIndividuelleRepository {

    private List<SeanceIndividuelle> seancesIndividuelles;

    public SeanceIndividuelleRepository() {
        this.seancesIndividuelles = new ArrayList<>();
    }

    // CREATE
    public void ajouter(SeanceIndividuelle seance) {
        seancesIndividuelles.add(seance);
    }

    // READ - get all
    public List<SeanceIndividuelle> getAll() {
        return seancesIndividuelles;
    }

    // READ - by ID
    public SeanceIndividuelle getById(int id) {
        return seancesIndividuelles.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // UPDATE
    public boolean update(SeanceIndividuelle updated) {
        for (int i = 0; i < seancesIndividuelles.size(); i++) {
            if (seancesIndividuelles.get(i).getId() == updated.getId()) {
                seancesIndividuelles.set(i, updated);
                return true;
            }
        }
        return false;
    }

    // DELETE
    public boolean delete(int id) {
        return seancesIndividuelles.removeIf(s -> s.getId() == id);
    }
}
