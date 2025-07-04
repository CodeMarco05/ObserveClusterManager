package com.observe.os1.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import io.vertx.core.http.HttpServerRequest;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;

@Provider
@LocalOnly
@Priority(Priorities.AUTHENTICATION) // Make sure it runs early
public class LocalhostOnlyFilter implements ContainerRequestFilter {

    @Context
    HttpServerRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String ip = request.remoteAddress().host();

        if (!isLocalhost(ip)) {
            requestContext.abortWith(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity("Access denied: localhost only").build()
            );
        }
    }

    private boolean isLocalhost(String ip) {
        return ip.equals("127.0.0.1") || ip.equals("::1") || ip.equals("localhost");
    }
}
