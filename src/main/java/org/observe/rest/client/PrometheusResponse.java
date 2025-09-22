package org.observe.rest.client;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Main response wrapper
public class PrometheusResponse {
    public String status;
    public Data data;
    
    public static class Data {
        public String resultType;
        public List<MetricResult> result;
    }
    
    public static class MetricResult {
        public Map<String, String> metric;
        public List<List<Object>> values; // [timestamp, value] pairs
        
        // Helper methods to work with values more easily
        public List<MetricValue> getMetricValues() {
            return values.stream()
                    .map(valueArray -> new MetricValue(
                        Long.parseLong(valueArray.get(0).toString()),
                        valueArray.get(1).toString()
                    ))
                    .collect(Collectors.toList());
        }
    }
    
    public static class MetricValue {
        public final long timestamp;
        public final String value;
        
        public MetricValue(long timestamp, String value) {
            this.timestamp = timestamp;
            this.value = value;
        }
        
        public double getValueAsDouble() {
            return Double.parseDouble(value);
        }
        
        public Instant getTimestampAsInstant() {
            return Instant.ofEpochSecond(timestamp);
        }
    }
}