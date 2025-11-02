package org.example;

import java.util.*;


public class TaskGraph {
    private final int N;
    private final Map<Integer, List<Edge>> adjList; // Основной граф
    private final int sourceNode;

    public TaskGraph(int N, List<Edge> edges, int source) {
        this.N = N;
        this.sourceNode = source;
        this.adjList = new HashMap<>();

        for (int i = 0; i < N; i++) {
            adjList.put(i, new ArrayList<>());
        }

        for (Edge edge : edges) {
            if (edge.u() >= 0 && edge.u() < N) {
                adjList.get(edge.u()).add(edge);
            }
        }
    }


    public TaskGraph getTransposedGraph() {
        List<Edge> transposedEdges = new ArrayList<>();
        for (List<Edge> edges : adjList.values()) {
            for (Edge edge : edges) {
                transposedEdges.add(new Edge(edge.v(), edge.u(), edge.w()));
            }
        }
        return new TaskGraph(N, transposedEdges, sourceNode);
    }

    public int getN() { return N; }
    public Map<Integer, List<Edge>> getAdjList() { return adjList; }

    public int getSourceNode() { return sourceNode; }
}