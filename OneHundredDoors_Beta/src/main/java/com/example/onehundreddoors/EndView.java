package com.example.onehundreddoors;

import javafx.application.Platform;
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

public class EndView {

    private Stage primaryStage;
    private boolean win;

    public EndView(Stage primaryStage, boolean win) {
        this.primaryStage = primaryStage;
        this.win = win;
    }

    public void showPage() {
        BorderPane endPane = new BorderPane();
        Scene levelScene = new Scene(endPane, 900, 500);
        primaryStage.setMaxWidth(900);
        primaryStage.setMaxHeight(500);

        Button endButton = new Button();

        if (win) {
            // Apply styles for the "Game Won" scenario
            levelScene.getStylesheets().add(getClass().getResource("css/gameWon.css").toExternalForm());
            endButton.setOnMouseClicked(e -> {
                primaryStage.hide();
                Platform.exit();
            });
        } else {
            // Apply styles for the "Game Lost" scenario
            levelScene.getStylesheets().add(getClass().getResource("css/gameLost.css").toExternalForm());
            endButton.setOnMouseClicked(e -> {
                // Redirect to ChooseCharacterView if the game is lost
                ChooseCharacterView chooseCharacterView = new ChooseCharacterView(primaryStage);
                chooseCharacterView.showPage();
            });
        }

        endButton.getStyleClass().add("button");

        endPane.setBottom(endButton);
        endPane.setAlignment(endButton, Pos.CENTER);

        primaryStage.setScene(levelScene);
        primaryStage.show();
    }
}
