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

public class WinScreenView {

    private Stage primaryStage;

    public WinScreenView(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene showPage() {
        // Create a BorderPane for the layout of the win screen.
        BorderPane levelLayout = new BorderPane();
        Scene levelScene = new Scene(levelLayout, 900, 500);
        primaryStage.setMaxWidth(900);
        primaryStage.setMaxHeight(500);
        levelScene.getStylesheets().add(getClass().getResource("css/winscreen.css").toExternalForm());

        // Create an "Exit" button.
        Button exitButton = new Button();

        // Apply a CSS style to the exit button.
        exitButton.getStyleClass().add("easyButton");

        // Set an action for the exit button (to be implemented later).
        exitButton.setOnAction(event -> {
            // Implement the action to exit the win screen.
        });

        // Create a VBox to contain the exit button and set its layout properties.
        VBox difficultyBox = new VBox(exitButton);
        VBox.setMargin(exitButton, new Insets(150, 0, 0, 0));
        difficultyBox.setAlignment(Pos.CENTER);

        // Set the VBox containing the exit button at the center of the BorderPane layout.
        levelLayout.setCenter(difficultyBox);

        return levelScene; // Return the Scene for the win screen.
    }
}
