package org.example;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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

        System.out.println("--- Запуск Интеграционного Тестирования Алгоритмов ---");

        for (String filePath : dataFiles) {
            System.out.println("\n--------------------------------------------------");
            System.out.println("Обработка файла: " + filePath);

            try {
                TaskGraph graph = loadGraphFromJson(filePath);
                if (graph == null) continue;

                SimpleMetrics metrics = new SimpleMetrics();

                KosarajuSCC sccFinder = new KosarajuSCC(metrics);
                CondensationGraph dag = sccFinder.findSCCs(graph);

                long sccTime = metrics.getElapsedTimeNanos();

                System.out.printf("  [SCC] Components found: %d. Time: %.2f мс%n",
                        dag.getNumComponents(), sccTime / 1_000_000.0);

                TopologicalSorter topoSorter = new TopologicalSorter(metrics);
                List<Integer> topoOrder = topoSorter.sort(dag);

                long topoTime = metrics.getElapsedTimeNanos();

                System.out.printf("  [TOPO] Time: %.2f мс%n", topoTime / 1_000_000.0);

                DAGShortestPath pathFinder = new DAGShortestPath(metrics);

                Map<Integer, Integer> shortestPaths = pathFinder.calculateShortestPaths(dag, topoOrder);

                Map<Integer, Integer> longestPaths = pathFinder.calculateLongestPaths(dag, topoOrder);

                long pathTime = metrics.getElapsedTimeNanos();

                System.out.printf("  [PATHS] Time: %.2f мс%n", pathTime / 1_000_000.0);

                System.out.printf("  [PATHS] Shortest Paths (Source %d): %s%n", dag.getOriginalSource(), formatPathOutput(dag, shortestPaths));
                System.out.printf("  [PATHS] Longest Paths (Critical Path Map): %s%n", formatPathOutput(dag, longestPaths));
                int criticalPathLength = longestPaths.values().stream().max(Integer::compare).orElse(-1);
                System.out.printf("  [PATHS] Critical Path Length: %d%n", criticalPathLength);


                System.out.println("\n  --- Metrics Summary ---");
                System.out.printf("  | SCC: DFS_VISITS: %d, EDGE_CHECKS: %d%n",
                        sccFinder.metrics.getCounter(Metrics.DFS_VISITS),
                        sccFinder.metrics.getCounter(Metrics.EDGE_CHECKS));
                System.out.printf("  | TOPO: KAHN_OPS (Push/Pop): %d%n",
                        topoSorter.metrics.getCounter(Metrics.KAHN_OPS));
                System.out.printf("  | PATHS: RELAXATIONS: %d%n",
                        pathFinder.metrics.getCounter(Metrics.DAG_RELAXATIONS));

            } catch (IOException e) {
                System.err.println("Ошибка чтения файла: " + filePath + ". Проверьте путь и формат.");
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

    private static String formatPathOutput(CondensationGraph dag, Map<Integer, Integer> distances) {
        Map<Integer, Integer> originalDistances = new HashMap<>();
        for (int i = 0; i < dag.getSccs().size(); i++) {
            int componentDistance = distances.getOrDefault(i, Integer.MAX_VALUE / 2);
            for (int originalVertex : dag.getSccs().get(i)) {
                originalDistances.put(originalVertex, componentDistance);
            }
        }
        TreeMap<Integer, Integer> sortedOutput = new TreeMap<>(originalDistances);
        return sortedOutput.toString();
    }
}