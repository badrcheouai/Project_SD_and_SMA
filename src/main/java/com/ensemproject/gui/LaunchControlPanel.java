package com.ensemproject.gui;

import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class LaunchControlPanel extends JFrame {

    private final Map<String, JButton> launchButtons = new LinkedHashMap<>();
    private final ContainerController container;

    public LaunchControlPanel(ContainerController container) {
        this.container = container;
        setTitle("Panneau de Contrôle - Lancement des Véhicules");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(0, 1, 10, 10));

        // Liste des véhicules à lancer
        for (String agentName : new String[]{"V3", "V4", "V5", "V6"}) {
            JButton btn = new JButton("Lancer " + agentName);
            btn.addActionListener(e -> launchVehicle(agentName, btn));
            add(btn);
            launchButtons.put(agentName, btn);
        }
    }

    private void launchVehicle(String name, JButton btn) {
        try {
            AgentController agent = container.createNewAgent(name, "com.ensemproject.agents.VehicleAgent", null);
            agent.start();
            btn.setEnabled(false);
        } catch (StaleProxyException e) {
            JOptionPane.showMessageDialog(this, "Erreur de lancement pour " + name + ": " + e.getMessage());
        }
    }

    public static void launch(ContainerController container) {
        SwingUtilities.invokeLater(() -> {
            LaunchControlPanel panel = new LaunchControlPanel(container);
            panel.setVisible(true);
        });
    }
}

