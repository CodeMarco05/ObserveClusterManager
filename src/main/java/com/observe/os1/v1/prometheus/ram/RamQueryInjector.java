package com.observe.os1.v1.prometheus.ram;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.ext.Provider;

import java.net.URI;

@Provider
public class RamQueryInjector implements ClientRequestFilter {
    @Override
    public void filter(ClientRequestContext requestContext) {

        UriBuilder builder = UriBuilder.fromUri(requestContext.getUri());

        if (requestContext.getUri().toString().contains("prometheus-ram-client")) {
            String oldPath = requestContext.getUri().getPath();
            builder.replacePath(oldPath.replace("/api/v1/query_range", "/api/v1/query"));
        }

        URI uri = builder.build();
        requestContext.setUri(uri);

        System.out.println(requestContext);
    }
}
