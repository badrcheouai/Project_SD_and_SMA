package com.ensemproject;

import com.ensemproject.gui.LaunchControlPanel;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;

public class Main {
    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.instance();
            Profile p = new ProfileImpl();
            p.setParameter(Profile.GUI, "true");
            ContainerController cc = rt.createMainContainer(p);

            // 1. Lancer l’interface graphique en premier (elle place V1 et V2 dans P2 et P4)
            cc.createNewAgent("Visualizer", "com.ensemproject.agents.VisualizerAgent", null).start();

            // 2. Attendre un peu pour s’assurer que la fenêtre est prête
            Thread.sleep(1000);

            // 3. Lancer l’agent central
            cc.createNewAgent("Central", "com.ensemproject.agents.CentralAgent", null).start();

            // 4. Lancer V1 et V2 déjà placées (P2 et P4)
            cc.createNewAgent("V1", "com.ensemproject.agents.VehicleAgent", null).start();
            cc.createNewAgent("V2", "com.ensemproject.agents.VehicleAgent", null).start();

            // 5. Lancer les places
            cc.createNewAgent("P1", "com.ensemproject.agents.ParkingSpotAgent", new Object[]{"free"}).start();
            cc.createNewAgent("P2", "com.ensemproject.agents.ParkingSpotAgent", new Object[]{"occupied"}).start(); // V1
            cc.createNewAgent("P3", "com.ensemproject.agents.ParkingSpotAgent", new Object[]{"free"}).start();
            cc.createNewAgent("P4", "com.ensemproject.agents.ParkingSpotAgent", new Object[]{"occupied"}).start(); // V2
            cc.createNewAgent("P5", "com.ensemproject.agents.ParkingSpotAgent", new Object[]{"free"}).start();

            // 6. Afficher les boutons pour lancer V3 à V6 (visuellement déjà présents dans la fenêtre)
            LaunchControlPanel.launch(cc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
