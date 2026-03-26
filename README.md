# Network Testing

## Description

This application provides a visual and interactive environment for solving the 
**weighted p-median problem** and analyzing its sensitivity to network changes. 
Using **Google OR-Tools**, it finds optimal facility locations and tests their 
robustness by simulating speed declines across a transportation network.

Contains features:
1.  **Sensitivity Experiments** (Solver execution and $k$-parameter analysis)
2.  **Visual Analysis** (Interactive graph visualization of results)
3.  **Synthetic Data Generation** (Graphs and population distributions)

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

## Installation & Build

1. **Clone the repository:**
   ```bash
   git clone https://github.com/DaliborBilicky/network-testing.git
   cd network-testing
   ```

2. **Build the project:**
   This command downloads all dependencies (including OR-Tools native libraries) and creates an executable "Fat JAR":
   ```bash
   mvn clean package
   ```

## Running the Application

After building, launch the GUI using the generated JAR file:

```bash
java -jar target/network-testing-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## Features & Usage

The application is divided into three main modules accessible via the sidebar:

### 1. Experiment Tab
The core engine for solving the p-median problem.
*   **Input:** Load your Edges and Vertices files.
*   **P-Range:** Run experiments for a range of facility counts (e.g., test 5 
through 15 facilities at once).
*   **Sensitivity:** Choose between:
    *   **First-K:** Find the exact point where the optimal solution changes.
    *   **All-K:** Test how the solution evolves as the sensitivity parameter 
$k$ increases.
*   **Output:** Generates a detailed `.json` report containing objectives, 
selected medians, and speed statistics.

### 2. Visualize Tab
A visual tool to understand the results.
*   **Interactive Canvas:** Load a result JSON and the corresponding network 
files to see the graph.
*   **Timeline:** Scrub through different values of $k$ (sensitivity levels) to 
see how facility locations shift on the map.
*   **Export:** Save the current visualization as a `.png` image.

### 3. Generate Tab
Create synthetic transport networks to test algorithms and simulate various 
demographic scenarios.
*   **Topology Settings:** Configure the underlying network structure and 
connectivity. You can adjust parameters to control the graph layout, density, 
and route redundancy.
*   **Weight Strategies:** Define how population or demand is distributed across 
the network nodes. 
*   **Export:** Save your generated network topology and vertex data to files 
for use in experiments and visualization.

---

## Understanding the Results

When running an experiment, the app analyzes how a solution holds up when the 
network is "elongated" (simulating delays). 

*   **Sensitivity parameter $k$:** Represents the magnitude of the network modification.
*   **Optimal Objective:** The best possible travel cost for the modified network.
*   **Cross Solution Objective:** The travel cost if you stick with the 
*original* facility locations on the *newly modified* network.
*   **Selected p-medians:** The indices of vertices chosen as optimal facility 
locations (e.g., ambulance stations).

## Technical Note: Google OR-Tools
This project uses **Google OR-Tools** for linear programming. On the first run,
Maven will handle the native library extraction. Ensure you have the appropriate 
system permissions to execute these libraries.
