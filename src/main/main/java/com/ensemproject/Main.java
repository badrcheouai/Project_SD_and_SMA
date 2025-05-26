package com.ensemproject;

import com.ensemproject.gui.LaunchControlPanel;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) {
        ContainerController cc = null;
        try {
            cc = initializeJadeContainer();
            initializeAgents(cc);
            LaunchControlPanel.launch(cc);
        } catch (StaleProxyException e) {
            handleError("Agent creation failed", e);
            System.exit(1);
        } catch (Exception e) {
            handleError("Unexpected error occurred", e);
            System.exit(1);
        }
    }

    private static ContainerController initializeJadeContainer() {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.MAIN_PORT, "1099");
        p.setParameter(Profile.GUI, "false");
        
        ContainerController cc = rt.createMainContainer(p);
        System.out.println("✅ Main container created successfully");
        return cc;
    }

    private static void initializeAgents(ContainerController cc) throws StaleProxyException {
        // Initialize Visualizer Agent
        cc.createNewAgent("Visualizer", "com.ensemproject.agents.VisualizerAgent", null).start();

        // Initialize Central Control Agent
        cc.createNewAgent("Central", "com.ensemproject.agents.CentralAgent", null).start();

        // Initialize Vehicle Agents (V1 and V2 initially placed in P2 and P4)
        cc.createNewAgent("V1", "com.ensemproject.agents.VehicleAgent", null).start();
        cc.createNewAgent("V2", "com.ensemproject.agents.VehicleAgent", null).start();

        // Initialize Parking Spot Agents
        initializeParkingSpots(cc);
    }

    private static void initializeParkingSpots(ContainerController cc) throws StaleProxyException {
        // Initial parking spot configuration:
        // P1, P3, P5: free
        // P2: occupied by V1
        // P4: occupied by V2
        cc.createNewAgent("P1", "com.ensemproject.agents.ParkingSpotAgent", new Object[]{"free"}).start();
        cc.createNewAgent("P2", "com.ensemproject.agents.ParkingSpotAgent", new Object[]{"occupied"}).start();
        cc.createNewAgent("P3", "com.ensemproject.agents.ParkingSpotAgent", new Object[]{"free"}).start();
        cc.createNewAgent("P4", "com.ensemproject.agents.ParkingSpotAgent", new Object[]{"occupied"}).start();
        cc.createNewAgent("P5", "com.ensemproject.agents.ParkingSpotAgent", new Object[]{"free"}).start();
    }

    private static void handleError(String message, Exception e) {
        System.err.println(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}