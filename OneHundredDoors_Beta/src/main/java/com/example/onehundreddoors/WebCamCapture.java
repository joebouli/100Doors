package com.example.onehundreddoors;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WebCamCapture {
    private VideoCapture capture;
    private ScheduledExecutorService timer;

    //private ImageView imageView;

    private Runnable frameGrabber = this::getFrame;
    
    //0- red/orange, 1- green, 2- blue
    int doortype = 0; // 
    int[] huesMin = {5,15,90};
    int[] huesMax = {25,89,128};
    int[] satMinMax = {50,255};
    int[] valMinMax = {70,255};
    
    public boolean isOpen;

    public WebCamCapture(int doortype) {
    	this.doortype = doortype;
    	isOpen = false;
    	
        this.capture = new VideoCapture(0);

        //imageView here is not used for computation but to debug the code
        //this.imageView = currentFrame;


        //System.out.print("WebCamCapture");

    }

    public void startCamera(){
        this.timer = Executors.newSingleThreadScheduledExecutor();
        timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
    }

    private void getFrame(){
        if (capture.isOpened()){
            Mat frame = new Mat();
            Mat frameHSV = new Mat();
            Mat frameMask =new Mat();
            capture.read(frame);
            //System.out.println("frame" + frame.toString());

			//int kernelSize = 3;
			//Imgproc.dilate(frame, frame,Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT, new Size(2 * kernelSize + 1, 2 * kernelSize + 1)));
			//Imgproc.GaussianBlur(frame, frame, new Size(15,15),50);
            Imgproc.cvtColor(frame, frameHSV, Imgproc.COLOR_BGR2HSV);
    		Scalar sCol = new Scalar(huesMin[doortype],satMinMax[0],valMinMax[0]);
    		Scalar eCol = new Scalar(huesMax[doortype],satMinMax[1],valMinMax[1]);
    		Core.inRange(frameHSV, sCol, eCol, frameMask);
    		isOpen = Core.mean(frameMask).val[0]>127;
    		if (isOpen) {
				System.out.println(isOpen);
				stop();
                timer.shutdown();
			}
			else {
				//System.out.println(isOpen);
			}
            //convertToImageView(frameMask);

        }
    }

    //THis code is used to debug and visualise the computed image by the model
    /*
    private void convertToImageView(Mat frame){
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", frame, buffer);
        Image imageToShow = new Image(new ByteArrayInputStream(buffer.toArray()));

        // run it in the main Thread
        Platform.runLater((Runnable) () -> imageView.setImage(imageToShow));
    }

     */

    public void stop(){
        capture.release();
        //imageView.setVisible(false);
    }

}
