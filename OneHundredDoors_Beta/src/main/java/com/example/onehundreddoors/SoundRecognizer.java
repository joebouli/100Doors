package com.example.onehundreddoors;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javax.sound.sampled.*;

public class SoundRecognizer {
    private StringProperty soundIntensityProperty = new SimpleStringProperty("0"); // Property to store sound intensity
    private boolean soundRecognitionRunning = false; // Flag to indicate if sound recognition is running
    double intensity; // Sound intensity value

    private TargetDataLine line; // Audio input line

    private Thread soundRecognitionThread; // Sound recognition thread
    private static final int SAMPLE_RATE = 44100; // Sampling frequency (Hz)
    private static final int BUFFER_SIZE = 4096; // Audio reading buffer size

    public SoundRecognizer() {
        // Class initialization
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Microphone not supported.");
        }
        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            // line.start(); // Commented out for now, will be started in the startSoundRecognition method.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean startSoundRecognition(double soundThreshold) {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Microphone not supported.");
                return false;
            }

            // TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info); // Commented out as it's created in the constructor.
            // line.open(format);
            line.start(); // Start audio input

            byte[] buffer = new byte[BUFFER_SIZE];

            soundRecognitionRunning = true; // Set the flag to indicate that sound recognition is running

            while (soundRecognitionRunning) {
                int bytesRead = line.read(buffer, 0, buffer.length);

                // Calculate sound intensity
                double soundIntensity = calculateSoundIntensity(buffer, bytesRead);

                // Update sound intensity property
                setSoundIntensity(soundIntensity);

                if (soundIntensity >= soundThreshold) {
                    System.out.println(soundIntensity);
                    return true; // Door opened
                }

                Thread.sleep(100); // Interval between audio readings
            }

            line.close(); // Close the audio input
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // Door not opened
    }

    public void stopSoundRecognition() {
        soundRecognitionRunning = false; // Stop sound recognition
        if (soundRecognitionThread != null) {
            soundRecognitionThread.interrupt(); // Stop the sound recognition thread
        }
    }

    private void setSoundIntensity(double intensity) {
        this.intensity = intensity; // Set the sound intensity value
    }

    public double getIntensity() {
        return intensity; // Get the current sound intensity
    }

    private double calculateSoundIntensity(byte[] buffer, int bytesRead) {
        // Calculate sound intensity using the average of the absolute values of audio samples
        double sum = 0.0;
        for (int i = 0; i < bytesRead; i += 2) {
            short sample = (short) ((buffer[i] & 0xFF) | (buffer[i + 1] << 8));
            sum += Math.abs(sample);
        }
        double average = sum / (bytesRead / 2);
        return average; // Return the calculated sound intensity
    }
}
