package com.ensemproject;

import com.ensemproject.gui.LaunchControlPanel;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize JADE runtime
            Runtime rt = Runtime.instance();
            
            // Create a profile for the main container
            Profile p = new ProfileImpl();
            p.setParameter(Profile.MAIN_HOST, "localhost");
            p.setParameter(Profile.MAIN_PORT, "1099");
            p.setParameter(Profile.GUI, "false");
            
            // Create the main container
            ContainerController cc = rt.createMainContainer(p);
            System.out.println("✅ Main container created successfully");

            // 1. Launch the GUI first (it places V1 and V2 in P2 and P4)
            cc.createNewAgent("Visualizer", "com.ensemproject.agents.VisualizerAgent", null).start();

            // 2. Wait a bit to ensure the window is ready
            Thread.sleep(1000);

            // 3. Launch the central agent
            cc.createNewAgent("Central", "com.ensemproject.agents.CentralAgent", null).start();

            // 4. Launch V1 and V2 already placed (P2 and P4)
            cc.createNewAgent("V1", "com.ensemproject.agents.VehicleAgent", null).start();
            cc.createNewAgent("V2", "com.ensemproject.agents.VehicleAgent", null).start();

            // 5. Launch the parking spots
            cc.createNewAgent("P1", "com.ensemproject.agents.ParkingSpotAgent", new Object[]{"free"}).start();
            cc.createNewAgent("P2", "com.ensemproject.agents.ParkingSpotAgent", new Object[]{"occupied"}).start(); // V1
            cc.createNewAgent("P3", "com.ensemproject.agents.ParkingSpotAgent", new Object[]{"free"}).start();
            cc.createNewAgent("P4", "com.ensemproject.agents.ParkingSpotAgent", new Object[]{"occupied"}).start(); // V2
            cc.createNewAgent("P5", "com.ensemproject.agents.ParkingSpotAgent", new Object[]{"free"}).start();

            // 6. Display buttons to launch V3 to V6 (visually already present in the window)
            // LaunchControlPanel.launch(cc); // Removed as requested

        } catch (StaleProxyException e) {
            System.err.println("Error creating agents: " + e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.err.println("Error during sleep: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
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