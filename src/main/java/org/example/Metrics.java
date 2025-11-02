package org.example;

public interface Metrics {
    void startTimer();
    void stopTimer();
    long getElapsedTimeNanos();
    void incrementCounter(String name);
    long getCounter(String name);

    String DFS_VISITS = "SCC_DFS_Visits";
    String EDGE_CHECKS = "SCC_Edge_Checks";
}