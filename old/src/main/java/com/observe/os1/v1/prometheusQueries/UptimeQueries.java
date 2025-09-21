package com.observe.os1.v1.prometheusQueries;

public enum UptimeQueries {
    TOTAL_UPTIME_IN_HOURS("(node_time_seconds - node_boot_time_seconds) / 3600"),
    TOTAL_UPTIME_IN_SECONDS("(node_time_seconds - node_boot_time_seconds)");

    private final String query;

    UptimeQueries(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return query;
    }
}
