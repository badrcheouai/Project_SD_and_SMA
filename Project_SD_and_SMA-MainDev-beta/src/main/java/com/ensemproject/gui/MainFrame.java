package com.ensemproject.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private final JTextArea logArea = new JTextArea(15, 40);
    private final JButton requestButton = new JButton("🔍 Rechercher une place");

    public MainFrame() {
        setTitle("Système Intelligent de Stationnement");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        logArea.setEditable(false);
        JPanel topPanel = new JPanel();
        topPanel.add(requestButton);
        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);
    }

    public void addLog(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append(msg + "\n"));
    }

    public void setRequestAction(ActionListener listener) {
        requestButton.addActionListener(listener);
    }
}
