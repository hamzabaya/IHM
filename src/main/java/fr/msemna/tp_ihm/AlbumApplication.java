package fr.msemna.tp_ihm;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AlbumApplication extends Application {

    @Override
    public void start(Stage stage) {
        try {
            
            FXMLLoader fxmlLoader = new FXMLLoader(AlbumApplication.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());


            stage.setTitle("Album Photo - TP 2024");


            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement du fichier FXML : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
