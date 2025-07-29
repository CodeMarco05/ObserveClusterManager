package com.observe.os1;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/health-check")
public class HealthCheck {

    @ConfigProperty(name = "quarkus.application.version")
    String appVersion;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @APIResponse(
            responseCode = "200",
            description = "Health check response",
            content = @org.eclipse.microprofile.openapi.annotations.media.Content(
                    mediaType = MediaType.TEXT_PLAIN
            )
    )
    public String hello() {
        return "Hello from Quarkus!\n" +
                "Application version: " + appVersion;
    }
}
