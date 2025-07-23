package com.observe.os1.v1.metrics;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@QuarkusTest
class RamResourceTest {

    private ClientAndServer mockServer;
    private static final int MOCK_SERVER_PORT = 9999;
    private static final String MOCK_SERVER_HOST = "localhost";

    @BeforeEach
    void setUp() {
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT);
    }

    @AfterEach
    void tearDown() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    @Test
    @DisplayName("Should fetch RAM usage in percentage used -- SUCCESS")
    void testRamUsageEndpoint() {
        // Mock Prometheus response for RAM metrics
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
                          [
                            1752966880,
                            "15.75313592457942"
                          ],
                          [
                            1752966885,
                            "15.926182984230486"
                          ],
                          [
                            1752966890,
                            "15.850185909994874"
                          ]
                        ]
                      }
                    ]
                  }
                }
                """;

        final String startTime = "1752966880";
        final String endTime = "1752966890";
        final String interval = "5";

        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/metrics/ram/usage-in-percent")
                        .withQueryStringParameter("startTime", startTime)
                        .withQueryStringParameter("endTime", endTime)
                        .withQueryStringParameter("interval", interval))
                .respond(response()
                        .withStatusCode(200)
                        .withBody(prometheusResponse));

        // Test the RAM usage endpoint
        given()
                .when().get("/v1/metrics/ram/usage-in-percent?startTime=" + startTime + "&endTime=" + endTime + "&interval=" + interval)
                .then()
                .statusCode(200)
                .body(containsString("15.75313592457942"))
                .body(containsString("15.926182984230486"))
                .body(containsString("15.850185909994874"));

    }

    @Test
    @DisplayName("Used Memory in GB -- SUCCESS")
    void testRamUsageInGBEndpoint() {
        // Mock Prometheus response for RAM usage in GB
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
                          [
                            1752966880,
                            "8.5"
                          ],
                          [
                            1752966885,
                            "8.7"
                          ],
                          [
                            1752966890,
                            "8.6"
                          ]
                        ]
                      }
                    ]
                  }
                }
                """;

        final String startTime = "1752966880";
        final String endTime = "1752966890";
        final String interval = "5";

        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/v1/metrics/ram/usage-in-gb")
                        .withQueryStringParameter("startTime", startTime)
                        .withQueryStringParameter("endTime", endTime)
                        .withQueryStringParameter("interval", interval))
                .respond(response()
                        .withStatusCode(200)
                        .withBody(prometheusResponse));

        // Test the RAM usage in GB endpoint
        given()
                .when().get("/v1/metrics/ram/usage-in-gb?startTime=" + startTime + "&endTime=" + endTime + "&interval=" + interval)
                .then()
                .statusCode(200)
                .body(containsString("8.5"))
                .body(containsString("8.7"))
                .body(containsString("8.6"));
    }


    @Test
    @DisplayName("Should return 400 when startTime >= endTime -- FAILURE")
    void testRamUsageEndpoint_InvalidTimeRange() {
        given()
                .queryParam("startTime", 1752966890)
                .queryParam("endTime", 1752966880) // endTime before startTime
                .queryParam("interval", 5)
                .when().get("/v1/metrics/ram/usage-in-percent")
                .then()
                .statusCode(400)
                .body(containsString("startTime must be less than endTime"));
    }

}