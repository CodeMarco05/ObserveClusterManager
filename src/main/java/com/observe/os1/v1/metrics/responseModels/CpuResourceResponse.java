package com.observe.os1.v1.metrics.responseModels;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Schema(description = "Response model for CPU resource metrics")
public class CpuResourceResponse {
    @Schema(description = "List of CPU usage metrics. First comes the unix timestamp, followed by the CPU usage value in percent.", examples = "                                    {\n" +
            "                                      \"metric\": {\n" +
            "                                        \"1752966880\": 0.0024999999999977263,\n" +
            "                                        \"1752966895\": 0.008749999999992042,\n" +
            "                                        \"1752966910\": 0.005000000000023874,\n" +
            "                                        \"1752966925\": 0.009999999999990905,\n" +
            "                                        \"1752966940\": 0.0037500000000108002\n" +
            "                                      }\n" +
            "                                    }")
    public Map<Long, Double> metric = new HashMap<>(); // first is the timestamp, second is the value of the cpu usage
}
