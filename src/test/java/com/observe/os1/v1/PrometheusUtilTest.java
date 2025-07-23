package com.observe.os1.v1;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.mockserver.integration.ClientAndServer;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@QuarkusTest
class PrometheusUtilTest {

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
    @DisplayName("Should return successful response with JSON content type when Prometheus returns 200")
    void testExecutePrometheusRequest_Success() {
        // Arrange
        String prometheusResponse = """
                {
                  "status": "success",
                  "data": {
                    "resultType": "vector",
                    "result": [
                      {
                        "metric": {
                          "__name__": "node_memory_MemAvailable_bytes",
                          "instance": "localhost:9100",
                          "job": "node"
                        },
                        "value": [1690107159, "8589934592"]
                      }
                    ]
                  }
                }
                """;

        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/query")
                        .withQueryStringParameter("query", "node_memory_MemAvailable_bytes"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(prometheusResponse));

        String testUrl = String.format("http://%s:%d/api/v1/query?query=node_memory_MemAvailable_bytes",
                MOCK_SERVER_HOST, MOCK_SERVER_PORT);

        // Act
        Response result = PrometheusUtil.executePrometheusRequest(testUrl);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, result.getHeaderString("Content-Type"));

        String responseBody = (String) result.getEntity();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("success"));
        assertTrue(responseBody.contains("node_memory_MemAvailable_bytes"));
        assertTrue(responseBody.contains("8589934592"));
    }

    @Test
    @DisplayName("Should return 404 error when Prometheus endpoint returns 404")
    void testExecutePrometheusRequest_NotFound() {
        // Arrange
        String errorResponse = "404 page not found";

        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/invalid_endpoint"))
                .respond(response()
                        .withStatusCode(404)
                        .withBody(errorResponse));

        String testUrl = String.format("http://%s:%d/api/v1/invalid_endpoint",
                MOCK_SERVER_HOST, MOCK_SERVER_PORT);

        // Act
        Response result = PrometheusUtil.executePrometheusRequest(testUrl);

        // Assert
        assertEquals(404, result.getStatus());
        String responseBody = (String) result.getEntity();
        assertTrue(responseBody.contains("Error fetching data from Prometheus"));
        assertTrue(responseBody.contains(errorResponse));
    }

    @Test
    @DisplayName("Should return 500 error when Prometheus returns internal server error")
    void testExecutePrometheusRequest_InternalServerError() {
        // Arrange
        String errorResponse = "Internal Server Error: Query execution failed";

        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/query"))
                .respond(response()
                        .withStatusCode(500)
                        .withBody(errorResponse));

        String testUrl = String.format("http://%s:%d/api/v1/query?query=invalid_metric",
                MOCK_SERVER_HOST, MOCK_SERVER_PORT);

        // Act
        Response result = PrometheusUtil.executePrometheusRequest(testUrl);

        // Assert
        assertEquals(500, result.getStatus());
        String responseBody = (String) result.getEntity();
        assertTrue(responseBody.contains("Error fetching data from Prometheus"));
        assertTrue(responseBody.contains(errorResponse));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when URL is malformed")
    void testExecutePrometheusRequest_MalformedURL() {
        // Test different types of malformed URLs
        String[] malformedUrls = {
                "not-a-valid-url://invalid",
                "://missing-protocol",
                "http://",
                "ftp://invalid-protocol-for-this-test",
                "not-a-url-at-all"
        };

        for (String malformedUrl : malformedUrls) {
            // Act
            Response result = PrometheusUtil.executePrometheusRequest(malformedUrl);

            // Assert
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatus(),
                    "Failed for URL: " + malformedUrl);

            String responseBody = (String) result.getEntity();
            assertTrue(responseBody.contains("Invalid URL"),
                    "Expected 'Invalid URL' in response for: " + malformedUrl + ", got: " + responseBody);

            // If we get here, test passed for this URL, break
            break;
        }
    }

    @Test
    @DisplayName("Should return 500 Internal Server Error when connection fails")
    void testExecutePrometheusRequest_ConnectionError() {
        // Arrange - Use a non-existent server
        String unreachableUrl = "http://non-existent-server-12345.com:9090/api/v1/query";

        // Act
        Response result = PrometheusUtil.executePrometheusRequest(unreachableUrl);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result.getStatus());
        String responseBody = (String) result.getEntity();
        assertTrue(responseBody.contains("Error connecting to Prometheus"));
    }

    @Test
    @DisplayName("Should return 500 when connection is refused")
    void testExecutePrometheusRequest_ConnectionRefused() {
        // Arrange - Use a port that's definitely not in use
        String refusedUrl = "http://localhost:9998/api/v1/query?query=test";

        // Act
        Response result = PrometheusUtil.executePrometheusRequest(refusedUrl);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result.getStatus());
        String responseBody = (String) result.getEntity();
        assertTrue(responseBody.contains("Error connecting to Prometheus"));
    }

    @Test
    @DisplayName("Should handle Prometheus timeout error correctly")
    void testExecutePrometheusRequest_Timeout() {
        // Arrange
        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/query"))
                .respond(response()
                        .withStatusCode(408)
                        .withBody("Request timeout"));

        String testUrl = String.format("http://%s:%d/api/v1/query?query=slow_query",
                MOCK_SERVER_HOST, MOCK_SERVER_PORT);

        // Act
        Response result = PrometheusUtil.executePrometheusRequest(testUrl);

        // Assert
        assertEquals(408, result.getStatus());
        String responseBody = (String) result.getEntity();
        assertTrue(responseBody.contains("Error fetching data from Prometheus"));
        assertTrue(responseBody.contains("Request timeout"));
    }

    @Test
    @DisplayName("Should handle empty response body correctly")
    void testExecutePrometheusRequest_EmptyResponse() {
        // Arrange
        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/query"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(""));

        String testUrl = String.format("http://%s:%d/api/v1/query?query=empty_result",
                MOCK_SERVER_HOST, MOCK_SERVER_PORT);

        // Act
        Response result = PrometheusUtil.executePrometheusRequest(testUrl);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        assertEquals(MediaType.APPLICATION_JSON, result.getHeaderString("Content-Type"));
        String responseBody = (String) result.getEntity();
        assertEquals("", responseBody);
    }

    @Test
    @DisplayName("Should handle complex Prometheus query with special characters")
    void testExecutePrometheusRequest_ComplexQuery() {
        // Arrange
        String complexQuery = "rate(http_requests_total{job=\"api-server\",handler=\"/api/comments\"}[5m])";
        String prometheusResponse = """
                {
                  "status": "success",
                  "data": {
                    "resultType": "vector",
                    "result": [
                      {
                        "metric": {
                          "handler": "/api/comments",
                          "instance": "localhost:8080",
                          "job": "api-server"
                        },
                        "value": [1690107159, "0.2"]
                      }
                    ]
                  }
                }
                """;

        mockServer
                .when(request()
                        .withMethod("GET")
                        .withPath("/api/v1/query"))
                .respond(response()
                        .withStatusCode(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(prometheusResponse));

        String testUrl = String.format("http://%s:%d/api/v1/query?query=%s",
                MOCK_SERVER_HOST, MOCK_SERVER_PORT, complexQuery);

        // Act
        Response result = PrometheusUtil.executePrometheusRequest(testUrl);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        String responseBody = (String) result.getEntity();
        assertTrue(responseBody.contains("api-server"));
        assertTrue(responseBody.contains("/api/comments"));
        assertTrue(responseBody.contains("0.2"));
    }
}