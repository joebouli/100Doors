package com.example.onehundreddoors;

import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javafx.scene.image.ImageView;

public class GameModel {

    // Defaults
    private int[] arrtypeDoors;
    private boolean[] arractiveDoors;
    private int[][] arrXYDoors;

    private int numDoors;

    private int[] arrCorrectDoors;
    private int gameProgress;
    private int hints;

    private String selectedCharacter;
    private int xChar, yChar;
    private int level;

    private Timer gameTimer;
    private int secondsElapsed;

    public GameModel() {
        // Initialize other instance variables, if necessary
        gameProgress = 0;
    }

    // Set which character has been chosen
    public void setSelectedCharacter(String selectedCharacter) {
        this.selectedCharacter = selectedCharacter;
    }

    public String getSelectedCharacter() {
        return selectedCharacter;
    }

    /*
    Easy is equal to 0
    Medium is equal to 1
    Hard is equal to 2
    */
    public void setLevel(int level) {
        this.level = level;
        numDoors = 8 + level * 4;
        hints = 2;
        // Set the total number of doors as well as their type;
        switch (level) {
            case 0:
                arrtypeDoors = new int[]{1, 1, 2, 2, 3, 3, 4, 4};
                break;
            case 1:
                arrtypeDoors = new int[]{1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4};
                break;
            case 2:
                arrtypeDoors = new int[]{1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4};
        }
        shuffleArray(arrtypeDoors);

        // Set Coordinates of Doors
        arrXYDoors = new int[numDoors][2];
        double angleSplit = 360.0 / numDoors;
        for (int i = 0; i < numDoors; i++) {
            double angleDoor = Math.toRadians(i * angleSplit);
            arrXYDoors[i][0] = (int) (Math.sin(angleDoor) * 170);
            arrXYDoors[i][1] = (int) (Math.cos(angleDoor) * -170);
        }

        // Set Make all doors Active
        arractiveDoors = new boolean[numDoors];
        Arrays.fill(arractiveDoors, true);

        // Set the right order to follow for all Doors and extract the first 4 in the order
        int[] allDoors = new int[numDoors];
        for (int k = 0; k < allDoors.length; k++) {
            allDoors[k] = k;
        }
        shuffleArray(allDoors);
        arrCorrectDoors = Arrays.copyOfRange(allDoors, 0, 4);
    }

    public int[] getDoorXY(int door) {
        return arrXYDoors[door];
    }

    public int[] getTypeDoors() {
        return arrtypeDoors;
    }

    public int[] getCorrectDoors() {
        return arrCorrectDoors;
    }

    public boolean isNextDoor(int i) {
        if (arrCorrectDoors[gameProgress] == i) {
            correctDoorFound(i);
            return true;
        }
        resetProgress();
        return false;
    }

    public int getProgress() {
        return gameProgress;
    }

    public void resetProgress() {
        gameProgress = 0;
        arractiveDoors = new boolean[numDoors];
        Arrays.fill(arractiveDoors, true);
    }

    public void correctDoorFound(int i) {
        gameProgress++;
        arractiveDoors[i] = false;
    }

    public int getHintDoor() {
        int posDoor = 0;
        switch (hints) {
            case 2: {
                posDoor = generateRandomPossibleDoor();
                arractiveDoors[posDoor] = false;
                System.out.println(Arrays.toString(arractiveDoors));
                hints--;
                break;
            }
            case 1: {
                posDoor = arrCorrectDoors[gameProgress];
                hints--;
            }
        }
        return posDoor;
    }

    public int getHint() {
        return hints;
    }

    public boolean isGameOver() {
        return gameProgress == 4;
    }

    private int generateRandomPossibleDoor() {
        Random rand = new Random();
        int random = rand.nextInt(numDoors);
        System.out.println(random);
        while (isCorrectDoor(random) || !arractiveDoors[random]) {
            System.out.println(random);
            random = rand.nextInt(numDoors);
        }
        return random;
    }

    private boolean isCorrectDoor(int random) {
        for (int i = 0; i < 4; i++) {
            if (arrCorrectDoors[i] == random) {
                return true;
            }
        }
        return false;
    }

    private static void shuffleArray(int[] array) {
        int index, temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    // Moves the character in the model
    /*
     * 0 is up
     * 1 is right
     * 2 is down
     * 3 is left
     */
    public void moveCharacter(int direction) {
        switch (direction) {
            case 0:
                yChar -= 10;
                break;
            case 1:
                xChar += 10;
                break;
            case 2:
                yChar += 10;
                break;
            case 3:
                xChar -= 10;
        }
    }

    public void resetCharacter() {
        xChar = 0;
        yChar = 0;
    }

    public int getCharacterX() {
        return xChar;
    }

    public int getCharacterY() {
        return yChar;
    }

    // Checks whether the character is on a Door
    // -1 represents no collision taking place.
    public int collisionDoor() {
        int val = -1;
        for (int i = 0; i < numDoors; i++) {
            if (!arractiveDoors[i]) {
                continue;
            }
            if ((Math.abs(xChar - arrXYDoors[i][0]) <= 15) && (Math.abs(yChar - arrXYDoors[i][1]) <= 15)) {
                val = i;
                break;
            }
        }
        return val;
    }

    // Method to start the timer
    public void startTimer() {
        gameTimer = new Timer();
        secondsElapsed = 0;

        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                secondsElapsed++;
            }
        }, 1000, 1000); // Update the timer every second
    }

    // Method to stop the timer
    public void stopTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }
    }

    // Method to get the time formatted as "mm:ss"
    public String getFormattedTime() {
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}
