package com.observe.os1.v1.metrics;

import com.observe.os1.v1.PrometheusRestClient;
import com.observe.os1.v1.prometheusQueries.UptimeQueries;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Instant;

@Path("/v1/metrics/uptime")
@Produces(MediaType.APPLICATION_JSON)
public class Uptime {

    @Inject
    @RestClient
    PrometheusRestClient prometheusRestClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get the current uptime of the system in hours")
    @Path("/in-hours")
    @APIResponse(
            responseCode = "200",
            description = "Prometheus query response for system uptime in hours",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            description = "Response containing system uptime in hours",
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
                    description = "Start time as Unix timestamp. Not mandatory, defaults to 1 second ago if not provided.",
                    example = "1752966880"
            ) Long startTime,

            @QueryParam("endTime")
            @Parameter(
                    description = "End time as Unix timestamp. Not mandatory, defaults to current time if not provided.",
                    example = "1752966940"
            ) Long endTime,

            @QueryParam("interval")
            @Parameter(
                    description = "Interval in seconds between data points. Also not mandatory, defaults to 15 seconds if not provided and gives one value.",
                    example = "15"
            ) Long interval
    ) {
        if (startTime == null) {
            startTime = Instant.now().getEpochSecond() - 1; // default to 1 sec ago
        }
        if (endTime == null) {
            endTime = Instant.now().getEpochSecond();
        }
        if (interval == null) {
            interval = 15L;
        }

        return prometheusRestClient.universalTimeQuery(
                UptimeQueries.TOTAL_UPTIME_IN_HOURS.toString(),
                startTime.toString(),
                endTime.toString(),
                interval + "s"
        );
    }
}
