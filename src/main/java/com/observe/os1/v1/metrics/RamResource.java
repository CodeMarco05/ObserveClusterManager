package com.observe.os1.v1.metrics;


import com.observe.os1.AppConfig;
import com.observe.os1.v1.PrometheusUtil;
import com.observe.os1.v1.prometheus.ram.PrometheusRamRestClient;
import com.observe.os1.v1.prometheus.ram.RamQueryType;
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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Path("/v1/metrics/ram")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RamResource {

    @Inject
    AppConfig appConfig;

    @Inject
    @RestClient
    PrometheusRamRestClient prometheusRamRestClient;

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
            ) Integer interval
    ){
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
        return prometheusRamRestClient.getRamUsageAsPercentage(
                RamQueryType.RAM_USAGE_PERCENTAGE,
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
    public Response getFreeMemory(
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

        String base = appConfig.prometheus().baseUrl();
        String baseUrl = base + "/api/v1/query_range";

        // Query as in the curl command
        String query = "node_memory_MemAvailable_bytes / 1024 / 1024 / 1024";

        // Step size (interval) in Prometheus syntax: e.g. "1s", "5s", "60s"
        String step = interval + "s";

        // Build URL with parameters
        String urlWithParams = String.format("%s?query=%s&start=%s&end=%s&step=%s",
                baseUrl,
                URLEncoder.encode(query, StandardCharsets.UTF_8),
                URLEncoder.encode(startTime.toString(), StandardCharsets.UTF_8),
                URLEncoder.encode(endTime.toString(), StandardCharsets.UTF_8),
                URLEncoder.encode(step, StandardCharsets.UTF_8)
        );

        return PrometheusUtil.executePrometheusRequest(urlWithParams);
    }

    @GET
    @Path("/used-memory-in-gb")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsedMemoryInGB(
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

        String base = appConfig.prometheus().baseUrl();
        String baseUrl = base + "/api/v1/query_range";

        // Query as in the curl command
        String query = "(node_memory_MemTotal_bytes - node_memory_MemAvailable_bytes) / 1024 / 1024 / 1024";

        // Step size (interval) in Prometheus syntax: e.g. "1s", "5s", "60s"
        String step = interval + "s";

        // Build URL with parameters
        String urlWithParams = String.format("%s?query=%s&start=%s&end=%s&step=%s",
                baseUrl,
                URLEncoder.encode(query, StandardCharsets.UTF_8),
                URLEncoder.encode(startTime.toString(), StandardCharsets.UTF_8),
                URLEncoder.encode(endTime.toString(), StandardCharsets.UTF_8),
                URLEncoder.encode(step, StandardCharsets.UTF_8)
        );

        return PrometheusUtil.executePrometheusRequest(urlWithParams);
    }
}
