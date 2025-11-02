package org.example;

import java.util.HashMap;
import java.util.Map;

public class SimpleMetrics implements Metrics {
    private long startTime;
    private long endTime;
    private final Map<String, Long> counters = new HashMap<>();

    @Override
    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    @Override
    public void stopTimer() {
        this.endTime = System.nanoTime();
    }

    @Override
    public long getElapsedTimeNanos() {
        return endTime - startTime;
    }

    @Override
    public void incrementCounter(String name) {
        counters.put(name, counters.getOrDefault(name, 0L) + 1);
    }

    @Override
    public long getCounter(String name) {
        return counters.getOrDefault(name, 0L);
    }

    public Map<String, Long> getAllCounters() {
        return counters;
    }

    public void reset() {
        counters.clear();
        startTime = 0;
        endTime = 0;
    }
}