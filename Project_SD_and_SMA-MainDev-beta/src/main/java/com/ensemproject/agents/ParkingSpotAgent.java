package com.ensemproject.agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.MessageTemplate;

public class ParkingSpotAgent extends Agent {
    private String status = "free";

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            status = (String) args[0];
        }

        System.out.println("🚗 " + getLocalName() + " prêt. État : " + status);

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF));
                if (msg != null) {
                    System.out.println("📥 " + getLocalName() + " → requête reçue de " + msg.getSender().getLocalName());
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent(status);
                    send(reply);
                    System.out.println("📤 " + getLocalName() + " → réponse envoyée : " + status);
                } else {
                    block();
                }
            }
        });
    }
}
