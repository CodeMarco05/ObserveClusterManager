package com.observe.os1.v1.metrics;


import com.observe.os1.AppConfig;
import com.observe.os1.v1.PrometheusRestClient;
import com.observe.os1.v1.prometheusQueries.RamQuery;
import io.smallrye.common.constraint.NotNull;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/v1/metrics/ram")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RamResource {

    @Inject
    @RestClient
    PrometheusRestClient prometheusRestClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get RAM metrics as percentage usage")
    @Path("/usage-in-percent")
    @APIResponse(
            responseCode = "200",
            description = "Prometheus query response for RAM usage in percent",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            description = "Response containing RAM usage in percent",
                            example = """
                                    {
                                      "status": "success",
                                      "data": {
                                        "resultType": "matrix",
                                        "result": [
                                          {
                                            "metric": {
                                              "instance": "host.docker.internal:9100"
                                            },
                                            "values": [
                                              [1752966880, "50.5"],
                                              [1752966882, "49.8"]
                                            ]
                                          }
                                        ]
                                      }
                                    }
                                    """
                    )
            )
    )
    public Response getRamUsageInPercent(
            @QueryParam("startTime")
            @NotNull
            @Parameter(
                    description = "Start time as Unix timestamp",
                    example = "1752966880"
            ) Long startTime,

            @QueryParam("endTime")
            @NotNull
            @Parameter(
                    description = "End time as Unix timestamp",
                    example = "1752966940"
            ) Long endTime,

            @QueryParam("interval")
            @NotNull
            @Parameter(
                    description = "Interval in seconds between data points",
                    example = "15"
            ) Integer interval
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

        // test the rest client
        return prometheusRestClient.universalTimeQuery(
                RamQuery.MEMORY_USAGE_PERCENTAGE.toString(),
                startTime.toString(),
                endTime.toString(),
                interval + "s"
        );
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get free memory metrics")
    @Path("/free-memory-in-gb")
    @APIResponse(
            responseCode = "200",
            description = "Prometheus query response for free memory in GB",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            description = "Response containing free memory in GB",
                            example = """
                                    {
                                      "status": "success",
                                      "data": {
                                        "resultType": "matrix",
                                        "result": [
                                          {
                                            "metric": {
                                              "instance": "host.docker.internal:9100"
                                            },
                                            "values": [
                                              [1752966880, "8.5"],
                                              [1752966882, "8.2"]
                                            ]
                                          }
                                        ]
                                      }
                                    }
                                    """
                    )
            )
    )
    public Response getFreeMemoryInGB(
            @QueryParam("startTime")
            @NotNull
            @Parameter(
                    description = "Start time as Unix timestamp",
                    example = "1752966880"
            ) Long startTime,

            @QueryParam("endTime")
            @NotNull
            @Parameter(
                    description = "End time as Unix timestamp",
                    example = "1752966940"
            ) Long endTime,

            @QueryParam("interval")
            @NotNull
            @Parameter(
                    description = "Interval in seconds between data points",
                    example = "15"
            ) Integer interval
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
                RamQuery.MEMORY_AVAILABLE_GB.toString(),
                startTime.toString(),
                endTime.toString(),
                interval + "s"
        );
    }


    @GET
    @Path("/used-memory-in-gb")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get used memory metrics in GB")
    @APIResponse(
            responseCode = "200",
            description = "Prometheus query response for used memory in GB",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            description = "Response containing used memory in GB",
                            example = """
                                    {
                                      "status": "success",
                                      "data": {
                                        "resultType": "matrix",
                                        "result": [
                                          {
                                            "metric": {
                                              "instance": "host.docker.internal:9100"
                                            },
                                            "values": [
                                              [1752966880, "7.5"],
                                              [1752966882, "7.3"]
                                            ]
                                          }
                                        ]
                                      }
                                    }
                                    """
                    )
            )
    )
    public Response getUsedMemoryInGB(
            @QueryParam("startTime")
            @NotNull
            @Parameter(
                    description = "Start time as Unix timestamp",
                    example = "1752966880"
            ) Long startTime,

            @QueryParam("endTime")
            @NotNull
            @Parameter(
                    description = "End time as Unix timestamp",
                    example = "1752966940"
            ) Long endTime,

            @QueryParam("interval")
            @NotNull
            @Parameter(
                    description = "Interval in seconds between data points",
                    example = "15"
            ) Integer interval
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
                RamQuery.MEMORY_USAGE_GB.toString(),
                startTime.toString(),
                endTime.toString(),
                interval + "s"
        );
    }
}
