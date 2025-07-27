package com.observe.os1.v1.metrics.responseModels;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CpuResponse {
    public String status;
    public Data data;

    public static class Data {
        public String resultType;
        public List<Result> result;

        public static class Result {
            public Map<String, String> metric;
            public List<Value> values;

            @JsonDeserialize(using = ValueDeserializer.class)
            public static class Value {
                public long timestamp;
                public double value;

                public Value(long timestamp, double value) {
                    this.timestamp = timestamp;
                    this.value = value;
                }
            }

            public static class ValueDeserializer extends JsonDeserializer<Value> {
                @Override
                public Value deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                    JsonNode node = p.getCodec().readTree(p);
                    long timestamp = node.get(0).asLong();
                    double value = Double.parseDouble(node.get(1).asText());
                    return new Value(timestamp, value);
                }
            }
        }
    }
}