package com.sport.service;

import java.time.LocalDate;
import java.util.List;

import com.sport.model.Coach;
import com.sport.model.Equipement;
import com.sport.model.EtatEquipement;
import com.sport.model.Membre;
import com.sport.model.Salle;
import com.sport.model.TypeSalle;
import com.sport.repository.CoachRepository;
import com.sport.repository.EquipementRepository;
import com.sport.repository.MembreRepository;
import com.sport.repository.SalleRepository;
import com.sport.repository.RapportRepository;

import com.sport.model.Rapport;

public class AdministrateurService {

    
    private MembreRepository membreRepository = new MembreRepository();
    private CoachRepository coachRepository = new CoachRepository();
    private EquipementRepository equipementRepository = new EquipementRepository();
    private SalleRepository salleRepository = new SalleRepository();
    private RapportService rapportService = new RapportService();
    private RapportRepository rapportRepository = new RapportRepository();
    
    // =========================
    // MEMBRES
    // =========================
    public void ajouterMembre(Membre membre) {
        membreRepository.ajouterMembre(membre);
    }

    public void modifierMembre(Membre membre) {
        membreRepository.modifierMembre(membre);
    }

    public void supprimerMembre(int membreId) {
        membreRepository.supprimerMembre(membreId);
    }

    public Membre getMembre(int membreId) {
        return membreRepository.trouverParId(membreId);
    }

    public List<Membre> listerMembres() {
        return membreRepository.listerMembres();
    }

    // =========================
    // COACHS
    // =========================
    public void ajouterCoach(Coach coach) {
        coachRepository.ajouterCoach(coach);
    }

    public void modifierCoach(Coach coach) {
        coachRepository.modifierCoach(coach);
    }

    public void supprimerCoach(int coachId) {
        coachRepository.supprimerCoach(coachId);
    }

    public Coach getCoach(int coachId) {
        return coachRepository.getCoachById(coachId);
    }

    public List<Coach> listerCoachs() {
        return coachRepository.listerCoachs();
    }

    // =========================
    // EQUIPEMENTS
    // =========================
    public void ajouterEquipement(Equipement equipement) {
        equipementRepository.ajouterEquipement(equipement);
    }

    public void modifierEquipement(Equipement equipement) {
        equipementRepository.modifierEquipement(equipement);
    }

    public void supprimerEquipement(int equipementId) {
        equipementRepository.supprimerEquipement(equipementId);
    }

    public Equipement getEquipement(int equipementId) {
        return equipementRepository.getEquipementById(equipementId);
    }

    public List<Equipement> listerEquipements() {
        return equipementRepository.listerEquipements();
    }

    public List<Equipement> listerEquipementsParEtat(EtatEquipement etat) {
        return equipementRepository.listerEquipementsParEtat(etat);
    }

    // =========================
    // SALLES
    // =========================
    public void ajouterSalle(Salle salle) {
        salleRepository.ajouterSalle(salle);
    }

    public void modifierSalle(Salle salle) {
        salleRepository.modifierSalle(salle);
    }

    public void supprimerSalle(int salleId) {
        salleRepository.supprimerSalle(salleId);
    }

    public Salle getSalle(int salleId) {
        return salleRepository.getSalleById(salleId);
    }

    public List<Salle> listerSalles() {
        return salleRepository.listerSalles();
    }

    public List<Salle> listerSallesParType(TypeSalle type) {
        return salleRepository.listerSallesParType(type);
    }

    public boolean verifierDisponibiliteSalle(int salleId, String dateHeure) {
        return salleRepository.verifierDisponibiliteSalle(salleId, dateHeure);
    }

    // =========================
    // RAPPORTS
    // =========================
   public Rapport genererRapport(String type, String dateDebut, String dateFin) {
        return rapportService.genererRapport(type, dateDebut, dateFin);
    }

    public List<Rapport> listerRapports() {
        return rapportService.listerRapports();
    }

    public void supprimerRapport(int rapportId) {
        rapportService.supprimerRapport(rapportId);
    }

   
}