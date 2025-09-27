package org.observe.controller;

import com.observe.openapi.model.CpuUsage;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class CpuControllerTest {

    @Inject
    CpuController cpuController;

    @Test
    void testGivesBackInfoAboutTheCurrentCpuUsage() {
        // When
        CpuUsage actualCpuUsage = cpuController.currentUsage();

        Log.info(actualCpuUsage);

        // Then
        assertNotNull(actualCpuUsage);
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