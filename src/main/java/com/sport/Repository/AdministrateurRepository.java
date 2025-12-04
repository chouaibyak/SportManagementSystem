package com.sport.repository;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;


import com.sport.model.Coach;
import com.sport.model.Membre;
import com.sport.model.Salle;
import com.sport.model.Equipement;
import com.sport.utils.DBConnection;


public class AdministrateurRepository {

    // Méthodes CRUD 
    public void ajouterMembre(Membre membre) {
        // Requête SQL d'insertion d'un membre dans la table MEMBRE
        String sql = "INSERT INTO MEMBRE (id_utilisateur, objectifSportif, preferences) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, membre.getId());
            stmt.setString(2, membre.getObjectifSportif() != null ? membre.getObjectifSportif().name() : null);
            stmt.setString(3, membre.getPreferences() != null ? membre.getPreferences().name() : null);

            stmt.executeUpdate();

    } catch (SQLException e) {
        System.out.println("Erreur ajout membre : " + e.getMessage());
    }
    }

    public void supprimerMembre(int membreId) {
        // Définir la requête SQL pour supprimer un membre
    String sql = "DELETE FROM MEMBRE WHERE id_utilisateur = ?";

     //Ouvrir la connexion et préparer la requête
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) 
         {

        // Associer l'identifiant du membre à supprimer
        stmt.setInt(1, membreId);

        // Exécuter la suppression dans la BDD
        int lignesSupprimees = stmt.executeUpdate();

        if (lignesSupprimees > 0) {
            System.out.println("Membre supprimé avec succès !");
        } else {
            System.out.println("Aucun membre trouvé avec l'ID : " + membreId);
        }

    } catch (SQLException e) {
        System.out.println("Erreur suppression membre : " + e.getMessage());
    }
    }

    public List<Membre> listerMembres() {
        // Retourne la liste de tous les membres
        return null;
    }

    public void ajouterCoach(Coach coach) {
        // Ajouter un coach dans la BDD
    }

    public void supprimerCoach(int coachId) {
        // Supprimer un coach de la BDD
    }

    public List<Coach> listerCoachs() {
        // Retourne la liste de tous les coachs
        return null;
    }

    public void ajouterEquipement(Equipement equipement) {
        //Implémenter la persistance dans la BDD
    }


    public void supprimerEquipement(int equipementId) {
        // Implémenter la suppression dans la BDD
    }

    
    public List<Equipement> listerEquipements() {
        // Retourner les équipements depuis la BDD
        return null;
    }
}
