package fr.msemna.tp_ihm;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;
import javafx.stage.FileChooser;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import java.util.Stack;

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
    private ImageView selectedImageView;
    private double initialMouseX;
    private double initialMouseY;
    private double initialImageX;
    private double initialImageY;



    @FXML
    private void initialize() {
        // Gestionnaire d'événements pour les clics de souris sur le Pane
        albumPane.setOnMousePressed(this::handleMousePressed);
        albumPane.setOnMouseReleased(this::handleMouseReleased);
        albumPane.setOnMouseDragged(this::handleMouseDragged);
        albumPane.widthProperty().addListener((observable, oldValue, newValue) -> resizeAllImages());
        albumPane.heightProperty().addListener((observable, oldValue, newValue) -> resizeAllImages());
    }


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
            imageView.setEffect(dropShadow);

            // Ajouter des décalages pour éviter le chevauchement initial
            double offsetX = 20 * imageViews.size();
            double offsetY = 20 * imageViews.size();
            imageView.setLayoutX(offsetX);
            imageView.setLayoutY(offsetY);

            // Ajoute l'image et l'ImageView aux listes respectives
            images.add(image);
            imageViews.add(imageView);
            albumPane.getChildren().add(imageView);

            // Redimensionner toutes les images
            resizeAllImages();
        }
    }

    /**
     * Redimensionne toutes les images selon la taille de l'image la plus récente.
     */
    private void resizeAllImages() {
        if (imageViews.isEmpty()) return;

        // Déterminer la taille de l'image la plus récente
        ImageView lastImageView = imageViews.get(imageViews.size() - 1);
        double paneWidth = albumPane.getWidth();
        double paneHeight = albumPane.getHeight();
        double imageRatio = lastImageView.getImage().getWidth() / lastImageView.getImage().getHeight();

        // Calculer la taille pour que la dernière image s'adapte au Pane
        double newWidth = paneHeight * imageRatio;
        double newHeight = paneHeight;
        if (newWidth > paneWidth) {
            newWidth = paneWidth;
            newHeight = paneWidth / imageRatio;
        }

        // Mettre à jour les dimensions de la dernière image
        lastImageView.setFitWidth(newWidth);
        lastImageView.setFitHeight(newHeight);

        // Appliquer ces dimensions à toutes les autres images
        for (ImageView imageView : imageViews) {
            imageView.setFitWidth(newWidth);
            imageView.setFitHeight(newHeight);
        }
    }

    /**
     * Gère l'événement lorsque la souris est pressée dans le pane.
     */
    private void handleMousePressed(MouseEvent event) {
        boolean imageClicked = false;

        // Parcourt les images dans l'ordre inverse (avant-plan d'abord)
        for (int i = imageViews.size() - 1; i >= 0; i--) {
            ImageView imageView = imageViews.get(i);

            // Vérifie si le clic est dans les limites de l'image
            if (isMouseOverImage(event.getX(), event.getY(), imageView)) {
                imageClicked = true;

                // Si l'image sélectionnée est déjà sélectionnée, ne pas réinitialiser
                if (selectedImageView != imageView) {
                    if (selectedImageView != null) {
                        resetSelectionEffect(selectedImageView); // Supprime l'effet de la sélection précédente
                    }
                    selectedImageView = imageView; // Définit la nouvelle image sélectionnée

                    // Enregistre les positions initiales de la souris et de l'image
                    initialMouseX = event.getX();
                    initialMouseY = event.getY();
                    initialImageX = imageView.getLayoutX();
                    initialImageY = imageView.getLayoutY();

                    applySelectionEffect(imageView); // Applique l'effet de sélection
                }
                break; // Stoppe dès qu'une image est sélectionnée
            }
        }

        // Si aucun clic sur une image, désélectionner
        if (!imageClicked && selectedImageView != null) {
            resetSelectionEffect(selectedImageView);
            selectedImageView = null;
        }
    }

    /**
     * Gère l'événement lorsque la souris est déplacée avec le bouton enfoncé.
     */
    private void handleMouseDragged(MouseEvent event) {
        if (selectedImageView != null) {
            // Calcule le déplacement de la souris
            double deltaX = event.getX() - initialMouseX;
            double deltaY = event.getY() - initialMouseY;

            // Met à jour la position de l'image
            selectedImageView.setLayoutX(initialImageX + deltaX);
            selectedImageView.setLayoutY(initialImageY + deltaY);
        }
    }

    /**
     * Gère l'événement lorsque la souris est relâchée.
     */
    private void handleMouseReleased(MouseEvent event) {
        // Aucune modification nécessaire ici, car la sélection reste active
    }

    /**
     * Vérifie si la souris est au-dessus d'une ImageView.
     */
    private boolean isMouseOverImage(double mouseX, double mouseY, ImageView imageView) {
        double x = imageView.getLayoutX();
        double y = imageView.getLayoutY();
        double width = imageView.getFitWidth();
        double height = imageView.getFitHeight();

        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    /**
     * Applique un effet visuel pour indiquer la sélection.
     */
    private void applySelectionEffect(ImageView imageView) {
        DropShadow selectionShadow = new DropShadow();
        selectionShadow.setColor(javafx.scene.paint.Color.BLUE); // Couleur de la sélection
        selectionShadow.setRadius(15);
        imageView.setEffect(selectionShadow);
    }

    /**
     * Réinitialise l'effet visuel de la sélection.
     */
    private void resetSelectionEffect(ImageView imageView) {
        imageView.setEffect(null); // Supprime tout effet
    }

    @FXML
    private void bringImageToFront() {
        if (selectedImageView != null) {
            int currentIndex = imageViews.indexOf(selectedImageView);
            // Si l'image n'est pas déjà au dernier niveau
            if (currentIndex < imageViews.size() - 1) {
                // Échange l'image avec celle qui est juste après
                ImageView nextImageView = imageViews.get(currentIndex + 1);

                // Permet de réorganiser les éléments du Pane
                albumPane.getChildren().remove(selectedImageView);
                albumPane.getChildren().remove(nextImageView);

                albumPane.getChildren().add(currentIndex + 1, selectedImageView);
                albumPane.getChildren().add(currentIndex, nextImageView);

                // Met à jour les listes
                imageViews.set(currentIndex, nextImageView);
                imageViews.set(currentIndex + 1, selectedImageView);

                images.set(currentIndex, nextImageView.getImage());
                images.set(currentIndex + 1, selectedImageView.getImage());
            }
        }
    }


    @FXML
    private void sendImageToBack() {
        if (selectedImageView != null) {
            int currentIndex = imageViews.indexOf(selectedImageView);
            // Si l'image n'est pas déjà au premier niveau
            if (currentIndex > 0) {
                // Échange l'image avec celle qui est juste avant
                ImageView previousImageView = imageViews.get(currentIndex - 1);

                // Permet de réorganiser les éléments du Pane
                albumPane.getChildren().remove(selectedImageView);
                albumPane.getChildren().remove(previousImageView);

                albumPane.getChildren().add(currentIndex - 1, selectedImageView);
                albumPane.getChildren().add(currentIndex, previousImageView);

                // Met à jour les listes
                imageViews.set(currentIndex, previousImageView);
                imageViews.set(currentIndex - 1, selectedImageView);

                images.set(currentIndex, previousImageView.getImage());
                images.set(currentIndex - 1, selectedImageView.getImage());
            }
        }
    }


    /**
     * Supprime l'image sélectionnée de l'album.
     */
    @FXML
    private void deleteSelectedImage() {
        if (selectedImageView != null) {
            // Supprime l'ImageView du Pane
            albumPane.getChildren().remove(selectedImageView);

            // Supprime l'image de la liste des ImageViews
            imageViews.remove(selectedImageView);

            // Supprime l'image de la liste des images
            images.remove(selectedImageView.getImage());

            // Réinitialiser la sélection
            resetSelectionEffect(selectedImageView);
            selectedImageView = null;
        }
    }


}
