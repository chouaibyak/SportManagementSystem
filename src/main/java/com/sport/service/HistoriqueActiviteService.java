package com.sport.service;

import java.time.LocalDate;
import java.util.List;

import com.sport.model.HistoriqueActivite;
import com.sport.model.Membre;
import com.sport.model.TypeSeance;
import com.sport.repository.HistoriqueActiviteRepository;


public class HistoriqueActiviteService {

    private HistoriqueActiviteRepository historiqueRepository;

    
    public HistoriqueActiviteService() {
        this.historiqueRepository = new HistoriqueActiviteRepository();
    }
    
    // Enregistrer une nouvelle activité
    public HistoriqueActivite enregistrerActivite(Membre membre, TypeSeance typeSeance, int duree, LocalDate date, String notes) {
        // Validation
        if (membre == null || membre.getId() == 0) {
            System.out.println("Erreur: Membre invalide");
            return null;
        }
        
        if (typeSeance == null) {
            System.out.println("Erreur: Type de séance requis");
            return null;
        }
        
        if (duree <= 0) {
            System.out.println("Erreur: Durée doit être positive");
            return null;
        }
        
        if (date == null) {
            System.out.println("Erreur: Date requise");
            return null;
        }
        
        // Créer et sauvegarder
        HistoriqueActivite historique = new HistoriqueActivite(membre, typeSeance, duree, date, notes);
        historiqueRepository.ajouterHistoriqueActivite(historique);
        
        System.out.println("✓ Activité enregistrée: " + typeSeance + " - " + duree + " min");
        return historique;
    }
    
    // Ajouter une note à un historique existant
    public void ajouterNote(int historiqueId, String note) {
        HistoriqueActivite historique = historiqueRepository.trouverParId(historiqueId);
        
        if (historique == null) {
            System.out.println("Erreur: Historique non trouvé");
            return;
        }
        
        if (note == null || note.trim().isEmpty()) {
            System.out.println("Erreur: Note vide");
            return;
        }
        
        // Utiliser la méthode métier du modèle
        historique.ajouterNote(note);
        
        // Sauvegarder
        historiqueRepository.modifierHistoriqueActivite(historique);
        System.out.println("✓ Note ajoutée à l'historique ID=" + historiqueId);
    }
    
    // Consulter les notes d'un historique
    public String consulterNotes(int historiqueId) {
        HistoriqueActivite historique = historiqueRepository.trouverParId(historiqueId);
        
        if (historique == null) {
            System.out.println("Erreur: Historique non trouvé");
            return null;
        }
        
        return historique.consulterNote();
    }
    
    // Obtenir l'historique complet d'un membre
    public List<HistoriqueActivite> obtenirHistoriqueMembre(int membreId) {
        return historiqueRepository.trouverParMembre(membreId);
    }
    
   // Modifier une activité
    public void modifierActivite(int historiqueId, TypeSeance nouveauType, 
                                 int nouvelleDuree, LocalDate nouvelleDate, String notes) {
        HistoriqueActivite historique = historiqueRepository.trouverParId(historiqueId);
        
        if (historique == null) {
            System.out.println("Erreur: Historique non trouvé");
            return;
        }
        
        // Mettre à jour les champs
        if (nouveauType != null) historique.setTypeSeance(nouveauType);
        if (nouvelleDuree > 0) historique.setDuree(nouvelleDuree);
        if (nouvelleDate != null) historique.setDate(nouvelleDate);
        if (notes != null) historique.setNotes(notes);
        
        
        // Sauvegarder les modifications
        historiqueRepository.modifierHistoriqueActivite(historique);
        System.out.println("✓ Activité ID=" + historiqueId + " modifiée");
    }
    
    // Supprimer tout l'historique d'un membre
    // Obtenir toutes les activités
    public List<HistoriqueActivite> obtenirToutesLesActivites() {
        return historiqueRepository.listerHistoriquesActivite();
    }
}