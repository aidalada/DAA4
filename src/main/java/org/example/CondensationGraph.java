package org.example;

import java.util.*;


public class CondensationGraph {
    private final List<List<Integer>> sccs;
    private final Map<Integer, List<Edge>> adjList;
    private final int numComponents;
    private final int originalSource;

    public CondensationGraph(List<List<Integer>> sccs, Map<Integer, List<Edge>> adjList, int originalSource) {
        this.sccs = sccs;
        this.adjList = adjList;
        this.numComponents = sccs.size();
        this.originalSource = originalSource;
    }


    public int getNumComponents() { return numComponents; }
    public Map<Integer, List<Edge>> getAdjList() { return adjList; }
    public List<List<Integer>> getSccs() { return sccs; }
    public int getOriginalSource() {
        return originalSource;
    }
}