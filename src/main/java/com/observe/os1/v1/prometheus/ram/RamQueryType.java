package com.observe.os1.v1.prometheus.ram;

public enum RamQueryType {
    RAM_USAGE_PERCENTAGE("ram_usage_percentage"),
    RAM_USAGE_GB("ram_usage_gb"),
    RAM_AVAILABLE_GB("ram_available_gb");

    private final String queryType;

    RamQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getQueryType() {
        return queryType;
    }

    @Override
    public String toString() {
        return queryType;
    }
}
