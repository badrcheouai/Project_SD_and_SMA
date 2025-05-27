---

## ⚙️ Installation & Exécution

1. Importer le projet dans IntelliJ
2. Ajouter le fichier `jade.jar` comme librairie
3. Exécuter la classe `Main.java`

> Cela démarre automatiquement :
> - L’interface graphique principale (`VisualizerAgent`)
> - L’agent de coordination central (`CentralAgent`)
> - Les 5 agents de stationnement (`P1` à `P5`)
> - Les voitures `V1` (placée en `P2`) et `V2` (placée en `P4`)
> - Le panneau de contrôle pour lancer `V3`, `V4`, `V5`, `V6`

---

## 🖥️ Guide Utilisateur

### 🪟 Fenêtre de visualisation
- Affiche les places de parking (`P1` à `P5`)
- `V1` et `V2` sont déjà stationnées
- `V3` à `V6` sont visibles en bas (non encore garées)

### 🎛 Panneau de contrôle
- Boutons `Lancer V3`, `Lancer V4`, etc.
- En cliquant :
  - Le véhicule est activé
  - Il envoie une requête
  - Il est animé automatiquement vers une place libre

---

## 🚫 Parking complet
- Si aucune place n’est disponible :
  - Le véhicule reçoit un message "Parking complet"
  - Il apparaît en **gris**, sans animation, dans le bas de la fenêtre

---

## ❌ Fermer le système
- Fermez la fenêtre **“Visualisation collective des véhicules”** pour arrêter tous les agents

---

## 📌 Remarques Techniques

- Une temporisation de 1 seconde est appliquée entre les requêtes pour éviter les conflits
- Le `CentralAgent` maintient une liste des places déjà attribuées (`assignedSpots`)
- `SharedVisualizer` permet à tous les agents d'accéder à l'interface graphique unique

---

## 📚 Crédit

Projet réalisé dans le cadre du système multi-agents avec :
- Java 17
- JADE Framework
- Java Swing
