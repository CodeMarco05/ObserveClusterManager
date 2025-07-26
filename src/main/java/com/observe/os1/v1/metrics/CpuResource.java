package com.observe.os1.v1.metrics;

import com.observe.os1.v1.PrometheusRestClient;
import com.observe.os1.v1.prometheusQueries.CpuQuereis;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/v1/metrics/cpu")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CpuResource {

    @Inject
    @RestClient
    PrometheusRestClient prometheusRestClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get Prometheus metrics")
    @Path("/usage-in-percent")
    @APIResponse(
            responseCode = "200",
            description = "Prometheus query response",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    examples = @ExampleObject(
                            name = "Prometheus Query Response",
                            summary = "Successful response with CPU metrics",
                            description = "Example showing matrix result type with time series data",
                            value = """
            {
              "status": "success",
              "data": {
                "resultType": "matrix",
                "result": [
                  {
                    "metric": {
                      "instance": "host.docker.internal:9100",
                      "mode": "user"
                    },
                    "values": [
                      [1752966880, "0.018324171247710568"],
                      [1752966882, "0.03083333333333016"]
                    ]
                  }
                ]
              }
            }
            """
                    )
            )
    )
    public Response getCpuUsageInPercent(
            @QueryParam("startTime")
            @Parameter(
                    description = "Start time as Unix timestamp",
                    example = "1752966880"
            ) Long startTime,

            @QueryParam("endTime")
            @Parameter(
                    description = "End time as Unix timestamp",
                    example = "1752966940"
            ) Long endTime,

            @QueryParam("interval")
            @Parameter(
                    description = "Interval in seconds between data points",
                    example = "15"
            ) Long interval

    ) {
        // check the parameters
        if (startTime == null || endTime == null || interval == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing required query parameters: startTime, endTime, interval")
                    .build();
        }
        // Validate the time range
        if (startTime >= endTime) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("startTime must be less than endTime")
                    .build();
        }

        return prometheusRestClient.universalTimeQuery(
                CpuQuereis.CPU_USAGE_PERCENTAGE.toString(),
                startTime.toString(),
                endTime.toString(),
                interval + "s"
        );
    }


}