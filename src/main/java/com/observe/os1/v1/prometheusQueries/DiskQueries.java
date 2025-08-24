package com.observe.os1.v1.prometheusQueries;

public enum DiskQueries {
    DISK_AVAILABLE_IN_GB("sum(node_filesystem_avail_bytes{fstype!~\"tmpfs|overlay\"}) / 1073741824"),
    DISK_USAGE_IN_GB("sum(node_filesystem_size_bytes{fstype!=\"tmpfs\"} - node_filesystem_avail_bytes{fstype!=\"tmpfs\"}) / 1024^3"),
    DISK_TOTAL_SIZE_IN_GB_ALL_SYSTEMS("sum(node_filesystem_size_bytes{fstype!~\"tmpfs|overlay\"}) / 1073741824"),
    DISK_TOTAL_SIZE_IN_GB("node_filesystem_size_bytes{mountpoint=\"/\", fstype!~\"tmpfs|overlay\"} / 1073741824");

    private final String query;

    DiskQueries(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return query;
    }
}
