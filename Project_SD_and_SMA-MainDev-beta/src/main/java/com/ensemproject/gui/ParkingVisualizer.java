package com.ensemproject.gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParkingVisualizer extends JPanel {

    private final Map<String, Point> parkingSpots = new LinkedHashMap<>();
    private final Map<String, Point> cars = new LinkedHashMap<>();
    private final Map<String, Point> defaultCarPositions = new LinkedHashMap<>();
    private final Map<String, Color> carColors = new LinkedHashMap<>();
    private final Map<String, String> spotOccupancy = new HashMap<>(); // spotId -> vehicleId
    private final Map<String, Timer> carTimers = new HashMap<>(); // Prevent multiple animations
    private JLabel statusLabel;
    private int availableSpaces;
    private JPanel controlPanel;

    public ParkingVisualizer() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(900, 650));
        setBackground(new Color(245, 245, 245));

        // Status panel
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(230, 230, 250));
        statusLabel = new JLabel("Available Spaces: 3");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.NORTH);

        // Control panel
        controlPanel = new JPanel();
        controlPanel.setBackground(new Color(230, 230, 250));
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
        String[] carIds = {"V1", "V2", "V3", "V4", "V5", "V6"};
        for (String carId : carIds) {
            JPanel carControlPanel = new JPanel();
            carControlPanel.setOpaque(false);
            carControlPanel.setLayout(new BoxLayout(carControlPanel, BoxLayout.Y_AXIS));
            JLabel carLabel = new JLabel(carId);
            carLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            carLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            carControlPanel.add(carLabel);
            JButton parkButton = new JButton("Garer");
            parkButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            parkButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            parkButton.setToolTipText("Gare la voiture sur la première place libre");
            parkButton.addActionListener(e -> parkCar(carId));
            carControlPanel.add(parkButton);
            JButton releaseButton = new JButton("Libérer");
            releaseButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            releaseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            releaseButton.setToolTipText("Libère la voiture et la ramène à sa position d'attente");
            releaseButton.addActionListener(e -> releaseCar(carId));
            carControlPanel.add(releaseButton);
            controlPanel.add(carControlPanel);
        }
        add(controlPanel, BorderLayout.SOUTH);

        // Visualization panel
        JPanel visualizationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                drawBackground(g2d);
                drawParkingSpots(g2d);
                drawCars(g2d);
                drawAreaLabels(g2d);
            }
        };
        visualizationPanel.setPreferredSize(new Dimension(900, 500));
        visualizationPanel.setBackground(new Color(245, 245, 245));
        add(visualizationPanel, BorderLayout.CENTER);

        // Parking spots (centered horizontally)
        int y = 170;
        int[] xCoords = {140, 290, 440, 590, 740};
        String[] spotIds = {"P1", "P2", "P3", "P4", "P5"};
        for (int i = 0; i < spotIds.length; i++) {
            parkingSpots.put(spotIds[i], new Point(xCoords[i], y));
        }

        // Unique waiting positions for each car (bottom row, spaced out, always inside window)
        int waitY = 400;
        int waitStartX = 140;
        int waitSpacing = 120;
        for (int i = 0; i < carIds.length; i++) {
            int wx = waitStartX + i * waitSpacing;
            if (wx > 820) wx = 820; // Clamp to window
            defaultCarPositions.put(carIds[i], new Point(wx, waitY));
        }
        cars.putAll(defaultCarPositions);

        // Car colors
        carColors.put("V1", new Color(0, 120, 215)); // Blue
        carColors.put("V2", new Color(0, 150, 0));   // Green
        carColors.put("V3", new Color(180, 0, 180)); // Magenta
        carColors.put("V4", new Color(255, 140, 0)); // Orange
        carColors.put("V5", new Color(0, 180, 180)); // Cyan
        carColors.put("V6", new Color(255, 105, 180)); // Pink

        // Initial occupied spots
        spotOccupancy.put("P2", "V1");
        spotOccupancy.put("P4", "V2");
        cars.put("V1", parkingSpots.get("P2"));
        cars.put("V2", parkingSpots.get("P4"));

        updateAvailableSpaces();
    }

    private void parkCar(String carId) {
        // Find first available spot
        String availableSpot = null;
        for (String spotId : parkingSpots.keySet()) {
            if (!spotOccupancy.containsKey(spotId)) {
                availableSpot = spotId;
                break;
            }
        }
        if (availableSpot != null) {
            addCar(carId, availableSpot, carColors.get(carId));
        } else {
            JOptionPane.showMessageDialog(this, "Parking complet! Impossible de garer " + carId, "Parking plein", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void releaseCar(String carId) {
        // Find the spot occupied by this car
        String occupiedSpot = null;
        for (Map.Entry<String, String> entry : spotOccupancy.entrySet()) {
            if (entry.getValue().equals(carId)) {
                occupiedSpot = entry.getKey();
                break;
            }
        }
        if (occupiedSpot != null) {
            spotOccupancy.remove(occupiedSpot);
            Point defaultPos = defaultCarPositions.get(carId);
            if (defaultPos != null) {
                moveCarToPosition(carId, defaultPos);
            }
            updateAvailableSpaces();
        }
    }

    private void moveCarToPosition(String carId, Point target) {
        // Stop any existing animation for this car
        if (carTimers.containsKey(carId)) {
            carTimers.get(carId).stop();
        }
        Point start = new Point(cars.get(carId));
        final int steps = 25;
        final int[] currentStep = {0};
        final double stepX = (target.x - start.x) / (double) steps;
        final double stepY = (target.y - start.y) / (double) steps;
        Timer timer = new Timer(15, null);
        carTimers.put(carId, timer);
        timer.addActionListener(e -> {
            currentStep[0]++;
            int newX = (int) Math.round(start.x + stepX * currentStep[0]);
            int newY = (int) Math.round(start.y + stepY * currentStep[0]);
            cars.put(carId, new Point(newX, newY));
            repaint();
            if (currentStep[0] >= steps) {
                cars.put(carId, new Point(target.x, target.y));
                timer.stop();
                carTimers.remove(carId);
                repaint();
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    private void updateAvailableSpaces() {
        availableSpaces = parkingSpots.size() - spotOccupancy.size();
        statusLabel.setText("Available Spaces: " + availableSpaces);
        statusLabel.setForeground(availableSpaces == 0 ? new Color(200, 0, 0) : new Color(0, 120, 0));
    }

    private void drawBackground(Graphics2D g) {
        // Draw parking area background
        g.setColor(new Color(230, 240, 250));
        g.fillRoundRect(60, 60, 780, 200, 30, 30);
        g.setColor(new Color(180, 180, 200));
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(60, 60, 780, 200, 30, 30);
    }

    private void drawParkingSpots(Graphics2D g) {
        for (Map.Entry<String, Point> entry : parkingSpots.entrySet()) {
            Point p = entry.getValue();
            String spotId = entry.getKey();
            boolean isOccupied = spotOccupancy.containsKey(spotId);
            GradientPaint gradient = new GradientPaint(
                p.x - 30, p.y - 20,
                isOccupied ? new Color(255, 200, 200) : new Color(200, 255, 200),
                p.x + 30, p.y + 20,
                isOccupied ? new Color(255, 150, 150) : new Color(150, 255, 150)
            );
            g.setPaint(gradient);
            g.fillRoundRect(p.x - 40, p.y - 30, 80, 60, 18, 18);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2));
            g.drawRoundRect(p.x - 40, p.y - 30, 80, 60, 18, 18);
            g.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g.drawString(spotId, p.x - 15, p.y + 5);
            g.setColor(isOccupied ? Color.RED : Color.GREEN);
            g.fillOval(p.x + 25, p.y - 20, 16, 16);
        }
    }

    private void drawCars(Graphics2D g) {
        for (Map.Entry<String, Point> entry : cars.entrySet()) {
            String carId = entry.getKey();
            Point p = entry.getValue();
            g.setColor(new Color(0, 0, 0, 40));
            g.fillOval(p.x - 12, p.y + 8, 28, 10);
            g.setColor(carColors.getOrDefault(carId, Color.GRAY));
            g.fillOval(p.x - 15, p.y - 15, 30, 30);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2));
            g.drawOval(p.x - 15, p.y - 15, 30, 30);
            g.setFont(new Font("Segoe UI", Font.BOLD, 14));
            g.setColor(Color.WHITE);
            g.drawString(carId, p.x - 12, p.y + 5);
        }
    }

    private void drawAreaLabels(Graphics2D g) {
        g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.setColor(new Color(60, 60, 120));
        g.drawString("Parking Area", 60, 90);
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.setColor(new Color(80, 80, 80));
        g.drawString("Waiting Area", 60, 390);
    }

    public synchronized void addCar(String carId, String parkingId, Color color) {
        if (parkingId == null) {
            System.out.println("⚠️ " + carId + " n'a pas obtenu de place. Affichage en gris.");
            moveCarToPosition(carId, defaultCarPositions.get(carId));
            return;
        }
        if (spotOccupancy.containsKey(parkingId)) {
            System.out.println("⚠️ Place " + parkingId + " déjà occupée.");
            return;
        }
        spotOccupancy.put(parkingId, carId);
        updateAvailableSpaces();
        moveCarToPosition(carId, parkingSpots.get(parkingId));
    }
}
