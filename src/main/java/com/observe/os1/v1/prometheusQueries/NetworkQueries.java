package com.observe.os1.v1.prometheusQueries;

public enum NetworkQueries {
    NETWORK_IN_IN_BYTES_PER_SECOND("sum(rate(node_network_receive_bytes_total{device!~\"lo\"}[5s]))"),
    NETWORK_OUT_IN_BYTES_PER_SECOND("sum(rate(node_network_transmit_bytes_total{device!~\"lo\"}[5s]))");

    private final String query;

    NetworkQueries(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return query;
    }
}
