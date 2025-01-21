package fr.msemna.tp_ihm;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.stage.FileChooser;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AlbumController {
    @FXML
    private Pane albumPane;

    // List to store images and their corresponding ImageViews
    private List<Image> images = new ArrayList<>();
    private List<ImageView> imageViews = new ArrayList<>();

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

            // Calculer l'aspect ratio de l'image
            double imageRatio = image.getWidth() / image.getHeight();

            // Appliquer un effet de shadow à l'ImageView
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(10); // Rayon de l'ombre
            dropShadow.setOffsetX(5); // Décalage horizontal
            dropShadow.setOffsetY(5); // Décalage vertical
            dropShadow.setColor(javafx.scene.paint.Color.GRAY); // Couleur de l'ombre

            // Appliquer l'ombre à l'ImageView
            imageView.setEffect(dropShadow);

            // Ajouter un décalage constant pour éviter le chevauchement
            double offsetX = 20 * imageViews.size(); // Décalage horizontal en fonction de l'index
            double offsetY = 20 * imageViews.size(); // Décalage vertical en fonction de l'index

            // Vérifier que l'image reste dans les limites du Pane
            if (offsetX + 100 > albumPane.getWidth()) {
                offsetX = 10; // Réinitialiser l'offset horizontal
            }
            if (offsetY + 100 > albumPane.getHeight()) {
                offsetY = 10; // Réinitialiser l'offset vertical
            }

            imageView.setLayoutX(offsetX);
            imageView.setLayoutY(offsetY);

            // Ajuster la taille initiale de l'image
            resizeImageView(albumPane.getWidth(), albumPane.getHeight(), imageRatio, imageView);

            // Ajoute l'image à la liste des images et l'imageView à la liste des ImageViews
            images.add(image);
            imageViews.add(imageView);

            // Ajoute l'image dans le Pane
            albumPane.getChildren().add(imageView);

            // Ajouter des listeners pour ajuster la taille de l'image lorsque la taille du Pane change
            albumPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                for (int i = 0; i < imageViews.size(); i++) {
                    resizeImageView(newValue.doubleValue(), albumPane.getHeight(), imageViews.get(i).getImage().getWidth() / imageViews.get(i).getImage().getHeight(), imageViews.get(i));
                }
            });

            albumPane.heightProperty().addListener((observable, oldValue, newValue) -> {
                for (int i = 0; i < imageViews.size(); i++) {
                    resizeImageView(albumPane.getWidth(), newValue.doubleValue(), imageViews.get(i).getImage().getWidth() / imageViews.get(i).getImage().getHeight(), imageViews.get(i));
                }
            });
        }
    }

    /**
     * Met à jour la taille de l'image en fonction de la taille du Pane et du ratio de l'image.
     */
    private void resizeImageView(double paneWidth, double paneHeight, double imageRatio, ImageView imageView) {
        if (imageView != null) {
            // Calcule la largeur et la hauteur de l'image en fonction du ratio
            double newWidth = paneHeight * imageRatio;
            double newHeight = paneHeight;

            // Si la largeur de l'image dépasse la largeur du pane, ajustez la taille
            if (newWidth > paneWidth) {
                newWidth = paneWidth;
                newHeight = paneWidth / imageRatio;
            }

            // Met à jour la taille de l'image
            imageView.setFitWidth(newWidth);
            imageView.setFitHeight(newHeight);
        }
    }
}
