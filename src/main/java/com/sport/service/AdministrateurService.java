package com.sport.service;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import com.sport.model.*;
import com.sport.repository.MembreRepository;
import com.sport.repository.CoachRepository;
import com.sport.repository.SalleRepository;
import com.sport.repository.EquipementRepository;




public class AdministrateurService {

    private  MembreRepository membreRepo= new MembreRepository();
    private  CoachRepository coachRepo = new CoachRepository();
    private  SalleRepository salleRepo = new SalleRepository();
    private  EquipementRepository equipRepo = new EquipementRepository();

    // =========================
    // MEMBRES
    // =========================
    public void ajouterMembre(Membre membre) {
        // ici tu peux ajouter une validation avant d'ajouter
        membreRepo.ajouter(membre);
    }

    public void modifierMembre(Membre membre) {
        membreRepo.modifierMembre(membre);
    }

    public void supprimerMembre(int membreId) {
        membreRepo.supprimerMembre(membreId);
    }

    public Membre getMembre(int membreId) {
        return membreRepo.getMembre(membreId);
    }

    public List<Membre> listerMembres() {
        return membreRepo.listerMembres();
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
