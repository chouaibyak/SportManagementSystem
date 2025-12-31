package com.sport.service;

import java.util.List;

import com.sport.model.Membre;
import com.sport.repository.MembreRepository;

public class MembreService {

    // On initialise directement ici
    private MembreRepository membreRepository = new MembreRepository();

    
    // Créer un membre avec validation
    public void creerMembre(Membre membre) {
        if (membre.getNom() == null || membre.getNom().isEmpty()) {
            System.out.println("Erreur : Le nom ne peut pas être vide.");
            return;
        }
        membreRepository.ajouterMembre(membre);
    }

    public List<Membre> recupererTousLesMembres() {
        return membreRepository.listerMembres();
    }

    public Membre recupererMembreParId(int id) {
        Membre m = membreRepository.trouverParId(id);
        if (m == null) {
            System.out.println("Service : Aucun membre trouvé avec l'ID " + id);
        }
        return m;
    }

    public void mettreAJourMembre(Membre membre) {
        // Le repository mettra maintenant à jour les infos perso et les objectifs
        membreRepository.modifierMembre(membre);
    }

    public void supprimerMembre(int id) {
        membreRepository.supprimerMembre(id);
    }

      // Méthode publique pour récupérer tous les membres
    public List<Membre> getAllMembres() {
        return membreRepository.listerMembres(); // <-- ici on appelle listerMembres()
    }
}