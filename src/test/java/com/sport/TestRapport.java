package com.sport;

import com.sport.model.Rapport;
import com.sport.service.RapportService;

public class TestRapport {
    
    public static void main(String[] args) {
        System.out.println("=== TEST RAPPORTSERVICE ===\n");
        
        RapportService service = new RapportService();
        
        // Test 1 - Occupation des cours
        System.out.println("1. Rapport Occupation Cours:");
        Rapport r1 = service.genererRapport("OCCUPATION_COURS", "2024-12-01", "2024-12-31");
        System.out.println("   " + r1.getDonnees());
        System.out.println();
        
        // Test 2 - Fréquentation
        System.out.println("2. Rapport Fréquentation:");
        Rapport r2 = service.genererRapport("FREQUENTATION_SALLE", "2024-12-01", "2024-12-31");
        System.out.println("   " + r2.getDonnees());
        System.out.println();
        
        // Test 3 - Satisfaction
        System.out.println("3. Rapport Satisfaction:");
        Rapport r3 = service.genererRapport("SATISFACTION_MEMBRES", "2024-01-01", "2024-12-31");
        System.out.println("   " + r3.getDonnees());
        System.out.println();
        
        // Test 4 - Revenus
        System.out.println("4. Rapport Revenus:");
        Rapport r4 = service.genererRapport("REVENUS_ABONNEMENTS", "2024-01-01", "2024-12-31");
        System.out.println("   " + r4.getDonnees());
        System.out.println();
        
        // Test 5 - Liste
        System.out.println("5. Total rapports en base: " + service.listerRapports().size());
        
        System.out.println("\n=== FIN DES TESTS ===");
    }
}