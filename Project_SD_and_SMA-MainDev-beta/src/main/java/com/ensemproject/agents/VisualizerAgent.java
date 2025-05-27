package com.ensemproject.agents;

import com.ensemproject.gui.ParkingVisualizer;
import com.ensemproject.gui.SharedVisualizer;
import jade.core.Agent;

import javax.swing.*;

public class VisualizerAgent extends Agent {
    @Override
    protected void setup() {
        SwingUtilities.invokeLater(() -> {
            ParkingVisualizer visualizer = new ParkingVisualizer();
            SharedVisualizer.setInstance(visualizer);

            JFrame frame = new JFrame("Visualisation collective des véhicules");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(visualizer);
            frame.pack();
            frame.setLocation(500, 100);
            frame.setVisible(true);
        });
    }
}
