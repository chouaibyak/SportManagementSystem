package com.sport.service;

import com.sport.model.Membre;
import com.sport.repository.MembreRepository;
import java.util.List;

public class MembreService {

    private MembreRepository membreRepository;

    public MembreService() {
        this.membreRepository = new MembreRepository();
    }

    // Créer un membre avec validation
    public void creerMembre(Membre membre) {
        if (membre.getNom() == null || membre.getNom().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide.");
        }
        // Ici, on pourrait vérifier si l'email existe déjà, etc.
        membreRepository.ajouter(membre);
        System.out.println("Service : Membre " + membre.getNom() + " ajouté avec succès.");
    }

    public List<Membre> recupererTousLesMembres() {
        return membreRepository.listerTout();
    }

    public Membre recupererMembreParId(int id) {
        Membre m = membreRepository.trouverParId(id);
        if (m == null) {
            System.out.println("Service : Aucun membre trouvé avec l'ID " + id);
        }
        return m;
    }

    public void mettreAJourMembre(Membre membre) {
        Membre existant = membreRepository.modifier(membre);
        if (existant != null) {
            System.out.println("Service : Membre mis à jour.");
        } else {
            System.out.println("Service : Échec de la mise à jour, membre introuvable.");
        }
    }

    public void supprimerMembre(int id) {
        boolean supprime = membreRepository.supprimer(id);
        if (supprime) {
            System.out.println("Service : Membre supprimé.");
        } else {
            System.out.println("Service : Impossible de supprimer, ID introuvable.");
        }
    }
}