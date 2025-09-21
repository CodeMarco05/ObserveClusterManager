package com.observe.os1;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class HealthCheckTest {

    @Test
    public void testHelloEndpoint() {
        given()
            .when().get("/health-check")
            .then()
            .statusCode(200)
            .body(containsString("Hello from Quarkus!"));
    }
}
