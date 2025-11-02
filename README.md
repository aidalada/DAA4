# Assignment 4: Graph Algorithms for Scheduling (SCC, TopoSort, DAG-SP)

## Overview

This project implements and tests three core graph algorithms—**Kosaraju's Algorithm for Strongly Connected Components (SCC)**, **Kahn's Algorithm for Topological Sort**, and **Shortest/Longest Paths on a Directed Acyclic Graph (DAG)**—to solve a simplified task-scheduling problem.

The primary goal is to first condense the original task graph (which may contain cycles) into a Condensation Graph (a pure DAG of SCCs), and then find optimal paths on that resulting DAG. Performance is tracked using execution time and operation counters (metrics).

## Project Structure

The project is structured to separate algorithms, metrics, and data:

```
ADS4/
├── src/main/java/org/example/
│   ├── Main.java              # Entry point and Test Runner
│   ├── KosarajuSCC.java       # Implements SCC and Condensation Graph builder
│   ├── TopologicalSorter.java # Implements Kahn's Algorithm for TopoSort
│   ├── DAGShortestPath.java   # Implements Shortest/Longest Paths on DAG
│   └── ... (Metrics, Graph classes)
├── data/
│   ├── small_1_dag.json       # All 9 generated datasets
│   └── ...
├── pom.xml                    # Maven configuration
└── README.md                  # This file
```

## Build and Run Instructions

This project uses **Maven**.

### Prerequisites

  * Java Development Kit (JDK) 17+
  * Maven 3+

### 1\. Build the Project

Open your terminal in the root directory (`~/IdeaProjects/ADS4`) and run:

```bash
# Clean and compile the Java code
mvn clean install
```

### 2\. Run the Main Test Suite

The `Main.java` file is configured to sequentially load, process, and test all 9 JSON files located in the `/data` directory and print the timing and metric results to the console.

```bash
# Execute the Main class
mvn exec:java -Dexec.mainClass="org.example.Main"
```

### Expected Output

The program will output the processing details for each of the 9 files, including **time (ms)** and **metric counters** (DFS visits, Kahn operations, Relaxations).

```
--- Starting Integrated Algorithm Testing ---

--------------------------------------------------
Processing file: data/small_1_dag.json
  [SCC] Components found: 6. Time: X.XX ms
  [TOPO] Time: X.XX ms
  [PATHS] Time: X.XX ms

  --- Metrics Summary ---
  | SCC: DFS_VISITS: YYY, EDGE_CHECKS: ZZZ
  | TOPO: KAHN_OPS (Push/Pop): AAA
  | PATHS: RELAXATIONS: BBB
... (repeated for 8 more files)
```

## Data Summary

The project is tested on 9 custom-generated datasets, covering three graph sizes (Small, Medium, Large) and three structural types (DAG, Single Cycle, Mixed).

| Size | Vertices (N) | Files | Structure Types | Weight Model |
| :--- | :--- | :--- | :--- | :--- |
| **Small** | 6-10 | 3 | DAG, Cycle, Mixed | Edge Weights |
| **Medium** | 15-20 | 3 | DAG, Cycle, Mixed | Edge Weights |
| **Large** | 40-50 | 3 | DAG, Cycle, Mixed | Edge Weights |

  * **Weight Model Used:** Edge Weights (`"weight_model": "edge"`) is consistently used for Shortest/Longest Path calculations.

