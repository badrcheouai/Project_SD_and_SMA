package com.ensemproject.agents;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.MessageTemplate;

import java.util.*;

public class CentralAgent extends Agent {
    private final Set<String> assignedSpots = new HashSet<>();
    private final List<String> availableSpots = new ArrayList<>();
    private String requesterVehicle = "";

    @Override
    protected void setup() {
        System.out.println("✅ CentralAgent prêt.");
        addBehaviour(new ReceiveRequestBehaviour());
    }

    private class ReceiveRequestBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
            if (msg != null) {
                requesterVehicle = msg.getSender().getLocalName();
                System.out.println("📨 Requête reçue de " + requesterVehicle);

                SequentialBehaviour seq = new SequentialBehaviour();
                seq.addSubBehaviour(new QueryParkingSpotsBehaviour());
                seq.addSubBehaviour(new SendSuggestionBehaviour());
                myAgent.addBehaviour(seq);

                // Pause pour éviter les collisions
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println("⛔ Sleep interrupted : " + e.getMessage());
                }
            } else {
                block();
            }
        }
    }

    private class QueryParkingSpotsBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            availableSpots.clear();
            ACLMessage request = new ACLMessage(ACLMessage.QUERY_REF);
            String[] parkingAgents = {"P1", "P2", "P3", "P4", "P5"};

            for (String agent : parkingAgents) {
                request.addReceiver(new AID(agent, AID.ISLOCALNAME));
            }

            request.setContent("status?");
            myAgent.send(request);
            System.out.println("📡 Central → Requête envoyée à tous les parkings");

            for (int i = 0; i < parkingAgents.length; i++) {
                ACLMessage reply = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if (reply != null) {
                    System.out.println("📬 Réponse de " + reply.getSender().getLocalName() + ": " + reply.getContent());
                    if ("free".equalsIgnoreCase(reply.getContent())) {
                        availableSpots.add(reply.getSender().getLocalName());
                    }
                }
            }

            System.out.println("✅ Places disponibles (non occupées) : " + availableSpots);
        }
    }

    private class SendSuggestionBehaviour extends OneShotBehaviour {
        @Override
        public void action() {
            // Filtrer les places déjà attribuées
            Optional<String> maybeSpot = availableSpots.stream()
                    .filter(s -> !assignedSpots.contains(s))
                    .min(Comparator.comparingInt(s -> Integer.parseInt(s.substring(1))));

            if (maybeSpot.isEmpty()) {
                ACLMessage fullMsg = new ACLMessage(ACLMessage.INFORM);
                fullMsg.addReceiver(new AID(requesterVehicle, AID.ISLOCALNAME));
                fullMsg.setContent("❌ Aucune place disponible. Parking complet !");
                myAgent.send(fullMsg);
                System.out.println("🚫 Parking complet → " + requesterVehicle);
                return;
            }

            String bestSpot = maybeSpot.get();
            assignedSpots.add(bestSpot);

            ACLMessage suggestion = new ACLMessage(ACLMessage.INFORM);
            suggestion.addReceiver(new AID(requesterVehicle, AID.ISLOCALNAME));
            suggestion.setContent("Place recommandée : " + bestSpot);
            myAgent.send(suggestion);
            System.out.println("📤 Suggestion envoyée à " + requesterVehicle + ": " + bestSpot);
        }
    }
}
