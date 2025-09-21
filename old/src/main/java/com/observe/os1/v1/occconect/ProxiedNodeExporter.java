package com.observe.os1.v1.occconect;

import com.observe.os1.models.ClusterClient;
import io.quarkus.logging.Log;
import io.smallrye.common.constraint.NotNull;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Path("v1/proxied-node-exporter")
public class ProxiedNodeExporter {

    @Inject
    ClusterClient clusterClient;

    @GET
    @Produces("text/plain; version=0.0.4; charset=utf-8")
    public Response getProxiedNode(
            @QueryParam("node_name")
            @NotNull
            String nodeName
    ) {
        Log.info("Requested proxied node exporter metrics for node: " + nodeName);

        // load the url from the database
        String uri = clusterClient.getUriByName(nodeName);


        String url = "http://100.69.175.1:9100/metrics"; // TODO dynamic url

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .build();

        try{
            // TODO maybe pack into a service or rest client and timeout handling
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Log.info("Successfully fetched metrics from proxied node exporter.");
                return Response.ok(response.body())
                        .header("Content-Type", "text/plain; version=0.0.4; charset=utf-8")
                        .build();
            } else {
                Log.error("Failed to fetch metrics from proxied node exporter. Status code: " + response.statusCode());
                return Response.status(Response.Status.BAD_GATEWAY)
                        .entity("Failed to fetch metrics from proxied node exporter.")
                        .build();
            }
        }catch (Exception e){
            Log.error("Error fetching metrics from proxied node exporter: " + e.getMessage());
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity("Error fetching metrics from proxied node exporter.")
                    .build();
        }
    }

    /*@GET
    @Produces("text/plain; version=0.0.4; charset=utf-8")
    public Response getProxiedNode() {
        // Generate sample Prometheus metrics
        StringBuilder metrics = new StringBuilder();

        // Add HELP and TYPE comments
        metrics.append("# HELP http_requests_total Total number of HTTP requests\n");
        metrics.append("# TYPE http_requests_total counter\n");
        metrics.append("http_requests_total{method=\"GET\",status=\"200\"} 1234\n");
        metrics.append("http_requests_total{method=\"POST\",status=\"200\"} 567\n");
        metrics.append("http_requests_total{method=\"GET\",status=\"404\"} 89\n");
        metrics.append("\n");

        metrics.append("# HELP memory_usage_bytes Current memory usage in bytes\n");
        metrics.append("# TYPE memory_usage_bytes gauge\n");
        metrics.append("memory_usage_bytes{type=\"heap\"} ").append(Runtime.getRuntime().totalMemory()).append("\n");
        metrics.append("memory_usage_bytes{type=\"used\"} ").append(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()).append("\n");
        metrics.append("\n");

        metrics.append("# HELP cpu_usage_ratio CPU usage ratio\n");
        metrics.append("# TYPE cpu_usage_ratio gauge\n");
        metrics.append("cpu_usage_ratio ").append(Math.random() * 0.8).append("\n");
        metrics.append("\n");

        metrics.append("# HELP node_temperature_celsius Node temperature in Celsius\n");
        metrics.append("# TYPE node_temperature_celsius gauge\n");
        metrics.append("node_temperature_celsius{sensor=\"cpu\"} ").append(45 + Math.random() * 20).append("\n");
        metrics.append("node_temperature_celsius{sensor=\"gpu\"} ").append(60 + Math.random() * 15).append("\n");
        metrics.append("\n");

        metrics.append("# HELP disk_usage_bytes Disk usage in bytes\n");
        metrics.append("# TYPE disk_usage_bytes gauge\n");
        metrics.append("disk_usage_bytes{device=\"/dev/sda1\",fstype=\"ext4\"} 1073741824000\n");
        metrics.append("disk_usage_bytes{device=\"/dev/sda2\",fstype=\"ext4\"} 536870912000\n");
        metrics.append("\n");

        metrics.append("# HELP network_bytes_total Network traffic in bytes\n");
        metrics.append("# TYPE network_bytes_total counter\n");
        metrics.append("network_bytes_total{device=\"eth0\",direction=\"tx\"} 987654321\n");
        metrics.append("network_bytes_total{device=\"eth0\",direction=\"rx\"} 1234567890\n");
        metrics.append("\n");

        metrics.append("# HELP up Whether the service is up\n");
        metrics.append("# TYPE up gauge\n");
        metrics.append("up 1\n");
        metrics.append("\n");

        metrics.append("# HELP process_start_time_seconds Process start time in seconds since epoch\n");
        metrics.append("# TYPE process_start_time_seconds gauge\n");
        metrics.append("process_start_time_seconds ").append(System.currentTimeMillis() / 1000 - 3600).append("\n");

        return Response.ok(metrics.toString())
                .header("Content-Type", "text/plain; version=0.0.4; charset=utf-8")
                .build();
    }*/
}
