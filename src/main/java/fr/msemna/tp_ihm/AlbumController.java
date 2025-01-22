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
import javafx.scene.paint.Color;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AlbumController {
    @FXML
    private Pane albumPane;

    private List<Image> images = new ArrayList<>();
    private List<ImageView> imageViews = new ArrayList<>();
    private List<Image> refImages = new ArrayList<>();
    private List<ImageView> refImageViews = new ArrayList<>();

    private ImageView selectedImageView;
    private double initialMouseX;
    private double initialMouseY;
    private double initialImageX;
    private double initialImageY;

    @FXML
    private void initialize() {
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

    // Méthode pour sauver l'état des listes avant toute modification
    private void saveState() {
        refImages = new ArrayList<>(images);
        refImageViews = new ArrayList<>(imageViews);
    }

    // Réinitialise l'affichage à l'état sauvegardé
    private void revertState() {
        images = new ArrayList<>(refImages);
        imageViews = new ArrayList<>(refImageViews);
        updateAlbumPane();
    }

    // Actualise le Pane avec les listes actuelles
    private void updateAlbumPane() {
        albumPane.getChildren().clear();
        for (ImageView imageView : imageViews) {
            albumPane.getChildren().add(imageView);
        }
    }

    @FXML
    private void handleImportPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);

            double imageRatio = image.getWidth() / image.getHeight();
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(10);
            dropShadow.setOffsetX(5);
            dropShadow.setOffsetY(5);
            dropShadow.setColor(javafx.scene.paint.Color.GRAY);
            imageView.setEffect(dropShadow);

            double offsetX = 20 * imageViews.size();
            double offsetY = 20 * imageViews.size();
            imageView.setLayoutX(offsetX);
            imageView.setLayoutY(offsetY);

            images.add(image);
            imageViews.add(imageView);
            albumPane.getChildren().add(imageView);

            resizeAllImages();
            saveState();  // Sauvegarde l'état après l'importation
        }
    }

    @FXML
    private void bringImageToFront() {
        if (selectedImageView != null) {
            int currentIndex = imageViews.indexOf(selectedImageView);
            if (currentIndex < imageViews.size() - 1) {
                ImageView nextImageView = imageViews.get(currentIndex + 1);
                albumPane.getChildren().remove(selectedImageView);
                albumPane.getChildren().remove(nextImageView);
                albumPane.getChildren().add(currentIndex + 1, selectedImageView);
                albumPane.getChildren().add(currentIndex, nextImageView);

                imageViews.set(currentIndex, nextImageView);
                imageViews.set(currentIndex + 1, selectedImageView);

                images.set(currentIndex, nextImageView.getImage());
                images.set(currentIndex + 1, selectedImageView.getImage());
                saveState();  // Sauvegarde l'état après l'avancement
            }
        }
    }

    @FXML
    private void sendImageToBack() {
        if (selectedImageView != null) {
            int currentIndex = imageViews.indexOf(selectedImageView);
            if (currentIndex > 0) {
                ImageView previousImageView = imageViews.get(currentIndex - 1);
                albumPane.getChildren().remove(selectedImageView);
                albumPane.getChildren().remove(previousImageView);
                albumPane.getChildren().add(currentIndex - 1, selectedImageView);
                albumPane.getChildren().add(currentIndex, previousImageView);

                imageViews.set(currentIndex, previousImageView);
                imageViews.set(currentIndex - 1, selectedImageView);

                images.set(currentIndex, previousImageView.getImage());
                images.set(currentIndex - 1, selectedImageView.getImage());
                saveState();  // Sauvegarde l'état après l'envoi en arrière
            }
        }
    }

    @FXML
    private void deleteSelectedImage() {
        if (selectedImageView != null) {
            albumPane.getChildren().remove(selectedImageView);
            imageViews.remove(selectedImageView);
            images.remove(selectedImageView.getImage());
            resetSelectionEffect(selectedImageView);
            selectedImageView = null;
            saveState();  // Sauvegarde l'état après suppression
        }
    }

    @FXML
    private void revertChanges() {
        revertState();  // Restaure l'état sauvegardé
    }

    private void resizeAllImages() {
        if (imageViews.isEmpty()) return;

        ImageView lastImageView = imageViews.get(imageViews.size() - 1);
        double paneWidth = albumPane.getWidth();
        double paneHeight = albumPane.getHeight();
        double imageRatio = lastImageView.getImage().getWidth() / lastImageView.getImage().getHeight();

        double newWidth = paneHeight * imageRatio;
        double newHeight = paneHeight;
        if (newWidth > paneWidth) {
            newWidth = paneWidth;
            newHeight = paneWidth / imageRatio;
        }

        lastImageView.setFitWidth(newWidth);
        lastImageView.setFitHeight(newHeight);

        for (ImageView imageView : imageViews) {
            imageView.setFitWidth(newWidth);
            imageView.setFitHeight(newHeight);
        }
    }

    private void handleMousePressed(MouseEvent event) {
        boolean imageClicked = false;

        for (int i = imageViews.size() - 1; i >= 0; i--) {
            ImageView imageView = imageViews.get(i);
            if (isMouseOverImage(event.getX(), event.getY(), imageView)) {
                imageClicked = true;

                if (selectedImageView != imageView) {
                    if (selectedImageView != null) {
                        resetSelectionEffect(selectedImageView);
                    }
                    selectedImageView = imageView;
                    initialMouseX = event.getX();
                    initialMouseY = event.getY();
                    initialImageX = imageView.getLayoutX();
                    initialImageY = imageView.getLayoutY();

                    applySelectionEffect(imageView);
                }
                break;
            }
        }

        if (!imageClicked && selectedImageView != null) {
            resetSelectionEffect(selectedImageView);
            selectedImageView = null;
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (selectedImageView != null) {
            double deltaX = event.getX() - initialMouseX;
            double deltaY = event.getY() - initialMouseY;
            selectedImageView.setLayoutX(initialImageX + deltaX);
            selectedImageView.setLayoutY(initialImageY + deltaY);
        }
    }

    private void handleMouseReleased(MouseEvent event) {
    }

    private boolean isMouseOverImage(double mouseX, double mouseY, ImageView imageView) {
        double x = imageView.getLayoutX();
        double y = imageView.getLayoutY();
        double width = imageView.getFitWidth();
        double height = imageView.getFitHeight();
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private void applySelectionEffect(ImageView imageView) {
        DropShadow selectionShadow = new DropShadow();
        selectionShadow.setColor(javafx.scene.paint.Color.BLUE);
        selectionShadow.setRadius(15);
        imageView.setEffect(selectionShadow);
    }

    private void resetSelectionEffect(ImageView imageView) {
        imageView.setEffect(null);
    }
}
