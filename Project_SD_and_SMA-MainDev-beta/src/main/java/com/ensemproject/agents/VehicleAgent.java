package com.ensemproject.agents;

import com.ensemproject.gui.ParkingVisualizer;
import com.ensemproject.gui.SharedVisualizer;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.awt.*;

public class VehicleAgent extends Agent {

    @Override
    protected void setup() {
        if (getLocalName().equals("V1") || getLocalName().equals("V2")) {
            System.out.println(getLocalName() + " 🚗 est déjà garée. Pas de requête.");
            return;
        }

        sendRequest();

        addBehaviour(new jade.core.behaviours.CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if (msg != null) {
                    String content = msg.getContent();
                    System.out.println(getLocalName() + " 🔁 message reçu : " + content);

                    ParkingVisualizer visualizer = null;
                    int retry = 0;
                    while (visualizer == null && retry < 50) {
                        visualizer = SharedVisualizer.getInstance();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ignored) {}
                        retry++;
                    }

                    if (visualizer == null) {
                        System.err.println("❌ " + getLocalName() + " → Visualizer non prêt !");
                        return;
                    }

                    if (content.contains("Parking complet")) {
                        System.out.println("🚫 " + getLocalName() + " n’a pas trouvé de place.");
                        visualizer.addCar(getLocalName(), null, Color.GRAY); // afficher en bas en gris
                        return;
                    }

                    String[] parts = content.split(": ");
                    if (parts.length == 2) {
                        String targetSpot = parts[1].trim();
                        System.out.println(getLocalName() + " 🏁 va se déplacer vers : " + targetSpot);

                        Color color = switch (getLocalName()) {
                            case "V3" -> Color.MAGENTA;
                            case "V4" -> Color.ORANGE;
                            case "V5" -> Color.CYAN;
                            case "V6" -> Color.PINK;
                            default -> Color.GRAY;
                        };

                        visualizer.addCar(getLocalName(), targetSpot, color);
                    }
                } else {
                    block();
                }
            }
        });
    }

    private void sendRequest() {
        ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
        req.addReceiver(new jade.core.AID("Central", jade.core.AID.ISLOCALNAME));
        req.setContent("Besoin d'une place");
        send(req);
        System.out.println(getLocalName() + " 📨 Requête envoyée.");
    }
}
