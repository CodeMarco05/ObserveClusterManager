package com.observe.os1.v1.prometheusQueries;

public enum DiskQueries {
    DISK_USAGE_IN_GB("(1 - (node_filesystem_avail_bytes / node_filesystem_size_bytes)) * 100");

    private final String query;

    DiskQueries(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return query;
    }
}
