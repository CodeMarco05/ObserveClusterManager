package org.observe.controller;

import com.observe.openapi.model.Network;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class NetworkControllerTest {

    @Inject
    NetworkController networkController;

    @Test
    void testNetworkIn() {
        List<Network> networkList = networkController.networkIn(1758912218, 1758912799, 10);

        Log.info(networkList);
    }

    @Test
    void testNetworkOut() {
        List<Network> networkList = networkController.networkOut(1758912218, 1758912799, 10);

        Log.info(networkList);
    }

    @Test
    void testNetworkInEndpointWithoutApiKey() {
        given()
            .queryParam("startTime", 1758912218)
            .queryParam("endTime", 1758912799)
            .queryParam("step", 10)
        .when()
            .get("/network/in")
        .then()
            .statusCode(401);
    }

    @Test
    void testNetworkInEndpointWithApiKey() {
        given()
            .header("X-API-Key", "test-api-key")
            .queryParam("startTime", 1758912218)
            .queryParam("endTime", 1758912799)
            .queryParam("step", 10)
        .when()
            .get("/network/in")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON);
    }

    @Test
    void testNetworkInEndpointWithWrongApiKey() {
        given()
            .header("X-API-Key", "wrong-key")
            .queryParam("startTime", 1758912218)
            .queryParam("endTime", 1758912799)
            .queryParam("step", 10)
        .when()
            .get("/network/in")
        .then()
            .statusCode(401);
    }

    @Test
    void testNetworkOutEndpointWithoutApiKey() {
        given()
            .queryParam("startTime", 1758912218)
            .queryParam("endTime", 1758912799)
            .queryParam("step", 10)
        .when()
            .get("/network/out")
        .then()
            .statusCode(401)
            .contentType(ContentType.JSON);
    }
}
