package com.observe.os1.v1.metrics;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
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

    // This class will handle CPU metrics related endpoints.
    // You can define methods here to fetch CPU usage, load averages, etc.
    // For example, you might have methods like getCpuUsage(), getLoadAverage(), etc.

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get Prometheus metrics")
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
            @QueryParam("startTime") Long startTime,
            @QueryParam("endTime") Long endTime,
            @QueryParam("interval") Long interval
    ) throws IOException {
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

        String baseUrl = "http://localhost:9090/api/v1/query_range";

        // Query wie im curl-Befehl
        String query = "avg by (instance,mode) (irate(node_cpu_seconds_total{mode!=\"idle\"}[15s]))";

        // Schrittweite (interval) in Prometheus-Syntax: z. B. "1s", "5s", "60s"
        String step = interval + "s";

        // URL mit Parametern zusammenbauen
        String urlWithParams = String.format("%s?query=%s&start=%s&end=%s&step=%s",
                baseUrl,
                URLEncoder.encode(query, StandardCharsets.UTF_8),
                URLEncoder.encode(startTime.toString(), StandardCharsets.UTF_8),
                URLEncoder.encode(endTime.toString(), StandardCharsets.UTF_8),
                URLEncoder.encode(step, StandardCharsets.UTF_8)
        );

        // Anfrage ausführen
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

        // Überprüfen Sie den Statuscode der Antwort
        if (status != HttpURLConnection.HTTP_OK) {
            return Response.status(status)
                    .entity("Error fetching data from Prometheus: " + response.toString())
                    .build();
        }
        // Geben Sie die Antwort zurück
        return Response.ok(response.toString())
                .header("Content-Type", MediaType.APPLICATION_JSON)
                .build();
    }
}
