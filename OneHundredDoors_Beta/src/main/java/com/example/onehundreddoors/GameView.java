package com.example.onehundreddoors;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint Color;
import javafx.scene.shape Circle;
import javafx.stage.Stage;

public class GameView {

    private Stage primaryStage;
    private GameModel gameModel;
    private Label timerLabel; // Label to display the timer
    private AnimationTimer gameLoop;

    private int centreX;
    private int centreY;

    // Characters
    private String characterName;
    private Image charfront;
    private Image charback;
    private Image charright;
    private Image charleft;
    private ImageView imgCharacter;

    private ImageView imgScore;
    private ImageView imgMehdi;
    private ImageView imgHint;
    // Doors
    private ArrayList<ImageView> imgDoors = new ArrayList<ImageView>();
    private Image[] typeClosedDoor;
    private Image[] typeOpenDoor;

    private SoundRecognizer soundRecognizer = new SoundRecognizer();

    public GameView(Stage primaryStage, GameModel gameModel) {
        this.primaryStage = primaryStage;
        this.gameModel = gameModel;
        gameModel.startTimer();
        initializeUI();
    }

    private void initializeUI() {
        // Create a label to display the timer
        timerLabel = new Label("0 seconds");

        // Create a VBox to organize UI elements
        VBox root = new VBox(timerLabel /* Add other elements here */);

        HBox worldPane = new HBox();

        // Create the scene and set the layout as the root
        Scene scene = new Scene(root, 900, 500);
        primaryStage.setMaxWidth(900);
        primaryStage.setMaxHeight(500);

        scene.getStylesheets().add(getClass().getResource("css/game.css").toExternalForm());

        // Load character images based on the selected character
        characterName = gameModel.getSelectedCharacter();
        charfront = new Image(getClass().getResourceAsStream("css/images/" + characterName + "_TinyFront.png"));
        charback = new Image(getClass().getResourceAsStream("css/images/" + characterName + "_TinyBack.png"));
        charleft = new Image(getClass().getResourceAsStream("css/images/" + characterName + "_TinyLeft.png"));
        charright = new Image(getClass().getResourceAsStream("css/images/" + characterName + "_TinyRight.png"));

        // Create an ImageView for the character
        imgCharacter = new ImageView(charfront);
        // Set character dimensions
        imgCharacter.setFitHeight(80);
        imgCharacter.setFitWidth(40);

        // Create a game pane to display doors and the character
        Pane gamePane = new Pane();
        // Bind the gamePane size to a fraction of the scene size
        gamePane.prefWidthProperty().bind(scene.widthProperty().multiply(0.60));
        gamePane.prefHeightProperty().bind(scene.heightProperty().multiply(0.90));

        // Load door images
        typeClosedDoor = new Image[] {
                new Image(getClass().getResourceAsStream("css/images/smallBrownDoor.png")),
                new Image(getClass().getResourceAsStream("css/images/smallButtonDoor.png")),
                new Image(getClass().getResourceAsStream("css/images/smallColourDoor.png")),
                new Image(getClass().getResourceAsStream("css/images/smallSoundDoor.png"))
        };

        typeOpenDoor = new Image[] {
                new Image(getClass().getResourceAsStream("css/images/smallBrownDoorOpened.png")),
                new Image(getClass().getResourceAsStream("css/images/smallButtonDoorOpened.png")),
                new Image(getClass().getResourceAsStream("css/images/smallColourDoorOpened.png")),
                new Image(getClass().getResourceAsStream("css/images/smallSoundDoorOpened.png"))
        };

        // Initialize doors based on the game model
        int[] arrDoors = gameModel.gettypeDoors();
        for (int i = 0; i < arrDoors.length; i++) {
            imgDoors.add(new ImageView(typeClosedDoor[arrDoors[i] - 1]));
        }
        System.out.println(Arrays.toString(arrDoors));
        System.out.println(Arrays.toString(gameModel.getcorrectDoors()));

        // Add doors and character to the gamePane
        gamePane.getChildren().addAll(imgDoors);
        gamePane.getChildren().add(imgCharacter);

        VBox hintPane = new VBox();
        // Bind hintPane size to a fraction of the scene size
        hintPane.prefWidthProperty().bind(scene.widthProperty().multiply(0.40));
        hintPane.prefHeightProperty().bind(scene.heightProperty().multiply(0.90));

        // Create ImageView for the score
        imgScore = new ImageView(new Image(getClass().getResourceAsStream("css/images/gameProgress_0.png")));
        imgScore.setFitHeight(150);
        imgScore.setFitWidth(150);

        // Create ImageView for Mehdi character
        imgMehdi = new ImageView(new Image(getClass().getResourceAsStream("css/images/Mehdi.png")));
        imgMehdi.setFitHeight(150);
        imgMehdi.setFitWidth(150);

        // Create ImageView for a hint
        imgHint = new ImageView();

        // Create a button for surrendering the game
        Button surrButton = new Button();
        surrButton.setOnMouseClicked(e -> {
            EndView end = new EndView(primaryStage, false);
            end.showPage();
        });
        surrButton.getStyleClass().add("surrButton");

        // Add the score, Mehdi character, and surrender button to hintPane
        hintPane.getChildren().addAll(imgScore, imgMehdi, surrButton);

        // Set margins for the hintPane elements
        hintPane.setMargin(imgScore, new Insets(-70, 0, 0, 180));
        hintPane.setMargin(imgMehdi, new Insets(50, 0, 0, 120));
        hintPane.setMargin(surrButton, new Insets(80, 0, 0, 140));

        // Add gamePane and hintPane to the world below the timer
        worldPane.getChildren().addAll(gamePane, hintPane);
        root.getChildren().add(worldPane);

        // Set up the game world
        centreX = 250;
        centreY = 180;
        double angleSplit = 360.0 / imgDoors.size();

        for (int i = 0; i < imgDoors.size(); i++) {
            // Set door dimensions
            imgDoors.get(i).setFitHeight(80);
            imgDoors.get(i).setFitWidth(60);
            // Set door positions
            imgDoors.get(i).setX(centreX + gameModel.getDoorXY(i)[0]);
            imgDoors.get(i).setY(centreY + gameModel.getDoorXY(i)[1]);
        }

        // Set initial character position
        imgCharacter.setX(centreX);
        imgCharacter.setY(centreY);

        // Handle character movement and door interactions
        imgCharacter.setOnKeyPressed(e -> {
            // First, move the character
            if ((e.getCode() == KeyCode.W)) {
                imgCharacter.setImage(charback);
                if (imgCharacter.getY() >= 0) {
                    gameModel.moveCharacter(0);
                }
            } else if ((e.getCode() == KeyCode.D)) {
                imgCharacter.setImage(charright);
                if (imgCharacter.getX() <= gamePane.getWidth() - imgCharacter.getFitWidth()) {
                    gameModel.moveCharacter(1);
                }
            } else if ((e.getCode() == KeyCode.S)) {
                imgCharacter.setImage(charfront);
                if (imgCharacter.getY() <= gamePane.getHeight() - imgCharacter.getFitHeight()) {
                    gameModel.moveCharacter(2);
                }
            } else if ((e.getCode() == KeyCode.A)) {
                imgCharacter.setImage(charleft);
                if (imgCharacter.getX() >= 0) {
                    gameModel.moveCharacter(3);
                }
            }
            // Update character position
            imgCharacter.setX(centreX + gameModel.getCharacterX());
            imgCharacter.setY(centreY + gameModel.getCharacterY());

            // Check if the character arrives at a door
            int atDoor = gameModel.collisionDoor();
            if (atDoor > -1) {
                if (gameModel.isNextDoor(atDoor)) {
                    imgDoors.get(atDoor).setImage(typeOpenDoor[gameModel.gettypeDoors()[atDoor] - 1]);
                    imgScore.setImage(new Image(getClass().getResourceAsStream("css/images/gameProgress_" + gameModel.getProgress() + ".png"));
                } else {
                    // Reset other doors if the chosen door is incorrect
                    for (int d = 0; d < arrDoors.length; d++) {
                        imgDoors.get(d).setImage(typeClosedDoor[gameModel.gettypeDoors()[d] - 1]);
                    }
                    imgScore.setImage(new Image(getClass().getResourceAsStream("css/images/gameProgress_0.png"));
                }
                // Handle door interaction and character reset
                DoorView dw = new DoorView(primaryStage, gameModel.gettypeDoors()[atDoor], scene, gameModel, soundRecognizer);
                imgCharacter.setX(centreX);
                imgCharacter.setY(centreY);
                gameModel.resetCharacter();
                imgHint.setVisible(false);
            }
        });

        // Handle clicking on the Mehdi character for hints
        imgMehdi.setOnMouseClicked(e -> {
            // Get the number of hints left
            int hints = gameModel.getHint();
            // Reduce hints by 1 and perform actions accordingly
            int hintDoor = gameModel.getHintDoor();
            if (hints == 2) {
                imgMehdi.setImage(new Image(getClass().getResourceAsStream("css/images/MehdiClicked.png")));
                imgDoors.get(hintDoor).setVisible(false);
                System.out.println("Hint 2");
                // Reset the Mehdi image after a delay
                delay(2000, () -> imgMehdi.setImage(new Image(getClass().getResourceAsStream("css/images/Mehdi.png")));
            } else if (hints == 1) {
                imgHint.setVisible(true);
                imgMehdi.setImage(new Image(getClass().getResourceAsStream("css/images/MehdiClicked.png")));
                imgHint.setImage(new Image(getClass().getResourceAsStream("css/images/halo.png"));
                gamePane.getChildren().add(imgHint);

                imgHint.setFitHeight(120); // Adjust the height
                imgHint.setFitWidth(120); // Adjust the width

                // Position the hint image near the corresponding door
                imgHint.setX(imgDoors.get(hintDoor).getX() - 35);
                imgHint.setY(imgDoors.get(hintDoor).getY() - 20);

                // Bring the door and character to the front
                imgDoors.get(hintDoor).toFront();
                imgCharacter.toFront();
                System.out.println("Hint 1");
                // Reset the Mehdi image after a delay
                delay(2000, () -> imgMehdi.setImage(new Image(getClass().getResourceAsStream("css/images/Mehdi.png")));
            }
        });

        // Set the scene and make it visible
        primaryStage.setScene(scene);
        primaryStage.show();
        imgCharacter.requestFocus();
    }

    public void showPage() {
        // Start the timer when the game begins
        // Update the view with the initial timer value
        updateTimerLabel();

        primaryStage.show();
        imgCharacter.requestFocus();
    }

    // Method to update the timer label using an AnimationTimer
    private void updateTimerLabel() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                String formattedTime = gameModel.getFormattedTime();
                timerLabel.setText("" + formattedTime);
                imgCharacter.requestFocus();
            }
        };
        timer.start();
    }

    // Helper method for introducing delays in actions
    public static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    // Handle any exceptions
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
    }
}
