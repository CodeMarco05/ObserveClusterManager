package com.observe.os1.v1.metrics;

import com.observe.os1.v1.PrometheusRestClient;
import com.observe.os1.v1.prometheusQueries.NetworkQueries;
import io.smallrye.common.constraint.NotNull;
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

@Path("v1/metrics/network")
@Produces(MediaType.APPLICATION_JSON)
public class NetworkResource {

    @Inject
    @RestClient
    PrometheusRestClient prometheusRestClient;

    @GET
    @Path("/in")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get traffic in in bytes per second")
    @APIResponse(
            responseCode = "200",
            description = "Prometheus query response for network traffic in in bytes per second",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            description = "Response containing network traffic in in bytes per second",
                            example = """
                                    {
                                        "status": "success",
                                        "data": {
                                          "resultType": "matrix",
                                          "result": [
                                            {
                                              "metric": {},
                                              "values": [
                                                [
                                                  1753800800,
                                                  "540.5"
                                                ],
                                                [
                                                  1753800815,
                                                  "621"
                                                ],
                                                [
                                                  1753800830,
                                                  "509"
                                                ],
                                                [
                                                  1753800845,
                                                  "1215"
                                                ],
                                                [
                                                  1753800860,
                                                  "576"
                                                ],
                                                [
                                                  1753800875,
                                                  "466"
                                                ],
                                                [
                                                  1753800890,
                                                  "466"
                                                ]
                                              ]
                                            }
                                          ]
                                        }
                                      }
                                    """
                    )
            )
    )
    public Response getIncomingTrafficInBytesPerSecond(
            @QueryParam("startTime")
            @NotNull
            @Parameter(
                    description = "Start time as Unix timestamp",
                    example = "1753800800"
            ) Long startTime,

            @QueryParam("endTime")
            @NotNull
            @Parameter(
                    description = "End time as Unix timestamp",
                    example = "1753800900"
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
                NetworkQueries.NETWORK_IN_IN_BYTES_PER_SECOND.toString(),
                startTime.toString(),
                endTime.toString(),
                interval + "s"
        );
    }

    @GET
    @Path("/out")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get traffic out in bytes per second")
    @APIResponse(
            responseCode = "200",
            description = "Prometheus query response for network traffic out in bytes per second",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            description = "Response containing network traffic out in bytes per second",
                            example = """
                                    {
                                      "status": "success",
                                      "data": {
                                        "resultType": "matrix",
                                        "result": [
                                          {
                                            "metric": {},
                                            "values": [
                                              [
                                                1753800800,
                                                "6599"
                                              ],
                                              [
                                                1753800815,
                                                "6555.75"
                                              ],
                                              [
                                                1753800830,
                                                "6600.5"
                                              ],
                                              [
                                                1753800845,
                                                "6552.5"
                                              ],
                                              [
                                                1753800860,
                                                "6557.5"
                                              ],
                                              [
                                                1753800875,
                                                "6555.5"
                                              ],
                                              [
                                                1753800890,
                                                "6551.5"
                                              ]
                                            ]
                                          }
                                        ]
                                      }
                                    }
                                    """
                    )
            )
    )
    public Response getOutgoingTrafficInBytesPerSecond(
            @QueryParam("startTime")
            @NotNull
            @Parameter(
                    description = "Start time as Unix timestamp",
                    example = "1753800800"
            ) Long startTime,

            @QueryParam("endTime")
            @NotNull
            @Parameter(
                    description = "End time as Unix timestamp",
                    example = "1753800900"
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
                NetworkQueries.NETWORK_OUT_IN_BYTES_PER_SECOND.toString(),
                startTime.toString(),
                endTime.toString(),
                interval + "s"
        );
    }
}
