package com.ensemproject.gui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParkingVisualizer extends JPanel {

    private final Map<String, Point> parkingSpots = new LinkedHashMap<>();
    private final Map<String, Point> cars = new LinkedHashMap<>();
    private final Map<String, Color> carColors = new LinkedHashMap<>();
    private final Map<String, String> spotOccupancy = new HashMap<>(); // spotId -> vehicleId

    public ParkingVisualizer() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);

        // Définir les emplacements des places de parking
        parkingSpots.put("P1", new Point(100, 100));
        parkingSpots.put("P2", new Point(250, 100));
        parkingSpots.put("P3", new Point(400, 100));
        parkingSpots.put("P4", new Point(550, 100));
        parkingSpots.put("P5", new Point(700, 100));

        // Voitures déjà garées
        cars.put("V1", new Point(250, 100)); // P2
        carColors.put("V1", Color.BLUE);
        spotOccupancy.put("P2", "V1");

        cars.put("V2", new Point(550, 100)); // P4
        carColors.put("V2", Color.GREEN);
        spotOccupancy.put("P4", "V2");

        // Voitures en attente, affichées en bas
        cars.put("V3", new Point(100, 500));
        carColors.put("V3", Color.MAGENTA);

        cars.put("V4", new Point(200, 500));
        carColors.put("V4", Color.ORANGE);

        cars.put("V5", new Point(300, 500));
        carColors.put("V5", Color.CYAN);

        cars.put("V6", new Point(400, 500));
        carColors.put("V6", Color.PINK);
    }

    public synchronized void addCar(String carId, String parkingId, Color color) {
        if (parkingId == null) {
            // Rejeté (parking complet) — afficher en bas
            System.out.println("⚠️ " + carId + " n’a pas obtenu de place. Affichage en gris.");
            cars.put(carId, new Point(100 + (cars.size() * 60), 550));
            carColors.put(carId, color);
            repaint();
            return;
        }

        if (spotOccupancy.containsKey(parkingId)) {
            System.out.println("⚠️ Place " + parkingId + " déjà occupée.");
            return;
        }

        if (!cars.containsKey(carId)) {
            // Si la voiture n'existe pas visuellement, l'ajouter en bas
            cars.put(carId, new Point(100 + (cars.size() * 60), 500));
            carColors.put(carId, color);
        }

        Point target = parkingSpots.get(parkingId);
        if (target != null) {
            spotOccupancy.put(parkingId, carId);

            Point start = new Point(cars.get(carId));

            new Timer(50, new java.awt.event.ActionListener() {
                Point current = new Point(start);

                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    int dx = target.x - current.x;
                    int dy = target.y - current.y;

                    if (Math.abs(dx) < 5 && Math.abs(dy) < 5) {
                        ((Timer) e.getSource()).stop();
                        cars.put(carId, new Point(target));
                        repaint();
                        return;
                    }

                    current.translate(dx / 10, dy / 10);
                    cars.put(carId, new Point(current));
                    repaint();
                }
            }).start();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Map.Entry<String, Point> entry : parkingSpots.entrySet()) {
            Point p = entry.getValue();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(p.x - 30, p.y - 20, 60, 40);
            g.setColor(Color.BLACK);
            g.drawString(entry.getKey(), p.x - 10, p.y + 5);
        }

        for (Map.Entry<String, Point> entry : cars.entrySet()) {
            String carId = entry.getKey();
            Point p = entry.getValue();
            g.setColor(carColors.getOrDefault(carId, Color.GRAY));
            g.fillOval(p.x - 10, p.y - 10, 20, 20);
            g.setColor(Color.BLACK);
            g.drawString(carId, p.x - 10, p.y - 15);
        }
    }
}
