package com.observe.os1.v1.prometheus;

public enum PrometheusQueryType {
    QUERY("query"),
    QUERY_RANGE("query_range"),
    METRICS("metrics"),
    LABELS("labels"),
    LABEL_VALUES("label_values");

    PrometheusQueryType(String query) {

    }
}
