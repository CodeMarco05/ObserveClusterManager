package org.observe.rest.client;

import lombok.Getter;

@Getter
public enum Queries {
    // Queries for CPU usage
    CPU_USAGE("avg by (instance,mode) (rate(node_cpu_seconds_total{mode!='idle'}[10s]))"),

    // Queries for RAM usage
    TOTAL_RAM_IN_GB("node_memory_MemTotal_bytes / 1024 / 1024 / 1024"),
    RAM_USAGE_IN_GB("(node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes) / 1024 / 1024 / 1024"),

    // Queries for Network usage
    NETWORK_IN_IN_BYTES_PER_SECOND("sum(rate(node_network_receive_bytes_total{device!~\"lo\"}[10s]))"),
    NETWORK_OUT_IN_BYTES_PER_SECOND("sum(rate(node_network_transmit_bytes_total{device!~\"lo\"}[10s]))"),

    // general queries
    UPTIME_IN_SECONDS("(node_time_seconds - node_boot_time_seconds)"),
    UP_QUERY("up");

    private final String query;

    Queries(String query) {
        this.query = query;
    }
}
