package com.sport.repository;

import java.util.ArrayList;
import java.util.List;

import com.sport.model.SeanceCollective;

public class SeanceCollectiveRepository {

    private List<SeanceCollective> seancesCollectives;

    public SeanceCollectiveRepository() {
        this.seancesCollectives = new ArrayList<>();
    }

    // CREATE
    public void ajouter(SeanceCollective seance) {
        seancesCollectives.add(seance);
    }

    // READ - get all
    public List<SeanceCollective> getAll() {
        return seancesCollectives;
    }

    // READ - by ID
    public SeanceCollective getById(int id) {
        return seancesCollectives.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // UPDATE
    public boolean update(SeanceCollective updated) {
        for (int i = 0; i < seancesCollectives.size(); i++) {
            if (seancesCollectives.get(i).getId() == updated.getId()) {
                seancesCollectives.set(i, updated);
                return true;
            }
        }
        return false;
    }

    // DELETE
    public boolean delete(int id) {
        return seancesCollectives.removeIf(s -> s.getId() == id);
    }
}
