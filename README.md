# 🚗 Système Multi-Agents de Gestion de Stationnement

Ce projet est une simulation d’un système intelligent de stationnement utilisant la plateforme **JADE** et une interface graphique **Java Swing**.

---

## 📦 Prérequis

* Java JDK 17 ou supérieur
* Bibliothèque `jade.jar` (inclus dans JADE)
* Fonctionne en terminal sous Windows / Linux / macOS

---

## ⚙️ Installation universelle en ligne de commande

### 1. Préparer les fichiers

Structure attendue :

```
Project_SD_and_SMA/
├── jade.jar
├── Main.java
├── agents/
│   ├── CentralAgent.java
│   ├── VehicleAgent.java
│   ├── ParkingSpotAgent.java
│   ├── VisualizerAgent.java
├── gui/
│   ├── ParkingVisualizer.java
│   ├── SharedVisualizer.java
│   ├── LaunchControlPanel.java
```

### 2. Compilation (terminal)

```bash
cd Project_SD_and_SMA
mkdir -p bin
javac -cp jade.jar -d bin $(find . -name "*.java")
```

👉 Sur **Windows**, utilisez `;` au lieu de `:` :

```bash
javac -cp jade.jar -d bin $(dir /s /b *.java)
```

### 3. Exécution

```bash
java -cp "bin:jade.jar" Main   # Linux/macOS
java -cp "bin;jade.jar" Main   # Windows
```

### 4. Optionnel : créer un JAR

```bash
jar cfe ProjectSMA.jar Main -C bin .
java -cp "ProjectSMA.jar:jade.jar" Main
```
