package com.observe.os1.v1.metrics;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;


import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@QuarkusTest
class RamResourceTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 9999;

    @BeforeEach
    void setUp() {
        printAllEnvironmentVariables();
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT);
        // Configure your app to use mock server for Prometheus
        System.setProperty("observe.prometheus.base-url", "http://localhost:" + MOCK_SERVER_PORT);
    }

    @AfterEach
    void tearDown() {
        if (mockServer != null) {
            mockServer.stop();
        }
        System.clearProperty("observe.prometheus.base-url");
    }

    private void printAllEnvironmentVariables() {
        System.out.println("=== Environment Variables ===");
        System.getenv().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry ->
                        System.out.println(entry.getKey() + " = " + entry.getValue())
                );
    }

    // SUCCESS TESTS
    @Test
    @DisplayName("Should fetch RAM usage in percentage used -- SUCCESS")
    void testRamUsageEndpoint() {
        String prometheusResponse = """
                {
                  "status": "success",
                  "data": {
                    "resultType": "matrix",
                    "result": [
                      {
                        "metric": {
                          "instance": "host.docker.internal:9100",
                          "job": "node-exporter"
                        },
                        "values": [
                          [1752966880, "15.75313592457942"],
                          [1752966885, "15.926182984230486"],
                          [1752966890, "15.850185909994874"]
                        ]
                      }
                    ]
                  }
                }
                """;

        // Mock the Prometheus endpoint (not your REST endpoint)
        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/metrics/ram/usage-in-percent")
                        .withQueryStringParameter("start", "1752966880")
                        .withQueryStringParameter("end", "1752966890")
                        .withQueryStringParameter("step", "5s"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(prometheusResponse));

        // Test your actual REST endpoint
        given()
                .queryParam("startTime", 1752966880)
                .queryParam("endTime", 1752966890)
                .queryParam("interval", 5)
                .when().get("/v1/metrics/ram/usage-in-percent")
                .then()
                .statusCode(200)
                .body(containsString("15.75313592457942"))
                .body(containsString("15.926182984230486"))
                .body(containsString("15.850185909994874"));
    }

    @Test
    @DisplayName("Free Memory in GB -- SUCCESS")
    void testFreeMemoryEndpoint() {
        String prometheusResponse = """
                {
                  "status": "success",
                  "data": {
                    "resultType": "matrix",
                    "result": [
                      {
                        "metric": {
                          "instance": "host.docker.internal:9100",
                          "job": "node-exporter"
                        },
                        "values": [
                          [1752966880, "8.5"],
                          [1752966885, "8.7"],
                          [1752966890, "8.6"]
                        ]
                      }
                    ]
                  }
                }
                """;

        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/query_range")
                        .withQueryStringParameter("start", "1752966880")
                        .withQueryStringParameter("end", "1752966890")
                        .withQueryStringParameter("step", "5s")
                        .withQueryStringParameter("query", "node_memory_MemAvailable_bytes / 1024 / 1024 / 1024"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(prometheusResponse));

        given()
                .queryParam("startTime", 1752966880)
                .queryParam("endTime", 1752966890)
                .queryParam("interval", 5)
                .when().get("/v1/metrics/ram/free-memory-in-gb")
                .then()
                .statusCode(200)
                .body(containsString("8.5"))
                .body(containsString("8.7"))
                .body(containsString("8.6"));
    }

    @Test
    @DisplayName("Used Memory in GB -- SUCCESS")
    void testUsedMemoryInGBEndpoint() {
        String prometheusResponse = """
                {
                  "status": "success",
                  "data": {
                    "resultType": "matrix",
                    "result": [
                      {
                        "metric": {
                          "instance": "host.docker.internal:9100",
                          "job": "node-exporter"
                        },
                        "values": [
                          [1752966880, "7.5"],
                          [1752966885, "7.3"],
                          [1752966890, "7.4"]
                        ]
                      }
                    ]
                  }
                }
                """;

        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/query_range")
                        .withQueryStringParameter("start", "1752966880")
                        .withQueryStringParameter("end", "1752966890")
                        .withQueryStringParameter("step", "5s"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(prometheusResponse));

        given()
                .queryParam("startTime", 1752966880)
                .queryParam("endTime", 1752966890)
                .queryParam("interval", 5)
                .when().get("/v1/metrics/ram/used-memory-in-gb")
                .then()
                .statusCode(200)
                .body(containsString("7.5"))
                .body(containsString("7.3"))
                .body(containsString("7.4"));
    }

    // FAILURE TESTS
    @Test
    @DisplayName("Should return 400 when missing startTime parameter -- FAILURE")
    void testMissingStartTime() {
        given()
                .queryParam("endTime", 1752966890)
                .queryParam("interval", 5)
                .when().get("/v1/metrics/ram/usage-in-percent")
                .then()
                .statusCode(400)
                .body(containsString("Missing required query parameters"));
    }

    @Test
    @DisplayName("Should return 400 when missing endTime parameter -- FAILURE")
    void testMissingEndTime() {
        given()
                .queryParam("startTime", 1752966880)
                .queryParam("interval", 5)
                .when().get("/v1/metrics/ram/usage-in-percent")
                .then()
                .statusCode(400)
                .body(containsString("Missing required query parameters"));
    }

    @Test
    @DisplayName("Should return 400 when missing interval parameter -- FAILURE")
    void testMissingInterval() {
        given()
                .queryParam("startTime", 1752966880)
                .queryParam("endTime", 1752966890)
                .when().get("/v1/metrics/ram/usage-in-percent")
                .then()
                .statusCode(400)
                .body(containsString("Missing required query parameters"));
    }

    @Test
    @DisplayName("Should return 400 when startTime >= endTime -- FAILURE")
    void testRamUsageEndpoint_InvalidTimeRange() {
        given()
                .queryParam("startTime", 1752966890)
                .queryParam("endTime", 1752966880)
                .queryParam("interval", 5)
                .when().get("/v1/metrics/ram/usage-in-percent")
                .then()
                .statusCode(400)
                .body(containsString("startTime must be less than endTime"));
    }

    @Test
    @DisplayName("Should return 400 when startTime equals endTime -- FAILURE")
    void testEqualTimeRange() {
        given()
                .queryParam("startTime", 1752966880)
                .queryParam("endTime", 1752966880)
                .queryParam("interval", 5)
                .when().get("/v1/metrics/ram/usage-in-percent")
                .then()
                .statusCode(400)
                .body(containsString("startTime must be less than endTime"));
    }

    @Test
    @DisplayName("Should handle Prometheus server error -- FAILURE")
    void testPrometheusError() {
        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/query_range"))
                .respond(response()
                        .withStatusCode(500)
                        .withBody("Internal Server Error"));

        given()
                .queryParam("startTime", 1752966880)
                .queryParam("endTime", 1752966890)
                .queryParam("interval", 5)
                .when().get("/v1/metrics/ram/usage-in-percent")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("Should handle Prometheus timeout -- FAILURE")
    void testPrometheusTimeout() {
        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/query_range"))
                .respond(response()
                        .withDelay(java.util.concurrent.TimeUnit.SECONDS, 10)
                        .withStatusCode(200));

        given()
                .queryParam("startTime", 1752966880)
                .queryParam("endTime", 1752966890)
                .queryParam("interval", 5)
                .when().get("/v1/metrics/ram/usage-in-percent")
                .then()
                .statusCode(500);
    }

    @Test
    @DisplayName("Should handle empty Prometheus result -- SUCCESS")
    void testEmptyPrometheusResult() {
        String emptyResponse = """
                {
                  "status": "success",
                  "data": {
                    "resultType": "matrix",
                    "result": []
                  }
                }
                """;

        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/query_range"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(emptyResponse));

        given()
                .queryParam("startTime", 1752966880)
                .queryParam("endTime", 1752966890)
                .queryParam("interval", 5)
                .when().get("/v1/metrics/ram/usage-in-percent")
                .then()
                .statusCode(200)
                .body(containsString("success"))
                .body(containsString("[]"));
    }

    @Test
    @DisplayName("Should verify correct Prometheus query construction -- SUCCESS")
    void testPrometheusQueryConstruction() {
        String prometheusResponse = """
                {
                  "status": "success",
                  "data": {
                    "resultType": "matrix",
                    "result": []
                  }
                }
                """;

        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/query_range")
                        .withQueryStringParameter("start", "1752966880")
                        .withQueryStringParameter("end", "1752966890")
                        .withQueryStringParameter("step", "15s"), exactly(1))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(prometheusResponse));

        given()
                .queryParam("startTime", 1752966880)
                .queryParam("endTime", 1752966890)
                .queryParam("interval", 15)
                .when().get("/v1/metrics/ram/usage-in-percent")
                .then()
                .statusCode(200);

        // Verify the request was made with correct parameters
        mockServer.verify(request()
                .withPath("/api/v1/query_range")
                .withQueryStringParameter("start", "1752966880")
                .withQueryStringParameter("end", "1752966890")
                .withQueryStringParameter("step", "15s"));
    }
}