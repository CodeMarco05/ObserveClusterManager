package org.observe.auth;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;

@ApiKeySecured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class ApiKeySecuredFilter implements ContainerRequestFilter {

    @ConfigProperty(name = "app.api.key")
    String expectedApiKey;

    private static final String API_KEY_HEADER = "X-API-Key";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // if the api key is null in the application throw an exception
        if (expectedApiKey == null || expectedApiKey.isEmpty()) {
            requestContext.abortWith(
                    Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("API key is not configured on the server")
                            .build()
            );
            return;
        }

        String apiKey = requestContext.getHeaderString(API_KEY_HEADER);

        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Invalid or missing API key")
                            .build()
            );
        }
    }
}
