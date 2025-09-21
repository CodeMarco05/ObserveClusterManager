package com.observe.os1.v1.metrics;

import com.observe.os1.MockServerConfig;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
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
public class CpuResourceTest {
    @Inject
    MockServerConfig mockServerConfig;

    private ClientAndServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = ClientAndServer.startClientAndServer(mockServerConfig.port());
    }

    @AfterEach
    void tearDown() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    // SUCCESS TESTS
    @Test
    @DisplayName("Should fetch CPU usage in percentage -- SUCCESS")
    void testCpuUsageEndpoint() {
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
                          [1752966880, "25.5"],
                          [1752966885, "30.2"],
                          [1752966890, "28.8"]
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
                .when().get("/v1/metrics/cpu/usage-in-percent")
                .then()
                .statusCode(200)
                .body(containsString("25.5"))
                .body(containsString("30.2"))
                .body(containsString("28.8"));
    }


}
