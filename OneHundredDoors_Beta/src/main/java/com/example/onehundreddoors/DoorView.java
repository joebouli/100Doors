package com.example.onehundreddoors;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DoorView {
    private ImageView handleImageView; // ImageView for the door handle

    private ImageView arrowImageView; // ImageView for the arrow
    private ImageView barImageView; // ImageView for the bar

    private WebCamCapture webCamCapture; // Object to capture webcam images
    private ImageView cameraFrame; // ImageView for displaying webcam feed

    private Scene gameScene; // Reference to the main game scene
    private GameModel gameModel; // Reference to the game model
    private Stage primaryStage; // Reference to the primary stage

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); } // Load OpenCV native library

    double soundIntensity; // Sound intensity
    private SoundRecognizer soundRecognizer; // Object for sound recognition

    private int type; // Type of door (1: Drag and drop, 2: Time button, 3: Colour, 4: Sound)
    DoorModel doorModel = new DoorModel(); // Door model
    private ImageView doorImageView; // ImageView for the door
    private boolean handleDragged = false; // Flag indicating if the handle is being dragged
    private double arrowPosition = 0.5; // Initial position of the arrow (center)
    private double arrowSpeed = 0.04; // Arrow movement speed
    private Timeline arrowTimeline; // Timeline for arrow animation

    public DoorView(Stage primaryStage, int type, Scene gameScene, GameModel gm, SoundRecognizer soundRecognizer) {
        this.gameScene = gameScene;
        this.primaryStage = primaryStage;
        this.gameModel = gm;
        AnchorPane root = new AnchorPane();
        this.type = type;

        switch (type) {
            case 1:
                // Drag and drop door
                dragAndDropDoor(primaryStage, root);
                break;
            case 2:
                // Time button door
                timeButtonDoor(primaryStage, root);
                break;
            case 3:
                // Colour door
                colourDoor(primaryStage, root);
                break;
            case 4:
                // Sound door
                soundDoor(primaryStage, root);

                // Sound door handling in a separate thread
                new Thread(() -> {
                    boolean opened = false;
                    while (!opened) {
                        // Start sound recognition
                        soundRecognizer.startSoundRecognition(2000.0);
                        double sound = 0;
                        doorImageView.setImage(new Image(getClass().getResourceAsStream("css/images/SoundDoor1.png"));
                        sound = soundRecognizer.getIntensity();
                        if (sound > 1000.0) {
                            doorImageView.setImage(new Image(getClass().getResourceAsStream("css/images/SoundDoor2.png"));
                        }
                        if (sound > 2000.0) {
                            doorImageView.setImage(new Image(getClass().getResourceAsStream("css/images/SoundDoor3.png"));
                        }
                        if (sound > 3000.0) {
                            doorImageView.setImage(new Image(getClass().getResourceAsStream("css/images/SoundDoor4.png"));
                        }
                        if (sound > 4000.0) {
                            opened = true;
                            break;
                        }
                    }
                    // Once the sound door is opened, update the view on the JavaFX application thread
                    Platform.runLater(() -> openSoundDoor());
                }).start();
                break;
        }
    }

    private void timeButtonDoor(Stage primaryStage, AnchorPane root) {
        // Load door image
        Image doorImage = new Image(getClass().getResourceAsStream("css/images/ButtonDoor.png"));
        doorImageView = new ImageView(doorImage);
        doorImageView.setPreserveRatio(true);
        doorImageView.setFitHeight(400);

        // Position the door at the center of the AnchorPane
        AnchorPane.setLeftAnchor(doorImageView, 260.0);

        // Load bar image
        Image barImage = new Image(getClass().getResourceAsStream("css/images/bar.png"));
        barImageView = new ImageView(barImage);
        barImageView.setPreserveRatio(true);
        barImageView.setFitWidth(220);
        barImageView.setX(355);
        barImageView.setY(330);

        // Load the arrow image
        Image arrowImage = new Image(getClass().getResourceAsStream("css/images/arrow.png"));
        arrowImageView = new ImageView(arrowImage);
        arrowImageView.setPreserveRatio(true);
        arrowImageView.setFitWidth(120); // Arrow width
        arrowImageView.setX(405); // Initially position the arrow at the center
        arrowImageView.setY(385); // Bar height

        arrowTimeline = new Timeline(
                new KeyFrame(Duration.millis(10), event -> moveArrow())
        );
        arrowTimeline.setCycleCount(Timeline.INDEFINITE);

        // Start the Timeline
        arrowTimeline.play();

        root.getChildren().addAll(doorImageView, barImageView, arrowImageView);
        Scene scene = new Scene(root, 900, 500);

        scene.getStylesheets().add(getClass().getResource("css/door.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setOnKeyPressed(event -> handleKeyPress(event));
    }

    private void moveArrow() {
        // Move the arrow
        arrowPosition += arrowSpeed;

        // Limit the minimum and maximum position of the arrow
        if (arrowPosition <= -0.4 || arrowPosition >= 1.3) {
            arrowSpeed = -arrowSpeed;
        }

        // Call the moveArrow method of the DoorModel class to get the new arrow position
        double newArrowX = doorModel.moveArrow(arrowPosition, arrowSpeed, barImageView.getFitWidth(), arrowImageView.getFitWidth(), barImageView.getX());

        // Update the arrow position
        arrowImageView.setX(newArrowX);
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            double arrowCenterX = arrowImageView.getX() + arrowImageView.getFitWidth() / 2;
            double barCenterX = barImageView.getX() + barImageView.getFitWidth() / 2;
            if (doorModel.isArrowAtCenter(arrowCenterX, barCenterX)) {
                System.out.println("Done");
                openTimeButtonDoor();
                delay(2500, () -> primaryStage.setScene(gameScene));

                if (gameModel.GameOver()) {
                    EndView end = new EndView(primaryStage, true);
                    end.showPage();
                } else {
                    primaryStage.show();
                }
                // GO BACK TO THE GAME!
            } else {
                System.out.println("Error");
                delay(1000, () -> arrowImageView.setVisible(true));
            }
        }
    }

    private void openTimeButtonDoor() {
        doorImageView.setImage(new Image(getClass().getResourceAsStream("css/images/ButtonDoorOpened.png"));
        doorImageView.setY(30);
    }

    private void colourDoor(Stage primaryStage, AnchorPane root) {
        Random rand = new Random();
        int colour = rand.nextInt(3);
        Image doorImage = new Image(getClass().getResourceAsStream("css/images/ColourDoor_" + colour + ".png"));
        doorImageView = new ImageView(doorImage);
        doorImageView.setPreserveRatio(true);
        doorImageView.setFitHeight(400);
        AnchorPane.setLeftAnchor(doorImageView, 260.0);

        cameraFrame = new ImageView();
        cameraFrame.setPreserveRatio(true);
        cameraFrame.setFitWidth(220);
        cameraFrame.setX(355);
        cameraFrame.setY(330);
        webCamCapture = new WebCamCapture(colour);

        root.getChildren().addAll(doorImageView, cameraFrame);
        Scene scene = new Scene(root, 900, 500);

        scene.getStylesheets().add(getClass().getResource("css/door.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        Thread backgroundThread = new Thread(() -> {
            boolean opened = false;
            webCamCapture.startCamera();
            while (!opened) {
                System.out.println("trapped");
                opened = webCamCapture.isOpen;
                if (opened) {
                    opened = true;
                    break;
                }
            }
            System.out.println("Freedom!");
            Platform.runLater(() -> openColourDoor(colour));
        });
        backgroundThread.setDaemon(true);
        backgroundThread.start();
    }

    private void openColourDoor(int colour) {
        System.out.println("Open Colour Door");
        doorImageView.setImage(new Image(getClass().getResourceAsStream("css/images/ColourDoorOpened_" + colour + ".png"));
        doorImageView.setY(30);
        delay(2500, () -> primaryStage.setScene(gameScene));
        if (gameModel.GameOver()) {
            EndView end = new EndView(primaryStage, true);
            end.showPage();
        } else {
            primaryStage.show();
        }
    }

    private void soundDoor(Stage primaryStage, AnchorPane root) {
        Image doorImage = new Image(getClass().getResourceAsStream("css/images/SoundDoor1.png"));
        doorImageView = new ImageView(doorImage);
        doorImageView.setPreserveRatio(true);
        doorImageView.setFitHeight(400);
        AnchorPane.setLeftAnchor(doorImageView, 260.0);

        root.getChildren().addAll(doorImageView);
        Scene scene = new Scene(root, 900, 500);

        scene.getStylesheets().add(getClass().getResource("css/door.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void openSoundDoor() {
        doorImageView.setImage(new Image(getClass().getResourceAsStream("css/images/SoundDoor5.png"));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        doorImageView.setImage(new Image(getClass().getResourceAsStream("css/images/soundDoorOpened.png"));
        doorImageView.setY(30);
        delay(2500, () -> primaryStage.setScene(gameScene));
        if (gameModel.GameOver()) {
            EndView end = new EndView(primaryStage, true);
            end.showPage();
        } else {
            primaryStage.show();
        }
    }

    private void dragAndDropDoor(Stage primaryStage, AnchorPane root) {
        // Load the door image
        Image doorImage = new Image(getClass().getResourceAsStream("css/images/BrownDoorNoHandle.png"));
        doorImageView = new ImageView(doorImage);
        doorImageView.setPreserveRatio(true);
        doorImageView.setFitHeight(400);

        // Position the door at the center of the AnchorPane
        AnchorPane.setLeftAnchor(doorImageView, 260.0);

        // Create a handle
        Image handleImage = new Image(getClass().getResourceAsStream("css/images/Handle.png"));
        handleImageView = new ImageView(handleImage);
        handleImageView.setPreserveRatio(true);
        handleImageView.setFitWidth(100);
        handleImageView.setX(500); // Initially position the handle on the door
        handleImageView.setY(400);

        // Add the handle to the scene
        root.getChildren().addAll(doorImageView, handleImageView);

        // Add drag events
        handleImageView.setOnMousePressed(this::handlePress);
        handleImageView.setOnMouseDragged(this::handleDrag);
        handleImageView.setOnMouseReleased(this::handleRelease);

        Scene scene = new Scene(root, 900, 500);

        scene.getStylesheets().add(getClass().getResource("css/door.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handlePress(MouseEvent event) {
        handleDragged = true;
        Image newHandleImage = new Image(getClass().getResourceAsStream("css/images/handleForward.png"));
        handleImageView.setImage(newHandleImage);
    }

    private void handleDrag(MouseEvent event) {
        if (handleDragged) {
            double newX = event.getSceneX() - handleImageView.getBoundsInParent().getWidth() / 2;
            double newY = event.getSceneY() - handleImageView.getBoundsInParent().getHeight() / 2;

            // Limit the coordinates to prevent the handle from going outside the AnchorPane
            newX = Math.max(0, Math.min(newX, 800.0);
            newY = Math.max(0, Math.min(newY, 400.0));

            handleImageView.relocate(newX, newY);
        }
    }

    private void handleRelease(MouseEvent event) {
        if (handleDragged) {
            double handleX = handleImageView.getBoundsInParent().getMinX() + handleImageView.getBoundsInParent().getWidth() / 2;
            double handleY = handleImageView.getBoundsInParent().getMinY() + handleImageView.getBoundsInParent().getHeight() / 2;
            if (doorModel.openDoorWithHandle(handleX, handleY)) {
                doorImageView.requestFocus();
                doorImageView.setImage(new Image(getClass().getResourceAsStream("css/images/BrownOpenedDoorWithHandle.png"));
                doorImageView.setY(30);
                handleImageView.setVisible(false);

                delay(2500, () -> primaryStage.setScene(gameScene));
                if (gameModel.GameOver()) {
                    EndView end = new EndView(primaryStage, true);
                    end.showPage();
                } else {
                    primaryStage.show();
                }
            }

            handleDragged = false;
        }
    }

    public static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try { Thread.sleep(millis); }
                catch (InterruptedException e) { }
                return null;
            }
        };
        sleeper.setOnSucceeded(event -> continuation.run());
        new Thread(sleeper).start();
    }
}
