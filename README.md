# Network Robustness Analyzer

## Description

The **Network Robustness Analyzer** is a powerful desktop application designed for facility location optimization and sensitivity analysis. It allows users to solve the **weighted p-median problem** and evaluate how optimal solutions (such as ambulance or fire station locations) hold up when transportation network conditions change.

The application provides a modern, integrated **Dashboard** environment where you can run simulations, manage an archive of past experiments, and perform visual comparative analysis on an interactive map.

## Key Features

- **Project-Based Workflow:** Everything is contained within a single `.ntp` project file (network topology, experiment results, and settings).
- **Facility Location Optimization:** Find the most efficient locations for a set number of facilities ($p$) using state-of-the-art optimization solvers.
- **Sensitivity Experiments:**
  - **First-K Strategy:** Pinpoint exactly when a network delay causes the current facility setup to become sub-optimal.
  - **All-K Strategy:** Analyze how facility locations shift across a wide range of network modification levels.
- **Interactive Visualization:**
  - **Color-Coded Comparison:** Instantly see which facilities remain stable (orange), which are new (green), and which were removed (red).
  - **Network Heatmaps:** Visualizes speed declines and "elongated" routes based on the current analysis parameters.
- **Data Export:** Professional **Excel (.xlsx)** reports featuring a horizontal layout for easy comparison of different sensitivity snapshots.

---

## Requirements

### Java Development Kit (JDK)
You need **JDK 21** or newer to run this application.
```bash
java --version
```

### Maven
The project uses **Apache Maven** for building and dependency management.
```bash
mvn -version
```

---

## Installation & Build

1. **Clone the repository:**
   ```bash
   git clone https://github.com/DaliborBilicky/network-testing.git
   cd network-testing
   ```

2. **Build the project:**
   This command downloads all dependencies and creates an executable "Fat JAR" in the `target` folder:
   ```bash
   mvn clean package
   ```

## Running the Application

Launch the GUI using the generated JAR file:

```bash
java -jar target/network-testing-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## User Guide

### 1. Managing Projects
The application uses the `.ntp` format to keep your work organized.
- **New Project:** Click `File -> New Project`. You will need to provide a nodes file (`vertices.txt`), an edges file (`edges.txt`), and an optional coordinates file (`coords.txt`).
- **Open Project:** Load an existing `.ntp` file to continue your work or review old results.

### 2. Running an Analysis
Use the **Execution Panel** to start a new experiment:
- **P-Median Range:** Set the number of facilities you want to test (e.g., from 5 to 10).
- **Search Strategy:** Choose between **First-K** (finding the first change point) or **All-K** (evolution mapping).
- **Base Speed:** Set the reference speed (in km/h) for the transportation network.
- **Run/Stop:** Click **RUN** to start. The analysis runs in the background, allowing you to use other parts of the app simultaneously.

### 3. Reviewing Results
- **Experiment Archive:** Browse your past runs. Selecting an experiment loads its data into the timeline.
- **Results Timeline:** A tree view of all computed solutions. Clicking a "snapshot" updates the map and statistics.
- **Stats Panels:** Compare the metrics (Objective values, Irregularity, Speed statistics) between the current and previous steps.
- **Interactive Map:** Use the mouse wheel to zoom and left-click to drag. The map shows facility locations and highlights network congestion levels.

### 4. Exporting Data
Click the **Export** icon on any experiment card in the Archive to generate an Excel spreadsheet. This report contains all computed values, objectives, and facility coordinates for every step of the experiment.

---

## Technical Note: Google OR-Tools
This project uses **Google OR-Tools** for mathematical optimization. Native libraries are handled automatically by the application. Ensure you have the appropriate system permissions to execute native binaries on your OS.
