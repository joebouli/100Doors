package com.example.onehundreddoors;

import javafx.scene.image.ImageView;

public class DoorModel {

    public DoorModel(){

    }

    public boolean openDoorWithHandle(double handleX, double handleY){
        // Check if the handle has been dragged over the lock
        double lockX = 540; // X coordinates of the lock
        double lockY = 230; // Y coordinates of the lock
        double lockWidth = 50; // Lock width
        double lockHeight = 50; // Lock height

        if (handleX >= lockX && handleX <= lockX + lockWidth && handleY >= lockY && handleY <= lockY + lockHeight) {
            // The handle is over the lock, change the door's image
            return true;
        }
        return false;
    }

    public boolean isArrowAtCenter(double arrowCenterX, double barCenterX) {
        // Check if the arrow's position is at the center of the bar

        // Tolerance to consider the arrow at the center
        double tolerance = 35;

        return Math.abs(arrowCenterX - barCenterX) < tolerance;
    }

    public double moveArrow(double arrowPosition, double arrowSpeed, double barWidth, double arrowWidth, double barX) {
        // Move the arrow
        arrowPosition += arrowSpeed;

        // Limit the minimum and maximum position of the arrow
        // arrowPosition = Math.max(0.0, Math.min(1.0, arrowPosition));
        if (arrowPosition <= -0.4 || arrowPosition >= 1.3) {
            arrowSpeed = -arrowSpeed;
        }
        // Update the arrow's position

        double arrowX = barX + (barWidth - arrowWidth) * arrowPosition;
        return arrowX;
    }
}
