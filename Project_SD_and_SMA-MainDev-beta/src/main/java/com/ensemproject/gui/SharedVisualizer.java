package com.ensemproject.gui;

public class SharedVisualizer {
    private static ParkingVisualizer instance;

    public static synchronized ParkingVisualizer getInstance() {
        return instance;
    }

    public static synchronized void setInstance(ParkingVisualizer vis) {
        instance = vis;
        System.out.println("✅ SharedVisualizer initialisé !");
    }
}
