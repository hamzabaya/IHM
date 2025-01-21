package fr.msemna.tp_ihm;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.Optional;

public class AlbumController {
    @FXML
    private Pane albumPane;

    /**
     * Gère la fermeture de l'application après confirmation de l'utilisateur.
     */
    @FXML
    private void handleClose() {
        // Crée une boîte de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quitter l'application");
        alert.setHeaderText("Êtes-vous sûr de vouloir quitter ?");
        alert.setContentText("Toutes les modifications non enregistrées seront perdues.");

        // Affiche la boîte de dialogue et récupère la réponse
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Ferme l'application proprement
            System.exit(0);
        }
    }

    /**
     * Gère l'importation d'une photo depuis un fichier.
     */
    @FXML
    private void handleImportPhoto() {
        // Crée un FileChooser pour choisir une image
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        // Ouvre la boîte de dialogue de sélection de fichier
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            // Charge l'image sélectionnée
            Image image = new Image(file.toURI().toString());

            // Crée une ImageView pour afficher l'image dans le Pane
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100); // Ajustez la taille de l'image
            imageView.setFitHeight(100);

            // Ajoute l'image dans le Pane (vous pouvez ajuster la position si nécessaire)
            albumPane.getChildren().add(imageView);
        }
    }
}
