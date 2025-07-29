package com.observe.os1.v1.metrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.observe.os1.v1.PrometheusRestClient;
import com.observe.os1.v1.metrics.responseModels.CpuResourceResponse;
import com.observe.os1.v1.metrics.responseModels.CpuResponse;
import com.observe.os1.v1.prometheusQueries.CpuQuereis;
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

@Path("/v1/metrics/cpu")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CpuResource {

    @Inject
    @RestClient
    PrometheusRestClient prometheusRestClient;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get Prometheus metrics pretty formatted as CPU usage in percent")
    @Path("/usage-in-percent")
    @APIResponse(
            responseCode = "200",
            description = "Prometheus query response",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = CpuResourceResponse.class
                    )
            )
    )
    public Response getCpuUsageInPercent(
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

        Response response;

        try {
            response = prometheusRestClient.universalTimeQuery(
                    CpuQuereis.CPU_USAGE_PERCENTAGE.toString(),
                    startTime.toString(),
                    endTime.toString(),
                    interval + "s"
            );
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error querying Prometheus: " + e.getMessage())
                    .build();
        }


        CpuResponse cpuResponse;

        try {
            String json = response.readEntity(String.class);
            ObjectMapper mapper = new ObjectMapper();
            cpuResponse = mapper.readValue(json, CpuResponse.class);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error processing the response: " + e.getMessage())
                    .build();
        }

        try {
            // add all the values up and put them in one list
            CpuResourceResponse cpuResourceResponse = addUpValues(cpuResponse);
            return Response.ok().entity(cpuResourceResponse).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error processing CPU data: " + e.getMessage())
                    .build();
        }
    }

    private CpuResourceResponse addUpValues(CpuResponse cpuResponse) throws NullPointerException {
        CpuResourceResponse cpuResourceResponse = new CpuResourceResponse();
        if (cpuResponse != null && cpuResponse.data != null && cpuResponse.data.result != null) {
            cpuResponse.data.result.forEach(result -> {
                result.values.forEach(value -> {
                    if (value == null) {
                        throw new NullPointerException("value in result.values is null");
                    }

                    long timeStamp = value.timestamp;
                    if (!cpuResourceResponse.metric.containsKey(timeStamp)) {
                        cpuResourceResponse.metric.put(timeStamp, value.value);
                    } else {
                        cpuResourceResponse.metric.compute(timeStamp, (k, valueAtTimeStamp) -> {
                            if (valueAtTimeStamp == null) {
                                throw new NullPointerException("valueAtTimeStamp is null for timestamp: " + timeStamp);
                            }
                            return valueAtTimeStamp + value.value;
                        });
                    }
                });
            });
        }
        return cpuResourceResponse;
    }
}

