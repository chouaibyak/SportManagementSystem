/*package com.sport;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // --- ETAPE 1 : DEFINIR LE POINT D'ENTRÉE ---
        // On démarre obligatoirement sur le LOGIN
        // Vérifie bien que tu as créé le fichier dans : src/main/resources/fxml/common/login.fxml
        String fxmlPath = "/fxml/common/register.fxml"; 
        
        System.out.println("Démarrage de l'application...");
        
        // --- ETAPE 2 : VERIFICATION DU CHEMIN (Sécurité) ---
        URL fxmlUrl = getClass().getResource(fxmlPath);
        if (fxmlUrl == null) {
            System.err.println("ERREUR FATALE : Impossible de trouver le fichier FXML !");
            System.err.println("Chemin cherché : " + fxmlPath);
            System.err.println("Vérifie que le dossier 'resources' est bien marqué comme 'Resources Root' et que le fichier existe.");
            return; // On stoppe tout pour éviter le crash violent
        }

        System.out.println("Chargement de la vue : " + fxmlUrl);

        // --- ETAPE 3 : CHARGEMENT ET AFFICHAGE ---
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        
        // On définit une taille plus petite pour le login (ex: 600x450) c'est plus élégant
        scene = new Scene(root, 600, 450);
        
        stage.setScene(scene);
        stage.setTitle("Sport Club - Authentification");
        
        // Optionnel : Empêcher l'utilisateur d'agrandir la fenêtre de login (souvent plus propre)
        stage.setResizable(false);
        
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
    */