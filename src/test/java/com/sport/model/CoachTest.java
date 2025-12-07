package com.sport.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CoachTest {

    private Coach coach;

    // Cette méthode sera exécutée avant chaque test pour initialiser un Coach
    @BeforeEach
    public void setUp() {
        coach = new Coach("1", "John", "Doe", new Date(), "john.doe@example.com", "password");
    }

    // Test pour vérifier que le coach a bien un nom
    @Test
    public void testNomCoach() {
        assertEquals("John", coach.getNom(), "Le nom du coach doit être John");
    }

    // Test pour ajouter une spécialité
    @Test
    public void testAjouterSpecialite() {
        assertTrue(coach.ajouterSpecialite("Yoga"), "La spécialité doit être ajoutée.");
        assertTrue(coach.getSpecialites().contains("Yoga"), "La spécialité Yoga devrait être présente.");
    }

    // Test pour affecter une séance au coach
    @Test
    public void testAffecterSeance() {
        Seance seance = new Seance(1, "Séance de Yoga", 20, new Salle(), LocalDateTime.now(), coach, TypeCours.YOGA, 60);
        assertTrue(coach.affecterSeance(seance), "La séance devrait être affectée au coach.");
        assertTrue(coach.getSeances().contains(seance), "La séance devrait être présente dans la liste des séances.");
    }

    // Test pour vérifier que la performance peut être notée
    @Test
    public void testNoterPerformance() {
        Membre membre = new Membre("2", "Jane", "Smith", new Date(), "jane.smith@example.com", "987654321", "456 Elm St");
        Performance performance = new Performance(LocalDateTime.now(), "Excellente", 90.0);
        
        coach.noterPerformance(membre, performance);
        
        assertTrue(membre.getPerformances().contains(performance), "La performance doit être ajoutée au membre.");
    }

    // Test pour vérifier que l'ajout de séance ne se fait pas deux fois
    @Test
    public void testAffecterSeanceExistante() {
        Seance seance = new Seance(1, "Séance de Yoga", 20, new Salle(), LocalDateTime.now(), coach, TypeCours.YOGA, 60);
        
        coach.affecterSeance(seance);
        
        // Essayer d'ajouter la même séance à nouveau
        assertFalse(coach.affecterSeance(seance), "La séance ne doit pas être ajoutée deux fois.");
    }
}
