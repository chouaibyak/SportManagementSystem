package com.sport.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://127.0.0.1:3307/sport_club?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    /**
     * Obtient la connexion à la base de données.
     * Si la connexion n'existe pas ou est fermée, elle sera créée.
     *
     * @return Connection JDBC valide
     * @throws SQLException si la connexion échoue
     */
    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Tentative de connexion à la base de données...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion à la base de données établie avec succès !");
            }
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base de données.");
            System.err.println("Vérifiez que MySQL/EasyPHP est démarré et que les paramètres sont corrects.");
            throw e; // relance l'exception pour que le programme appelant puisse gérer l'erreur
        }
        return connection;
    }

    /**
     * Ferme la connexion si elle est ouverte.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Connexion à la base de données fermée.");
                }
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture de la connexion.");
                e.printStackTrace();
            }
        }
    }
}
