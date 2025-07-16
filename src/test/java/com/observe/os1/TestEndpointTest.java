package com.observe.os1;

import com.observe.os1.auth.ClientService;
import com.observe.os1.models.ClientModel;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class TestEndpointTest {

    @Inject
    ClientService clientService;

    @Inject
    JwtTokenGenerator jwtTokenGenerator;

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/test")
                .then()
                .statusCode(200)
                .body(is("Hello from Quarkus!"));
    }

    @Test
    public void testProtectedEndpoint() {
        given()
                .header("Authorization", "Bearer " + getTestToken())
                .when().get("/test")
                .then()
                .statusCode(200);
    }

    private String getTestToken() {
        ClientModel client = clientService.registerClient("testUser", "testPassword", "user");
        if (client == null) {
            throw new RuntimeException("Failed to register test client");
        }

        return jwtTokenGenerator.generateToken(client);
    }
}
