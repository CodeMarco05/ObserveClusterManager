package com.observe.os1.v1.newconnection;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class NewConnectionTest {

    @Test
    public void testSuccessfulRequestFromLocalhost() {
        RestAssured.given()
                .header("X-Forwarded-For", "127.0.0.1")  // simulate local request
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/v1/new-connection")
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("temporaryPassword", Matchers.notNullValue());
    }

    @Test
    public void testRequestFromUnknownSourceIsForbidden() {
        RestAssured.given()
                .header("X-Forwarded-For", "8.8.8.8")  // simulate external IP
                .contentType(ContentType.JSON)
                .body("{}")
                .when()
                .post("/v1/new-connection")
                .then()
                .statusCode(Response.Status.FORBIDDEN.getStatusCode())
                .body(Matchers.equalTo("Zugriff verweigert: Nur lokale Verbindungen sind erlaubt."));
    }

}
