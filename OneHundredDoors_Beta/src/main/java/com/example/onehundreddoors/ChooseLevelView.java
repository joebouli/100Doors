package com.example.onehundreddoors;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChooseLevelView {

    private Stage primaryStage;
    private GameModel gameModel = new GameModel();

    public ChooseLevelView(Stage primaryStage, String selectedCharacter) {
        this.primaryStage = primaryStage;
        gameModel.setSelectedCharacter(selectedCharacter);
    }

    public void showPage() {
        // Create the layout for choosing the game level.
        BorderPane levelLayout = new BorderPane();
        Scene levelScene = new Scene(levelLayout, 900, 500);
        primaryStage.setMaxWidth(900);
        primaryStage.setMaxHeight(500);
        levelScene.getStylesheets().add(getClass().getResource("css/chooseLevel.css").toExternalForm());

        // Create buttons for selecting the game difficulty.
        Button easyButton = new Button();
        Button mediumButton = new Button();
        Button hardButton = new Button();

        easyButton.getStyleClass().add("easyButton");
        mediumButton.getStyleClass().add("mediumButton");
        hardButton.getStyleClass().add("hardButton");

        // Define actions for difficulty selection buttons.
        /*
         * Easy is equal to 0
         * Medium is equal to 1
         * Hard is equal to 2
         */
        easyButton.setOnAction(event -> {
            gameModel.setLevel(0);
            startGame();
        });
        mediumButton.setOnAction(event -> {
            gameModel.setLevel(1);
            startGame();
        });
        hardButton.setOnAction(event -> {
            gameModel.setLevel(2);
            startGame();
        });

        // Create a button for going back to character selection.
        Button backButton = new Button();
        backButton.setOnAction(event -> {
            ChooseCharacterView chooseCharacterView = new ChooseCharacterView(primaryStage);
            chooseCharacterView.showPage();
        });
        backButton.getStyleClass().add("backButton");

        // Create a container for difficulty selection buttons.
        VBox difficultyBox = new VBox(easyButton, mediumButton, hardButton);
        VBox.setMargin(easyButton, new Insets(130, 0, 0, 0));
        VBox.setMargin(mediumButton, new Insets(20, 0, 0, 0));
        VBox.setMargin(hardButton, new Insets(20, 0, 0, 0));
        difficultyBox.setAlignment(Pos.CENTER);

        // Create a container for the back button.
        HBox buttonsBox = new HBox(backButton);
        buttonsBox.setAlignment(Pos.BOTTOM_LEFT);

        // Set the layout for the level selection view.
        levelLayout.setCenter(difficultyBox);
        levelLayout.setBottom(buttonsBox);

        primaryStage.setScene(levelScene);
    }

    private void startGame() {
        // Start the game with the selected character and difficulty.
        GameView gameView = new GameView(primaryStage, gameModel);
        gameView.showPage();
    }
}
