package org.observe.rest.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.GET;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PrometheusResponse {
    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private Data data;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Data {
        @JsonProperty("resultType")
        private String resultType;

        @JsonProperty("result")
        private List<Result> result;
    }

    // Inner Result class
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Result {
        @JsonProperty("metric")
        private Map<String, String> metric;

        @JsonProperty("values")
        private List<List<String>> values;
    }

    public int getFirstTimestamp() {
        if (data != null && data.getResult() != null && !data.getResult().isEmpty()) {
            List<List<String>> values = data.getResult().getFirst().getValues();
            if (values != null && !values.isEmpty()) {
                return Integer.parseInt(values.getFirst().getFirst());
            }
        }
        return 0; // or throw an exception if preferred
    }
}