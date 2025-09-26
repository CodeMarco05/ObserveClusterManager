package org.observe.rest.client;

import lombok.Getter;

@Getter
public enum Queries {
    CPU_USAGE("avg by (instance,mode) (rate(node_cpu_seconds_total{mode!='idle'}[10s]))"),
    TOTAL_RAM_IN_GB("node_memory_MemTotal_bytes / 1024 / 1024 / 1024"),
    RAM_USAGE_IN_GB("(node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes) / 1024 / 1024 / 1024");

    private final String query;

    Queries(String query) {
        this.query = query;
    }
}
