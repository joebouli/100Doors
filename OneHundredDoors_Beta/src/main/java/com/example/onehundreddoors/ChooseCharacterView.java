package com.example.onehundreddoors;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ChooseCharacterView {

    private Stage primaryStage;
    private ImageView[] characterAvatars;
    private int currentIndex = 0;
    private double startX;
    private String selectedCharacter;
    private ImageView selectedAvatar;

    public ChooseCharacterView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showPage() {
        // Create the main layout for character selection.
        BorderPane characterLayout = new BorderPane();
        characterLayout.setPrefSize(900, 500);
        primaryStage.setMaxWidth(900);
        primaryStage.setMaxHeight(500);

        // Create a box to display character avatars.
        HBox charactersBox = createCharactersBox();
        characterLayout.setCenter(charactersBox);

        // Create "Continue" and "Back" buttons.
        Button continueButton = new Button();
        continueButton.setOnAction(event -> {
            System.out.println(selectedCharacter);
            // Transition to the choose level view.
            ChooseLevelView chooseLevelView = new ChooseLevelView(primaryStage, selectedCharacter);
            chooseLevelView.showPage();
        });
        continueButton.getStyleClass().add("continueButton");

        Button backButton = new Button();
        backButton.setOnAction(event -> {
            // Transition back to the home view.
            HomeView homeView = new HomeView();
            homeView.showHomePage(primaryStage);
        });
        backButton.getStyleClass().add("backButton");

        // Create a box to contain buttons and set their alignment.
        HBox buttonsBox = new HBox(20, backButton, continueButton);
        buttonsBox.setSpacing(800);  // Adjust the spacing between buttons as needed.
        buttonsBox.setAlignment(Pos.CENTER);
        characterLayout.setBottom(buttonsBox);

        // Create and set the scene for the character selection view.
        Scene characterScene = new Scene(characterLayout);
        characterScene.getStylesheets().add(getClass().getResource("css/characterSelection.css").toExternalForm());
        primaryStage.setScene(characterScene);
    }

    private HBox createCharactersBox() {
        // Create a box to display character avatars.
        HBox charactersBox = new HBox(20);  // Adjust spacing as needed.
        charactersBox.setAlignment(Pos.CENTER);

        // Define character names.
        String[] characterNames = {"character_1", "character_2", "character_3"};
        characterAvatars = new ImageView[characterNames.length];

        // Create and add character avatars to the box.
        for (int i = 0; i < characterNames.length; i++) {
            String characterName = characterNames[i];
            ImageView characterAvatar = createCharacterAvatar(characterName);
            characterAvatars[i] = characterAvatar;

            // Add event filters for swipe gestures.
            characterAvatar.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                startX = event.getX();
            });

            characterAvatar.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
                double endX = event.getX();
                if (startX - endX > 40) {
                    // Swipe right-left
                    moveCenter((currentIndex + 1) % characterAvatars.length);
                } else if (endX - startX > 40) {
                    // Swipe left-right
                    moveCenter((currentIndex - 1 + characterAvatars.length) % characterAvatars.length);
                }
            });

            charactersBox.getChildren().add(characterAvatar);
        }

        updateCenter();
        return charactersBox;
    }

    private ImageView createCharacterAvatar(String characterName) {
        // Create an image view for a character avatar.
        Image image = new Image(getClass().getResourceAsStream("css/images/" + characterName + "_Background.png"));
        ImageView avatar = new ImageView(image);
        avatar.setId(characterName);
        avatar.setFitWidth(180);
        avatar.setFitHeight(180);
        return avatar;
    }

    private void moveCenter(int newIndex) {
        // Move the center of character selection to the new index.
        if (newIndex != currentIndex) {
            resetSelectedAvatarScale();
            currentIndex = newIndex;
            updateCenter();
        }
    }

    private void resetSelectedAvatarScale() {
        // Reset the scale of the selected avatar.
        if (selectedAvatar != null) {
            selectedAvatar.setScaleX(1.0);
            selectedAvatar.setScaleY(1.0);
        }
    }

    private void updateCenter() {
        // Update the center character and apply animations.
        selectedCharacter = characterAvatars[currentIndex].getId();

        for (int i = 0; i < characterAvatars.length; i++) {
            ImageView characterAvatar = characterAvatars[i];
            double targetX = (i - currentIndex) * 100.0;

            // Create a translate transition for smooth animation.
            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), characterAvatar);
            transition.setToX(targetX);
            transition.play();

            if (i == currentIndex) {
                characterAvatar.setScaleX(1.6);
                characterAvatar.setScaleY(1.6);
                selectedAvatar = characterAvatar;
            } else {
                characterAvatar.setScaleX(1.0);
                characterAvatar.setScaleY(1.0);
            }
        }
    }
}
