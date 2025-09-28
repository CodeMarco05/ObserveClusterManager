package org.observe.controller;

import com.observe.openapi.model.DiskStatistic;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class DiskControllerTest {
    @Inject
    DiskController diskController;

    @Test
    void testGetDiskMetrics(){
        List<DiskStatistic> statistic = diskController.diskStatistic(1759051296, 1759051395, 10);
        Log.info(statistic.toString());
    }

    @Test
    void testGetDiskMetricsCall(){
        given()
                .header("X-API-Key", "test-api-key")
                .queryParam("startTime", 1759052553)
                .queryParam("endTime", 1759053053)
                .queryParam("step", 10)
                .when()
                .get("/disk/disk-stat")
                .then()
                .statusCode(200)
                .log().all()
                .contentType(ContentType.JSON);
    }
}
