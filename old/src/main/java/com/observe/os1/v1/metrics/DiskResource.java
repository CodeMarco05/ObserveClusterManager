package com.observe.os1.v1.metrics;

import com.observe.os1.v1.PrometheusRestClient;
import com.observe.os1.v1.prometheusQueries.DiskQueries;
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

@Path("/v1/metrics/disk")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DiskResource {

    @Inject
    @RestClient
    PrometheusRestClient prometheusRestClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/free-space-in-gb")
    @Operation(summary = "Get free disk space in gb")
    @APIResponse(
            responseCode = "200",
            description = "Prometheus query response for free disk space in GB",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            description = "Response containing free disk space in GB",
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
                                                1752966880,
                                                "37.23836898803711"
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
    public Response getFreeSpaceInGB(
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
                    example = "1752966880"
            ) Long endTime,

            @QueryParam("interval")
            @NotNull
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
        if (startTime < 0 || endTime < 0 || interval <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid query parameters: startTime, endTime must be non-negative and interval must be positive")
                    .build();
        }

        return prometheusRestClient.universalTimeQuery(
                DiskQueries.DISK_AVAILABLE_IN_GB.toString(),
                startTime.toString(),
                endTime.toString(),
                interval + "s"
        );
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/used-space-in-gb")
    @Operation(summary = "Get used disk space in gb")
    @APIResponse(
            responseCode = "200",
            description = "Prometheus query response for used disk space in GB",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            description = "Response containing used disk space in GB",
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
                                                1752966880,
                                                "25.579723358154297"
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
    public Response getUsedSpaceInGB(
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
                    example = "1752966880"
            ) Long endTime,

            @QueryParam("interval")
            @NotNull
            @Parameter(
                    description = "Interval in seconds between data points",
                    example = "5"
            ) Long interval
    ) {
        // check the parameters
        if (startTime == null || endTime == null || interval == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing required query parameters: startTime, endTime, interval")
                    .build();
        }
        if (startTime < 0 || endTime < 0 || interval <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid query parameters: startTime, endTime must be non-negative and interval must be positive")
                    .build();
        }

        return prometheusRestClient.universalTimeQuery(
                DiskQueries.DISK_USAGE_IN_GB.toString(),
                startTime.toString(),
                endTime.toString(),
                interval + "s"
        );
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/total-size-in-gb")
    @Operation(summary = "Get total disk size in gb")
    @APIResponse(
            responseCode = "200",
            description = "Prometheus query response for total disk space in GB",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            description = "Response containing total disk space in GB",
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
                                                1753798144,
                                                "62.818092346191406"
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
    public Response getTotalDiskSizeInGB(
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
                    example = "1752966980"
            ) Long endTime,

            @QueryParam("interval")
            @NotNull
            @Parameter(
                    description = "Interval in seconds between data points",
                    example = "5"
            ) Long interval
    ) {
        // check the parameters
        if (startTime == null || endTime == null || interval == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing required query parameters: startTime, endTime, interval")
                    .build();
        }
        if (startTime < 0 || endTime < 0 || interval <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid query parameters: startTime, endTime must be non-negative and interval must be positive")
                    .build();
        }

        return prometheusRestClient.universalTimeQuery(
                DiskQueries.DISK_TOTAL_SIZE_IN_GB.toString(),
                startTime.toString(),
                endTime.toString(),
                interval + "s"
        );
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/total-size-in-gb-all-volumes")
    @Operation(summary = "Get total disk size in gb over all volumes")
    @APIResponse(
            responseCode = "200",
            description = "Prometheus query response for total disk space in GB over all volumes",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            description = "Response containing total disk space in GB over all volumes",
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
                                                 1753798144,
                                                 "62.818092346191406"
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
    public Response getTotalDiskSizeInGBOverAllVolumes(
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
                    example = "1752966980"
            ) Long endTime,

            @QueryParam("interval")
            @NotNull
            @Parameter(
                    description = "Interval in seconds between data points",
                    example = "5"
            ) Long interval
    ) {
        // check the parameters
        if (startTime == null || endTime == null || interval == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Missing required query parameters: startTime, endTime, interval")
                    .build();
        }
        if (startTime < 0 || endTime < 0 || interval <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid query parameters: startTime, endTime must be non-negative and interval must be positive")
                    .build();
        }

        return prometheusRestClient.universalTimeQuery(
                DiskQueries.DISK_TOTAL_SIZE_IN_GB_ALL_SYSTEMS.toString(),
                startTime.toString(),
                endTime.toString(),
                interval + "s"
        );
    }

}
