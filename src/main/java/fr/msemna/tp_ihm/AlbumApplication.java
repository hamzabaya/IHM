package fr.msemna.tp_ihm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AlbumApplication extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Charger le fichier FXML
            FXMLLoader fxmlLoader = new FXMLLoader(AlbumApplication.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Titre de la fenêtre
            stage.setTitle("Album Photo - TP 2024");

            // Définir la scène et afficher la fenêtre
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            // Gestion des erreurs de chargement du FXML
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du fichier FXML : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
