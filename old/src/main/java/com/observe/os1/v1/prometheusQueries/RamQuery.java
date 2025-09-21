package com.observe.os1.v1.prometheusQueries;

public enum RamQuery {
    MEMORY_USAGE_PERCENTAGE("((node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes) / node_memory_MemTotal_bytes) * 100"),
    MEMORY_USAGE_GB("(node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes) / 1024 / 1024 / 1024"),
    MEMORY_AVAILABLE_GB("node_memory_MemAvailable_bytes / 1024 / 1024 / 1024"),
    MEMORY_TOTAL_GB("node_memory_MemTotal_bytes / 1024 / 1024 / 1024");

    private final String query;

    RamQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return query;
    }
}
