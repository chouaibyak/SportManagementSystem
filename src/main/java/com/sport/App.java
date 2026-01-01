package com.sport;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Scene scene;
    private static Stage stage; // On garde une référence au stage

    @Override
    public void start(Stage stage) throws IOException {
        App.stage = stage; // On sauvegarde le stage
        
        // On démarre sur le Register pour tester, ou Login selon ton choix
        //String fxmlPath = "/fxml/common/register.fxml"; 
       String fxmlPath = "/fxml/admin/admin_layout.fxml";
     


        URL fxmlUrl = getClass().getResource(fxmlPath);
        if (fxmlUrl == null) {
            System.err.println("ERREUR : Fichier FXML introuvable : " + fxmlPath);
            return;
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        
        
        // CORRECTION ICI :
        // 1. On ne met PAS de taille fixe (400, 550). On laisse le root décider.
        scene = new Scene(root);
        
        stage.setScene(scene);
        stage.setTitle("Sport Club - Authentification");
        
        // 2. On autorise le redimensionnement pour éviter les bugs d'affichage
        stage.setResizable(true);
        
        stage.show();
    }
    
    // --- METHODE UTILITAIRE POUR CHANGER DE PAGE PROPREMENT ---
    public static void setRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/common/" + fxml + ".fxml"));
        Parent root = loader.load();
        
        // On remplace le contenu de la scène
        scene.setRoot(root);
        
        // IMPERATIF : On demande à la fenêtre de s'adapter à la nouvelle taille du contenu
        stage.sizeToScene(); 
        stage.centerOnScreen(); // Optionnel : recentre la fenêtre

         stage.setMaximized(true);
    }

    public static void main(String[] args) {
        launch();
    }
}