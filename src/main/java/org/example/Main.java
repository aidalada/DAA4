package org.example;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Main {

    private static class TaskData {
        int n;
        List<Edge> edges;
        int source;
    }

    public static void main(String[] args) {
        String[] dataFiles = {
                "data/small_1_dag.json",
                "data/small_2_cycle.json",
                "data/small_3_mixed.json",

                "data/medium_1_dag.json",
                "data/medium_2_cycle.json",
                "data/medium_3_mixed.json",

                "data/large_1_dag.json",
                "data/large_2_cycle.json",
                "data/large_3_mixed.json"
        };

        System.out.println("--- Starting Integrated Algorithm Testing ---");

        for (String filePath : dataFiles) {
            System.out.println("\n- - - ");
            System.out.println("Processing file: " + filePath);

            try {
                TaskGraph graph = loadGraphFromJson(filePath);
                if (graph == null) continue;

                SimpleMetrics metrics = new SimpleMetrics();

                KosarajuSCC sccFinder = new KosarajuSCC(metrics);
                CondensationGraph dag = sccFinder.findSCCs(graph);

                long sccTime = metrics.getElapsedTimeNanos();

                System.out.printf("  [SCC] Components found: %d. Time: %.2f ms%n",
                        dag.getNumComponents(), sccTime / 1_000_000.0);


                metrics.reset();
                TopologicalSorter topoSorter = new TopologicalSorter(metrics);
                List<Integer> topoOrder = topoSorter.sort(dag);

                long topoTime = metrics.getElapsedTimeNanos();

                System.out.printf("  [TOPO] Time: %.2f ms%n", topoTime / 1_000_000.0);


                metrics.reset();
                DAGShortestPath pathFinder = new DAGShortestPath(metrics);

                Map<Integer, Integer> shortestPaths = pathFinder.calculateShortestPaths(dag, topoOrder);

                Map<Integer, Integer> longestPaths = pathFinder.calculateLongestPaths(dag, topoOrder);

                long pathTime = metrics.getElapsedTimeNanos();

                System.out.printf("  [PATHS] Time: %.2f ms%n", pathTime / 1_000_000.0);


                System.out.println("\n  --- Metrics Summary ---");
                System.out.printf("  | SCC: DFS_VISITS: %d, EDGE_CHECKS: %d%n",
                        sccFinder.metrics.getCounter(Metrics.DFS_VISITS),
                        sccFinder.metrics.getCounter(Metrics.EDGE_CHECKS));
                System.out.printf("  | TOPO: KAHN_OPS (Push/Pop): %d%n",
                        topoSorter.metrics.getCounter(TopologicalSorter.KAHN_OPS));
                System.out.printf("  | PATHS: RELAXATIONS: %d%n",
                        pathFinder.metrics.getCounter(DAGShortestPath.DAG_RELAXATIONS));

            } catch (IOException e) {
                System.err.println("Error reading file: " + filePath + ". Check path and format.");
                e.printStackTrace();
            }
        }
    }


    private static TaskGraph loadGraphFromJson(String filePath) throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            TaskData data = gson.fromJson(reader, TaskData.class);
            return new TaskGraph(data.n, data.edges, data.source);
        }
    }
}