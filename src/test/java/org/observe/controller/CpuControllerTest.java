package org.observe.controller;

import com.observe.openapi.model.CpuUsage;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.observe.rest.client.PrometheusResponse;
import org.observe.rest.client.PrometheusRestClient;
import org.observe.service.CpuService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

@QuarkusTest
class CpuControllerTest {

    @Inject
    CpuController cpuController;

    CpuService cpuService;

    @BeforeEach
    void setUp() {
        // Mock CpuService approach
        setupCpuServiceMock();

        // Mock PrometheusRestClient approach
        setupPrometheusRestClientMock();
    }

    private void setupCpuServiceMock() {
        // Create mock CpuService
        cpuService = Mockito.mock(CpuService.class);

        // Create mock CpuUsage for current usage
        CpuUsage mockCurrentCpuUsage = new CpuUsage();
        mockCurrentCpuUsage.setUsageInPercent("25.5");
        mockCurrentCpuUsage.setUnixTime(1640995200);

        // Mock time series data
        List<CpuUsage> mockTimeSeries = new ArrayList<>();
        CpuUsage usage1 = new CpuUsage();
        usage1.setUsageInPercent("20.1");
        usage1.setUnixTime(1758912218);

        CpuUsage usage2 = new CpuUsage();
        usage2.setUsageInPercent("30.5");
        usage2.setUnixTime(1758912228);

        mockTimeSeries.add(usage1);
        mockTimeSeries.add(usage2);

        // Mock the service methods
        Mockito.when(cpuService.getCpuUsageNow()).thenReturn(mockCurrentCpuUsage);
        Mockito.when(cpuService.getCpuUsageOverTime(Mockito.any(Integer.class), Mockito.any(Integer.class), Mockito.any(Integer.class)))
               .thenReturn(mockTimeSeries);

        // Install the mock in Quarkus
        QuarkusMock.installMockForType(cpuService, CpuService.class);
    }

    private void setupPrometheusRestClientMock() {
        // Create mock PrometheusRestClient
        PrometheusRestClient prometheusRestClient = Mockito.mock(PrometheusRestClient.class);

        // Create mock PrometheusResponse
        PrometheusResponse mockResponse = createMockPrometheusResponse();

        // Mock the Response object
        Response mockHttpResponse = Mockito.mock(Response.class);
        Mockito.when(mockHttpResponse.readEntity(PrometheusResponse.class)).thenReturn(mockResponse);
        Mockito.when(mockHttpResponse.getStatus()).thenReturn(200);

        // Mock the REST client call
        Mockito.when(prometheusRestClient.universalQuery(anyString(), anyString(), anyString(), anyString()))
               .thenReturn(mockHttpResponse);

        // Install the mock in Quarkus (Note: This might not work for REST clients)
        // QuarkusMock.installMockForType(prometheusRestClient, PrometheusRestClient.class);
    }

    private PrometheusResponse createMockPrometheusResponse() {
        PrometheusResponse response = new PrometheusResponse();
        response.setStatus("success");

        PrometheusResponse.Data data = new PrometheusResponse.Data();
        data.setResultType("matrix");

        PrometheusResponse.Result result = new PrometheusResponse.Result();
        result.setMetric(Map.of("instance", "localhost:9100", "job", "node-exporter"));
        result.setValues(List.of(
            List.of("1640995200", "15.5"),  // timestamp, cpu usage %
            List.of("1640995260", "20.2")
        ));

        data.setResult(List.of(result));
        response.setData(data);

        return response;
    }

    @Test
    void testGivesBackInfoAboutTheCurrentCpuUsageWithMockedData() {
        // When
        CpuUsage actualCpuUsage = cpuController.currentUsage();

        Log.info(actualCpuUsage);

        // Then
        assertNotNull(actualCpuUsage);
        assertNotNull(actualCpuUsage.getUsageInPercent());
        assertEquals("25.5", actualCpuUsage.getUsageInPercent());
        assertEquals(1640995200, actualCpuUsage.getUnixTime());
    }

    @Test
    void testGivesBackATimeSeriesOfCpuUsage() {
        // When
        List<CpuUsage> actualCpuUsage = cpuController.usageOverTime(1758912218, 1758912799, 10);

        Log.info(actualCpuUsage);

        // Then
        assertNotNull(actualCpuUsage);
    }

    @Test
    void testReturns400WhenStartTimeIsNull() {
        given()
                .header("X-API-KEY", "test-api-key")
            .queryParam("endTime", 1758912799)
            .queryParam("step", 10)
        .when()
            .get("/cpu/usage-in-percent-over-time")
        .then()
                .log().all()
            .statusCode(400);
    }

}