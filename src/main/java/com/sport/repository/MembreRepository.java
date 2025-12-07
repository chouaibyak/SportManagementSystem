package com.sport.repository;

import com.sport.model.Membre;
import java.util.ArrayList;
import java.util.List;

public class MembreRepository {

    // Simulation d'une BDD en mémoire (List)
    private static List<Membre> membresBDD = new ArrayList<>();
    private static int compteurId = 1; // Pour simuler l'auto-incrément

    // CREATE
    public Membre ajouter(Membre membre) {
        membre.setId(compteurId++); // Simule l'ID généré par la BDD
        membresBDD.add(membre);
        return membre;
    }

    // READ (Tout)
    public List<Membre> listerTout() {
        return new ArrayList<>(membresBDD); // Retourne une copie
    }

    // READ (Par ID)
    public Membre trouverParId(int id) {
        for (Membre m : membresBDD) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    // UPDATE
    public Membre modifier(Membre membreModifie) {
        for (int i = 0; i < membresBDD.size(); i++) {
            if (membresBDD.get(i).getId() == membreModifie.getId()) {
                membresBDD.set(i, membreModifie);
                return membreModifie;
            }
        }
        return null; // Pas trouvé
    }

    // DELETE
    public boolean supprimer(int id) {
        return membresBDD.removeIf(m -> m.getId() == id);
    }
}