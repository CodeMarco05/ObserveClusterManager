package com.observe.os1.v1.metrics;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Path("/v1/metrics/ping")
@Produces(MediaType.APPLICATION_JSON)
public class PingResource {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Ping an IP address and return comprehensive ping information",
            description = "Tests network connectivity to the specified address using Java's built-in networking and returns detailed network statistics including latency, packet loss, and system information in JSON format."
    )
    @APIResponse(
            responseCode = "200",
            description = "Ping executed successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    example = """
        {
          "address": "8.8.8.8",
          "timestamp": "2025-07-23 09:26:35",
          "requestedBy": "CodeMarco05",
          "success": true,
          "exitCode": 0,
          "totalExecutionTimeMs": 1045,
          "packetCount": 4,
          "timeoutSeconds": 3,
          "packetsSent": 4,
          "packetsReceived": 4,
          "packetsLost": 0,
          "packetLossPercentage": 0.0,
          "minLatencyMs": 14.2,
          "maxLatencyMs": 18.7,
          "avgLatencyMs": 16.3,
          "stdDeviationMs": 1.8,
          "latencies": [14.2, 16.1, 18.7, 16.2],
          "ttl": 64,
          "sequences": [1, 2, 3, 4],
          "rawOutput": "Java Network Connectivity Test Results",
          "errorOutput": "",
          "operatingSystem": "Linux",
          "javaVersion": "17.0.2",
          "resolvedIpAddress": "8.8.8.8",
          "dnsLookupTimeMs": 12.5
        }
        """
            )
    )
    @Path("/ping-ip-address")
    public Response pingAddress(
            @QueryParam("address")
            @Parameter(
                    description = "The address to ping",
                    example = "8.8.8.8"
            ) @DefaultValue("8.8.8.8") String address,

            @QueryParam("count")
            @Parameter(
                    description = "Number of ping packets to send",
                    example = "4"
            ) @DefaultValue("4") int count,

            @QueryParam("timeout")
            @Parameter(
                    description = "Timeout in seconds",
                    example = "3"
            ) @DefaultValue("3") int timeout
    ) {
        PingResult result = new PingResult();
        result.address = address;
        result.timestamp = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        result.requestedBy = "CodeMarco05";
        result.packetCount = count;
        result.timeoutSeconds = timeout;
        result.packetsSent = count;

        try {
            long startTime = System.currentTimeMillis();

            // Perform Java-based network connectivity test
            performJavaNetworkTest(result, address, count, timeout);

            long endTime = System.currentTimeMillis();
            result.totalExecutionTimeMs = endTime - startTime;

            // Calculate statistics
            calculateStatistics(result);

            // Set success based on received packets
            result.success = result.packetsReceived > 0;
            result.exitCode = result.success ? 0 : 1;

            if (result.success) {
                return Response.ok(result).build();
            } else {
                result.errorMessage = "Network connectivity test failed - host unreachable or timeout";
                return Response.status(Response.Status.REQUEST_TIMEOUT).entity(result).build();
            }

        } catch (Exception e) {
            result.success = false;
            result.exitCode = -1;
            result.errorMessage = "Error executing network test: " + e.getMessage();
            result.errorOutput = e.getClass().getSimpleName() + ": " + e.getMessage();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
    }

    private void performJavaNetworkTest(PingResult result, String address, int count, int timeoutSeconds) {
        try {
            // DNS resolution with timing
            long dnsStart = System.nanoTime();
            InetAddress inetAddress = InetAddress.getByName(address);
            long dnsEnd = System.nanoTime();

            result.resolvedIpAddress = inetAddress.getHostAddress();
            result.dnsLookupTimeMs = (dnsEnd - dnsStart) / 1_000_000.0;

            StringBuilder output = new StringBuilder();
            output.append("Java Network Connectivity Test Results\n");
            output.append("Target: ").append(address).append(" (").append(result.resolvedIpAddress).append(")\n");
            output.append("DNS Lookup Time: ").append(String.format("%.2f", result.dnsLookupTimeMs)).append(" ms\n\n");

            // Perform multiple connectivity tests
            List<CompletableFuture<ConnectivityResult>> futures = new ArrayList<>();

            for (int i = 1; i <= count; i++) {
                final int sequence = i;
                CompletableFuture<ConnectivityResult> future = CompletableFuture.supplyAsync(() -> {
                    return performSingleConnectivityTest(inetAddress, timeoutSeconds, sequence);
                }, executor);
                futures.add(future);

                // Small delay between tests to simulate ping behavior
                if (i < count) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            // Collect results
            for (CompletableFuture<ConnectivityResult> future : futures) {
                try {
                    ConnectivityResult connectResult = future.get(timeoutSeconds + 1, TimeUnit.SECONDS);

                    if (connectResult.success) {
                        result.packetsReceived++;
                        result.latencies.add(connectResult.latencyMs);
                        result.sequences.add(connectResult.sequence);
                        result.ttl = Math.max(result.ttl, connectResult.estimatedTtl);

                        output.append(String.format("Response from %s: seq=%d time=%.2f ms ttl=%d\n",
                                result.resolvedIpAddress, connectResult.sequence,
                                connectResult.latencyMs, connectResult.estimatedTtl));
                    } else {
                        output.append(String.format("Request timeout for seq=%d\n", connectResult.sequence));
                    }
                } catch (Exception e) {
                    output.append(String.format("Error for sequence %d: %s\n",
                            futures.indexOf(future) + 1, e.getMessage()));
                }
            }

            result.rawOutput = output.toString();

        } catch (UnknownHostException e) {
            result.errorMessage = "Unknown host: " + address;
            result.errorOutput = e.getMessage();
            result.rawOutput = "DNS resolution failed for " + address;
        } catch (Exception e) {
            result.errorMessage = "Network test failed: " + e.getMessage();
            result.errorOutput = e.getMessage();
            result.rawOutput = "Network connectivity test encountered an error";
        }
    }

    private ConnectivityResult performSingleConnectivityTest(InetAddress address, int timeoutSeconds, int sequence) {
        ConnectivityResult result = new ConnectivityResult();
        result.sequence = sequence;
        result.estimatedTtl = 64; // Default TTL estimate

        long startTime = System.nanoTime();

        try {
            // Try multiple approaches for better connectivity testing

            // Approach 1: InetAddress.isReachable (ICMP if available, otherwise TCP)
            boolean reachableViaICMP = address.isReachable(timeoutSeconds * 1000);

            if (reachableViaICMP) {
                long endTime = System.nanoTime();
                result.latencyMs = (endTime - startTime) / 1_000_000.0;
                result.success = true;
                return result;
            }

            // Approach 2: TCP connection test to common ports if ICMP fails
            int[] commonPorts = {80, 443, 53, 22, 21, 25, 110, 995, 993, 143};

            for (int port : commonPorts) {
                try (Socket socket = new Socket()) {
                    long tcpStart = System.nanoTime();
                    socket.connect(new InetSocketAddress(address, port), timeoutSeconds * 1000);
                    long tcpEnd = System.nanoTime();

                    result.latencyMs = (tcpEnd - tcpStart) / 1_000_000.0;
                    result.success = true;
                    result.estimatedTtl = 64; // Estimate based on typical values
                    return result;
                } catch (IOException e) {
                    // Port not open, try next one
                    continue;
                }
            }

            // If no ports are open, still check if we got any network response
            result.success = false;
            result.latencyMs = 0.0;

        } catch (IOException e) {
            result.success = false;
            result.latencyMs = 0.0;
        }

        return result;
    }

    private void calculateStatistics(PingResult result) {
        result.packetsLost = result.packetsSent - result.packetsReceived;

        if (result.packetsSent > 0) {
            result.packetLossPercentage = ((double) result.packetsLost / result.packetsSent) * 100.0;
        }

        if (!result.latencies.isEmpty()) {
            result.minLatencyMs = result.latencies.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
            result.maxLatencyMs = result.latencies.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
            result.avgLatencyMs = result.latencies.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

            // Calculate standard deviation
            double mean = result.avgLatencyMs;
            double variance = result.latencies.stream()
                    .mapToDouble(latency -> Math.pow(latency - mean, 2))
                    .average()
                    .orElse(0.0);
            result.stdDeviationMs = Math.sqrt(variance);
        }
    }

    // Helper class for connectivity test results
    private static class ConnectivityResult {
        boolean success;
        double latencyMs;
        int sequence;
        int estimatedTtl;
    }

    @Schema(description = "Complete ping result with network statistics and system information")
    public static class PingResult {
        @Schema(description = "The target IP address or hostname that was pinged", example = "8.8.8.8")
        public String address;

        @Schema(description = "UTC timestamp when the ping was executed (YYYY-MM-DD HH:MM:SS format)", example = "2025-07-23 09:26:35")
        public String timestamp;

        @Schema(description = "Username of the person who requested the ping", example = "CodeMarco05")
        public String requestedBy;

        @Schema(description = "Overall success status - true if at least one packet was received", example = "true")
        public boolean success;

        @Schema(description = "Process exit code (0 = success, >0 = failure)", example = "0")
        public int exitCode;

        @Schema(description = "Error message if ping failed, null if successful")
        public String errorMessage;

        @Schema(description = "Total time taken to execute connectivity test in milliseconds", example = "1045")
        public long totalExecutionTimeMs;

        @Schema(description = "Number of connectivity tests configured to be performed", example = "4")
        public int packetCount;

        @Schema(description = "Timeout configured for each test in seconds", example = "3")
        public int timeoutSeconds;

        @Schema(description = "Number of connectivity tests performed", example = "4")
        public int packetsSent = 0;

        @Schema(description = "Number of successful connectivity tests", example = "4")
        public int packetsReceived = 0;

        @Schema(description = "Number of failed connectivity tests", example = "0")
        public int packetsLost = 0;

        @Schema(description = "Failure percentage calculated as (lost/sent)*100", example = "0.0")
        public double packetLossPercentage = 0.0;

        @Schema(description = "Minimum response time in milliseconds", example = "14.2")
        public double minLatencyMs = 0.0;

        @Schema(description = "Maximum response time in milliseconds", example = "18.7")
        public double maxLatencyMs = 0.0;

        @Schema(description = "Average response time in milliseconds", example = "16.3")
        public double avgLatencyMs = 0.0;

        @Schema(description = "Standard deviation of response times", example = "1.8")
        public double stdDeviationMs = 0.0;

        @Schema(description = "Array of individual response times in milliseconds", example = "[14.2, 16.1, 18.7, 16.2]")
        public List<Double> latencies = new ArrayList<>();

        @Schema(description = "Estimated Time To Live value", example = "64")
        public int ttl = 0;

        @Schema(description = "Sequence numbers from each connectivity test", example = "[1, 2, 3, 4]")
        public List<Integer> sequences = new ArrayList<>();

        @Schema(description = "Complete output from connectivity test")
        public String rawOutput;

        @Schema(description = "Error output if any errors occurred")
        public String errorOutput;

        @Schema(description = "Operating system where test was executed", example = "Linux")
        public String operatingSystem = System.getProperty("os.name");

        @Schema(description = "Java version used to execute the test", example = "21.0.1")
        public String javaVersion = System.getProperty("java.version");

        @Schema(description = "Resolved IP address of the target", example = "8.8.8.8")
        public String resolvedIpAddress;

        @Schema(description = "Time taken for DNS lookup in milliseconds", example = "12.5")
        public double dnsLookupTimeMs = 0.0;
    }
}