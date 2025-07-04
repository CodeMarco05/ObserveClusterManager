package com.observe.os1.v1.newConnection;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.matchesRegex;

@QuarkusTest
public class NewConnectionTest {

    // TODO adopt the tests so they use an admin API key
    @Test
    public void testSuccessfulRequestFromLocalhost() {
        RestAssured.given()
                .header("X-Forwarded-For", "127.0.0.1")
                .when()
                .post("/v1/new-connection")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body(matchesRegex("^[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}-[a-zA-Z0-9]{4}$"));
    }

    @Test
    public void testRequestFromUnknownSourceIsForbidden() {
        RestAssured.given()
                .header("X-Forwarded-For", "8.8.8.8")
                .when()
                .post("/v1/new-connection")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode())
                .body(Matchers.equalTo("Access denied. Just for local access."));
    }

}
