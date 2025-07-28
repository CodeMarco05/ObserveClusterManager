package com.observe.os1.v1.metrics.responseModels;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Raw response model for CPU resource metrics from Prometheus")
public class CpuResourceRawResponse {

    @Schema(description = "Status of the response", examples = "success")
    public String status;

    @Schema(description = "Data container for the metrics")
    public Data data;

    @Schema(description = "Data container for CPU metrics")
    public static class Data {

        @Schema(description = "Type of result", examples = "matrix")
        public String resultType;

        @Schema(description = "List of metric results")
        public List<MetricResult> result;
    }

    @Schema(description = "Individual metric result containing metadata and values")
    public static class MetricResult {

        @Schema(
                description = "Metric metadata containing instance and mode information",
                examples = "{ \"instance\": \"host.docker.internal:9100\", \"mode\": \"user\" }"
        )
        public Map<String, String> metric;

        @Schema(
                description = "Array of timestamp-value pairs. Each inner array contains [timestamp, value] as strings",
                examples = "[[\"1752966880\", \"0.0024999999999977263\"], [\"1752966895\", \"0.007499999999993179\"]]"
        )
        public List<List<String>> values;
    }
}