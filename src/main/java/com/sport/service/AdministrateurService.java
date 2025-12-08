package com.sport.service;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import com.sport.model.*;
import com.sport.repository.AdministrateurRepository;

public class AdministrateurService {

    private AdministrateurRepository repo;

    public AdministrateurService() {
        this.repo = new AdministrateurRepository();
    }

    // =========================
    // MEMBRES
    // =========================
    public void ajouterMembre(Membre membre) {
        // ici tu peux ajouter une validation avant d'ajouter
        repo.ajouterMembre(membre);
    }

    public void modifierMembre(Membre membre) {
        repo.modifierMembre(membre);
    }

    public void supprimerMembre(int membreId) {
        repo.supprimerMembre(membreId);
    }

    public Membre getMembre(int membreId) {
        return repo.getMembre(membreId);
    }

    public List<Membre> listerMembres() {
        return repo.listerMembres();
    }

    // =========================
    // COACHS
    // =========================
    public void ajouterCoach(Coach coach) {
        repo.ajouterCoach(coach);
    }

    public void modifierCoach(Coach coach) {
        repo.modifierCoach(coach);
    }

    public void supprimerCoach(int coachId) {
        repo.supprimerCoach(coachId);
    }

    public Coach getCoach(int coachId) {
        return repo.getCoach(coachId);
    }

    public List<Coach> listerCoachs() {
        return repo.listerCoachs();
    }

    public List<Coach> listerCoachsParDomaine(String domaine) {
        return repo.listerCoachsParDomaine(domaine);
    }

    // =========================
    // EQUIPEMENTS
    // =========================
    public void ajouterEquipement(Equipement equipement) {
        repo.ajouterEquipement(equipement);
    }

    public void modifierEquipement(Equipement equipement) {
        repo.modifierEquipement(equipement);
    }

    public void supprimerEquipement(int equipementId) {
        repo.supprimerEquipement(equipementId);
    }

    public Equipement getEquipement(int equipementId) {
        return repo.getEquipement(equipementId);
    }

    public List<Equipement> listerEquipements() {
        return repo.listerEquipements();
    }

    public List<Equipement> listerEquipementsParEtat(Equipement.EtatEquipement etat) {
        return repo.listerEquipementsParEtat(etat);
    }

    // =========================
    // SALLES
    // =========================
    public void ajouterSalle(Salle salle) {
        repo.ajouterSalle(salle);
    }

    public void modifierSalle(Salle salle) {
        repo.modifierSalle(salle);
    }

    public void supprimerSalle(int salleId) {
        repo.supprimerSalle(salleId);
    }

    public Salle getSalle(int salleId) {
        return repo.getSalle(salleId);
    }

    public List<Salle> listerSalles() {
        return repo.listerSalles();
    }

    public List<Salle> listerSallesParType(Salle.TypeSalle type) {
        return repo.listerSallesParType(type);
    }

    public boolean verifierDisponibiliteSalle(int salleId, Date date, Time heureDebut, Time heureFin) {
        return repo.verifierDisponibiliteSalle(salleId, date, heureDebut, heureFin);
    }

    // =========================
    // RAPPORTS
    // =========================
    public void genererRapport(Rapport.TypeRapport type, Date dateDebut, Date dateFin) {
        // Ici, on délègue la génération au repository
        repo.genererRapport(type, dateDebut, dateFin);
    }
}
