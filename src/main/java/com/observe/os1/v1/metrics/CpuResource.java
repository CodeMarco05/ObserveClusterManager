package com.observe.os1.v1.metrics;

import com.observe.os1.AppConfig;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;

@Path("/v1/metrics/cpu")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CpuResource {

    @Inject
    AppConfig appConfig;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get Prometheus metrics")
    @Path("/usage-as-percentage")
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
    public Response getCpuUsageAsPercentage(
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

        String base = appConfig.prometheus().baseUrl();
        String baseUrl = base + "/api/v1/query_range";

        // Query as in the curl command
        String query = "avg by (instance,mode) (irate(node_cpu_seconds_total{mode!=\"idle\"}[15s]))";

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

        try {
            // Execute request
            URL url = new URL(urlWithParams);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            conn.disconnect();

            // Check the response status code
            if (status != HttpURLConnection.HTTP_OK) {
                return Response.status(status)
                        .entity("Error fetching data from Prometheus: " + response.toString())
                        .build();
            }
            // Return the response
            return Response.ok(response.toString())
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .build();

        } catch (MalformedURLException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid URL: " + e.getMessage())
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error connecting to Prometheus: " + e.getMessage())
                    .build();
        }
    }


}