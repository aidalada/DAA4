package org.example;

import java.util.*;


public class DAGShortestPath {

    public static final String DAG_RELAXATIONS = "DAG_Relaxations";

    public final Metrics metrics;

    private static final int INFINITY = Integer.MAX_VALUE / 2;

    public DAGShortestPath(Metrics metrics) {
        this.metrics = metrics;
    }


    public Map<Integer, Integer> calculateShortestPaths(CondensationGraph dag, List<Integer> topoOrder) {
        metrics.startTimer();

        Map<Integer, Integer> distances = initializeDistances(dag, false);
        int sourceComponent = getSourceComponentId(dag);

        if (distances.containsKey(sourceComponent)) {
            distances.put(sourceComponent, 0);
        }

        for (int u : topoOrder) {
            int distU = distances.getOrDefault(u, INFINITY);
            if (distU != INFINITY) {
                for (Edge edge : dag.getAdjList().getOrDefault(u, Collections.emptyList())) {
                    metrics.incrementCounter(DAG_RELAXATIONS);
                    int v = edge.v();
                    int weight = edge.w();

                    if (distances.get(v) > distU + weight) {
                        distances.put(v, distU + weight);
                    }
                }
            }
        }

        metrics.stopTimer();
        return distances;
    }


    public Map<Integer, Integer> calculateLongestPaths(CondensationGraph dag, List<Integer> topoOrder) {
        Map<Integer, List<Edge>> invertedAdjList = new HashMap<>();
        for (int u = 0; u < dag.getNumComponents(); u++) {
            invertedAdjList.put(u, new ArrayList<>());
            for (Edge edge : dag.getAdjList().getOrDefault(u, Collections.emptyList())) {
                invertedAdjList.get(u).add(new Edge(edge.u(), edge.v(), -edge.w()));
            }
        }


        CondensationGraph invertedDag = new CondensationGraph(dag.getSccs(), invertedAdjList, dag.getOriginalSource());

        Map<Integer, Integer> negativeDistances = initializeDistances(invertedDag, false);
        int sourceComponent = getSourceComponentId(dag);
        if (negativeDistances.containsKey(sourceComponent)) {
            negativeDistances.put(sourceComponent, 0);
        }

        for (int u : topoOrder) {
            int distU = negativeDistances.getOrDefault(u, INFINITY);
            if (distU != INFINITY) {
                for (Edge edge : invertedDag.getAdjList().getOrDefault(u, Collections.emptyList())) {
                    metrics.incrementCounter(DAG_RELAXATIONS); // Метрика сохраняется
                    int v = edge.v();
                    int weight = edge.w();

                    if (negativeDistances.get(v) > distU + weight) {
                        negativeDistances.put(v, distU + weight);
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
        int componentId = -1;
        for (int i = 0; i < dag.getSccs().size(); i++) {
            if (dag.getSccs().get(i).contains(sourceNode)) {
                componentId = i;
                break;
            }
        }
        return componentId;
    }
}