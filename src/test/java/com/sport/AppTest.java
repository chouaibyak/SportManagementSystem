package com.sport;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

import com.sport.utils.DBConnection;

public class AppTest {

    @Test
    public void testDatabaseConnection() {
        System.out.println("\n--- Exécution du Test d'Intégration de Connexion BDD ---");

        try (Connection connection = DBConnection.getConnection()) { // Auto-close
            assertNotNull("La connexion à la base de données est nulle. Vérifiez EasyPHP/MySQL et la BDD 'sport_club'.", connection);

            if (!connection.isValid(1)) {
                throw new AssertionError("La connexion a été établie, mais elle n'est pas valide (fermée ou expirée).");
            }

            System.out.println("✅ TEST RÉUSSI : Connexion établie et valide.");
        } catch (SQLException e) {
            System.err.println("❌ TEST ÉCHOUÉ : Une erreur SQL est survenue. Détails : " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }
}
