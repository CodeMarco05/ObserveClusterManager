package com.observe.os1.v1.prometheus.ram;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "prometheus-ram-client")
@Path("/api/v1/query_range")
@RegisterProvider(RamQueryInjector.class)
public interface PrometheusRamRestClient {

    @GET
    Response getRamUsageAsPercentage(
        @QueryParam("request-type") String query, //this gets deleted in the query injector it is there to see what gets called
        @QueryParam("start") String start,
        @QueryParam("end") String end,
        @QueryParam("step") String step
    );
}
