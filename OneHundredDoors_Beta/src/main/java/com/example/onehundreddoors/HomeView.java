package com.example.onehundreddoors;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class HomeView extends Application {

    private Stage primaryStage;
    private Scene scene;
    private VBox root;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("OneHundredDoors");

        showHomePage(primaryStage);
    }

    // Display the home page with buttons for start, leaderboard, and instructions.
    public void showHomePage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        scene = new Scene(root, 900, 500);
        primaryStage.setMaxWidth(900);
        primaryStage.setMaxHeight(500);

        scene.getStylesheets().add(getClass().getResource("css/home.css").toExternalForm());

        // Create buttons for Start, Leaderboard, and Instructions.
        Button startButton = new Button();
        Button leaderBoardButton = new Button();
        Button instructionsButton = new Button();

        // Set margins for the buttons.
        VBox.setMargin(startButton, new Insets(210, 0, 0, 0));
        VBox.setMargin(leaderBoardButton, new Insets(15, 0, 0, 0));
        VBox.setMargin(instructionsButton, new Insets(15, 0, 0, 0));

        // Apply styles to the buttons.
        startButton.getStyleClass().add("startButton");
        leaderBoardButton.getStyleClass().add("leaderBoardButton");
        instructionsButton.getStyleClass().add("instructionsButton");

        // Add the buttons to the layout.
        root.getChildren().addAll(startButton, leaderBoardButton, instructionsButton);

        // Handle button actions.
        startButton.setOnAction(event -> {
            ChooseCharacterView chooseCharacterView = new ChooseCharacterView(primaryStage);
            chooseCharacterView.showPage();
        });

        leaderBoardButton.setOnAction(event -> {
            showLeaderBoard();
        });

        instructionsButton.setOnAction(event -> showInstructions());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Display the leaderboard page.
    private void showLeaderBoard() {
        root.getChildren().clear();

        scene.getStylesheets().add(getClass().getResource("css/leaderboard.css").toExternalForm());

        // Create a button to navigate back to the home page.
        Button backButton = new Button();
        backButton.getStyleClass().add("backButton");

        StackPane stackPane = new StackPane();

        // Set the margin for the back button.
        StackPane.setMargin(backButton, new Insets(450, 850, 0, 0));

        stackPane.getChildren().add(backButton);
        backButton.setOnAction(event -> showHomePage(primaryStage));
        root.getChildren().add(stackPane);
    }

    // Display the instructions page.
    public void showInstructions() {
        root.getChildren().clear();

        scene.getStylesheets().add(getClass().getResource("css/instructions.css").toExternalForm());

        // Create a text element with the game's story and instructions.
        Text storyText = new Text("You wake up in the middle of a strange room\nsurrounded by doors...\nYou try to exit but soon realize that you are stuck...\nBla bla bla");
        storyText.getStyleClass().add("text");
        storyText.setTextAlignment(TextAlignment.CENTER);

        // Create a button to navigate back to the home page.
        Button backButton = new Button();
        backButton.getStyleClass().add("backButton");

        StackPane stackPane = new StackPane();

        // Set the alignment and margin for the story text and back button.
        StackPane.setAlignment(storyText, Pos.CENTER);
        StackPane.setMargin(storyText, new Insets(170, 0, 20, 0));
        StackPane.setMargin(backButton, new Insets(450, 850, 0, 0));

        stackPane.getChildren().addAll(storyText, backButton);

        backButton.setOnAction(event -> showHomePage(primaryStage));

        root.getChildren().add(stackPane);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
