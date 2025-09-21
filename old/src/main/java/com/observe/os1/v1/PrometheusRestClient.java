package com.observe.os1.v1;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "prometheus-metrics-client")
@Path("/api/v1/query_range")
public interface PrometheusRestClient {
    /**
     * Get universal time query results from Prometheus.
     *
     * @param query the Prometheus query to execute
     * @param start the start time of the range in Unix timestamp format
     * @param end   the end time of the range in Unix timestamp format
     * @param step  the resolution of the data points in seconds
     * @return a Response containing the Prometheus query result
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Response universalTimeQuery(
            @QueryParam("query") String query,
            @QueryParam("start") String start,
            @QueryParam("end") String end,
            @QueryParam("step") String step
    );
}
