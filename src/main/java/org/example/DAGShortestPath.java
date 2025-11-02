package org.example;

import java.util.*;


public class DAGShortestPath {

    public final Metrics metrics;

    private static final int INFINITY = Integer.MAX_VALUE / 2;

    public DAGShortestPath(Metrics metrics) {
        this.metrics = metrics;
    }


    public Map<Integer, Integer> calculateShortestPaths(CondensationGraph dag, List<Integer> topoOrder) {
        metrics.startTimer();

        Map<Integer, Integer> distances = initializeDistances(dag, false); // Инициализация
        int sourceComponent = getSourceComponentId(dag);

        if (distances.containsKey(sourceComponent)) {
            distances.put(sourceComponent, 0);
        }

        for (int u : topoOrder) {
            int distU = distances.getOrDefault(u, INFINITY);
            if (distU != INFINITY) {
                for (Edge edge : dag.getAdjList().getOrDefault(u, Collections.emptyList())) {
                    int v = edge.v();
                    int weight = edge.w();

                    if (distU + weight < distances.getOrDefault(v, INFINITY)) {
                        distances.put(v, distU + weight);
                        metrics.incrementCounter(Metrics.DAG_RELAXATIONS); // МЕТРИКА: релаксация
                    }
                }
            }
        }

        metrics.stopTimer();
        return distances;
    }


    public Map<Integer, Integer> calculateLongestPaths(CondensationGraph dag, List<Integer> topoOrder) {
        metrics.startTimer();


        Map<Integer, Integer> negativeDistances = initializeDistances(dag, false);
        int sourceComponent = getSourceComponentId(dag);

        if (negativeDistances.containsKey(sourceComponent)) {
            negativeDistances.put(sourceComponent, 0);
        }

        for (int u : topoOrder) {
            int distU = negativeDistances.getOrDefault(u, INFINITY);
            if (distU != INFINITY) {
                for (Edge edge : dag.getAdjList().getOrDefault(u, Collections.emptyList())) {
                    int v = edge.v();
                    int negativeWeight = -edge.w();


                    if (distU + negativeWeight < negativeDistances.getOrDefault(v, INFINITY)) {
                        negativeDistances.put(v, distU + negativeWeight);
                        metrics.incrementCounter(Metrics.DAG_RELAXATIONS);
                    }
                }
            }
        }

        Map<Integer, Integer> longestPaths = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : negativeDistances.entrySet()) {
            if (entry.getValue() != INFINITY) {
                longestPaths.put(entry.getKey(), -entry.getValue());
            } else {
                longestPaths.put(entry.getKey(), -1);
            }
        }

        metrics.stopTimer();
        return longestPaths;
    }



    private Map<Integer, Integer> initializeDistances(CondensationGraph dag, boolean useNegativeInfinity) {
        Map<Integer, Integer> distances = new HashMap<>();
        for (int i = 0; i < dag.getNumComponents(); i++) {
            distances.put(i, INFINITY);
        }
        return distances;
    }

    private int getSourceComponentId(CondensationGraph dag) {
        int sourceNode = dag.getOriginalSource();
        for (int i = 0; i < dag.getSccs().size(); i++) {
            if (dag.getSccs().get(i).contains(sourceNode)) {
                return i;
            }
        }
        return -1;
    }
}