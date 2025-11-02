package org.example;

import java.util.*;


public class KosarajuSCC {

    public final Metrics metrics;

    public KosarajuSCC(Metrics metrics) {
        this.metrics = metrics;
    }


    public CondensationGraph findSCCs(TaskGraph graph) {
        metrics.startTimer();

        Stack<Integer> stack = new Stack<>();
        boolean[] visited = new boolean[graph.getN()];

        for (int i = 0; i < graph.getN(); i++) {
            if (!visited[i]) {
                dfsFirstPass(graph, i, visited, stack);
            }
        }

        TaskGraph transposedGraph = graph.getTransposedGraph();
        Arrays.fill(visited, false);

        List<List<Integer>> sccs = new ArrayList<>();

        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (!visited[u]) {
                List<Integer> currentScc = new ArrayList<>();
                dfsSecondPass(transposedGraph, u, visited, currentScc);
                sccs.add(currentScc);
            }
        }

        metrics.stopTimer();

        return buildCondensationGraph(graph, sccs);
    }



    private void dfsFirstPass(TaskGraph graph, int u, boolean[] visited, Stack<Integer> stack) {
        visited[u] = true;
        metrics.incrementCounter(Metrics.DFS_VISITS);

        for (Edge edge : graph.getAdjList().getOrDefault(u, Collections.emptyList())) {
            metrics.incrementCounter(Metrics.EDGE_CHECKS);
            if (!visited[edge.v()]) {
                dfsFirstPass(graph, edge.v(), visited, stack);
            }
        }
        stack.push(u);
    }


    private void dfsSecondPass(TaskGraph transposedGraph, int u, boolean[] visited, List<Integer> currentScc) {
        visited[u] = true;
        currentScc.add(u);
        metrics.incrementCounter(Metrics.DFS_VISITS);

        for (Edge edge : transposedGraph.getAdjList().getOrDefault(u, Collections.emptyList())) {
            metrics.incrementCounter(Metrics.EDGE_CHECKS);
            if (!visited[edge.v()]) {
                dfsSecondPass(transposedGraph, edge.v(), visited, currentScc);
            }
        }
    }


    private CondensationGraph buildCondensationGraph(TaskGraph originalGraph, List<List<Integer>> sccs) {
        int numComponents = sccs.size();

        int[] vertexToComponentId = new int[originalGraph.getN()];
        for (int i = 0; i < numComponents; i++) {
            for (int vertex : sccs.get(i)) {
                vertexToComponentId[vertex] = i;
            }
        }

        Map<Integer, List<Edge>> newAdjList = new HashMap<>();
        for (int i = 0; i < numComponents; i++) {
            newAdjList.put(i, new ArrayList<>());
        }

        for (int u = 0; u < originalGraph.getN(); u++) {
            int componentU = vertexToComponentId[u];

            for (Edge originalEdge : originalGraph.getAdjList().getOrDefault(u, Collections.emptyList())) {
                int v = originalEdge.v();
                int componentV = vertexToComponentId[v];

                if (componentU != componentV) {
                    Edge newEdge = new Edge(componentU, componentV, originalEdge.w());
                    if (!newAdjList.get(componentU).contains(newEdge)) {
                        newAdjList.get(componentU).add(newEdge);
                    }
                }
            }
        }

        return new CondensationGraph(sccs, newAdjList, originalGraph.getSourceNode());
    }
}