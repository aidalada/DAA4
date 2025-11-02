package org.example;

import java.util.*;

public class TopologicalSorter {

    public static final String KAHN_OPS = "Kahn_Pops_Pushes";

    public final Metrics metrics;

    public TopologicalSorter(Metrics metrics) {
        this.metrics = metrics;
    }

    public List<Integer> sort(CondensationGraph dag) {
        metrics.startTimer();
        int V = dag.getNumComponents();
        Map<Integer, Integer> inDegree = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < V; i++) {
            inDegree.put(i, 0);
        }

        for (List<Edge> edges : dag.getAdjList().values()) {
            for (Edge edge : edges) {
                inDegree.put(edge.v(), inDegree.getOrDefault(edge.v(), 0) + 1);
            }
        }

        for (int i = 0; i < V; i++) {
            if (inDegree.getOrDefault(i, 0) == 0) {
                queue.add(i);
                metrics.incrementCounter(KAHN_OPS);
            }
        }

        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.incrementCounter(KAHN_OPS);
            result.add(u);

            for (Edge edge : dag.getAdjList().getOrDefault(u, Collections.emptyList())) {
                int v = edge.v();
                int newInDegree = inDegree.get(v) - 1;
                inDegree.put(v, newInDegree);

                if (newInDegree == 0) {
                    queue.add(v);
                    metrics.incrementCounter(KAHN_OPS);
                }
            }
        }

        metrics.stopTimer();
        return result;
    }
}