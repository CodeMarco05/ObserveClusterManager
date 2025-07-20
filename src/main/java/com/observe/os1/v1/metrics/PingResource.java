package com.observe.os1.v1.metrics;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/v1/metrics/ping")
@Produces(MediaType.APPLICATION_JSON)
public class PingResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Ping an IP address and return comprehensive ping information",
            description = "Executes a ping command to the specified address and returns detailed network statistics including latency, packet loss, and system information in JSON format."
    )
    @APIResponse(
            responseCode = "200",
            description = "Ping executed successfully",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    example = """
        {
          "address": "8.8.8.8",
          "timestamp": "2025-07-20 15:31:00",
          "requestedBy": "CodeMarco05",
          "success": true,
          "exitCode": 0,
          "totalExecutionTimeMs": 3045,
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
          "ttl": 116,
          "sequences": [1, 2, 3, 4],
          "rawOutput": "PING 8.8.8.8 (8.8.8.8) 56(84) bytes of data...\\n64 bytes from 8.8.8.8: icmp_seq=1 ttl=116 time=14.2 ms",
          "errorOutput": "",
          "operatingSystem": "Linux",
          "javaVersion": "17.0.2"
        }
        """
            )
    )
    @APIResponse(
            responseCode = "408",
            description = "Ping timeout or host unreachable",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    example = """
        {
          "address": "192.168.1.999",
          "timestamp": "2025-07-20 15:31:00",
          "requestedBy": "CodeMarco05",
          "success": false,
          "exitCode": 2,
          "errorMessage": "Ping failed - host unreachable or timeout",
          "totalExecutionTimeMs": 3000,
          "packetCount": 4,
          "timeoutSeconds": 3,
          "packetsSent": 4,
          "packetsReceived": 0,
          "packetsLost": 4,
          "packetLossPercentage": 100.0,
          "minLatencyMs": 0.0,
          "maxLatencyMs": 0.0,
          "avgLatencyMs": 0.0,
          "stdDeviationMs": 0.0,
          "latencies": [],
          "ttl": 0,
          "sequences": [],
          "rawOutput": "ping: 192.168.1.999: Name or service not known",
          "errorOutput": "",
          "operatingSystem": "Linux",
          "javaVersion": "17.0.2"
        }
        """
            )
    )
    @APIResponse(
            responseCode = "500",
            description = "Internal server error during ping execution",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    example = """
        {
          "address": "8.8.8.8",
          "timestamp": "2025-07-20 15:31:00",
          "requestedBy": "CodeMarco05",
          "success": false,
          "exitCode": -1,
          "errorMessage": "IO Error executing ping command: Process execution failed",
          "totalExecutionTimeMs": 150,
          "packetCount": 4,
          "timeoutSeconds": 3,
          "packetsSent": 0,
          "packetsReceived": 0,
          "packetsLost": 0,
          "packetLossPercentage": 0.0,
          "latencies": [],
          "rawOutput": "",
          "errorOutput": "Permission denied",
          "operatingSystem": "Linux",
          "javaVersion": "17.0.2"
        }
        """
            )
    )
    @Schema(
            name = "PingResponse",
            description = "Comprehensive ping result containing all network statistics and system information"
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

        try {
            long startTime = System.currentTimeMillis();

            ProcessBuilder processBuilder;
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("win")) {
                processBuilder = new ProcessBuilder(
                        "ping", "-n", String.valueOf(count),
                        "-w", String.valueOf(timeout * 1000), address
                );
            } else {
                processBuilder = new ProcessBuilder(
                        "ping", "-c", String.valueOf(count),
                        "-W", String.valueOf(timeout), address
                );
            }

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
            );

            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8)
            );

            StringBuilder output = new StringBuilder();
            StringBuilder errorOutput = new StringBuilder();
            String line;

            // Read stdout
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                parsePingLine(line, result);
            }

            // Read stderr
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            long endTime = System.currentTimeMillis();

            result.exitCode = exitCode;
            result.success = exitCode == 0 && result.packetsReceived > 0;
            result.totalExecutionTimeMs = endTime - startTime;
            result.rawOutput = output.toString().trim();
            result.errorOutput = errorOutput.toString().trim();

            // Calculate statistics
            calculateStatistics(result);

            // Determine HTTP status code
            if (result.success) {
                return Response.ok(result).build();
            } else {
                result.errorMessage = "Ping failed - host unreachable or timeout";
                return Response.status(Response.Status.REQUEST_TIMEOUT).entity(result).build();
            }

        } catch (IOException e) {
            result.success = false;
            result.errorMessage = "IO Error executing ping command: " + e.getMessage();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            result.success = false;
            result.errorMessage = "Ping command interrupted: " + e.getMessage();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();

        } catch (Exception e) {
            result.success = false;
            result.errorMessage = "Unexpected error: " + e.getMessage();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
    }

    private void parsePingLine(String line, PingResult result) {
        try {
            // Parse individual ping responses
            if (line.contains("time=") || line.contains("time<")) {
                result.packetsReceived++;

                // Extract latency
                Pattern latencyPattern = Pattern.compile("time[<=](\\d+(?:\\.\\d+)?)");
                Matcher latencyMatcher = latencyPattern.matcher(line);
                if (latencyMatcher.find()) {
                    double latency = Double.parseDouble(latencyMatcher.group(1));
                    result.latencies.add(latency);
                }

                // Extract TTL
                Pattern ttlPattern = Pattern.compile("ttl=(\\d+)");
                Matcher ttlMatcher = ttlPattern.matcher(line);
                if (ttlMatcher.find()) {
                    result.ttl = Integer.parseInt(ttlMatcher.group(1));
                }

                // Extract sequence number
                Pattern seqPattern = Pattern.compile("icmp_seq=(\\d+)");
                Matcher seqMatcher = seqPattern.matcher(line);
                if (seqMatcher.find()) {
                    result.sequences.add(Integer.parseInt(seqMatcher.group(1)));
                }
            }

            // Parse summary statistics (Linux/Mac)
            if (line.contains("packets transmitted")) {
                Pattern statsPattern = Pattern.compile("(\\d+) packets transmitted, (\\d+) (?:packets )?received");
                Matcher statsMatcher = statsPattern.matcher(line);
                if (statsMatcher.find()) {
                    result.packetsSent = Integer.parseInt(statsMatcher.group(1));
                    result.packetsReceived = Integer.parseInt(statsMatcher.group(2));
                }
            }

            // Parse Windows statistics
            if (line.contains("Sent =") && line.contains("Received =")) {
                Pattern winPattern = Pattern.compile("Sent = (\\d+), Received = (\\d+), Lost = (\\d+)");
                Matcher winMatcher = winPattern.matcher(line);
                if (winMatcher.find()) {
                    result.packetsSent = Integer.parseInt(winMatcher.group(1));
                    result.packetsReceived = Integer.parseInt(winMatcher.group(2));
                    result.packetsLost = Integer.parseInt(winMatcher.group(3));
                }
            }

        } catch (Exception e) {
            // Ignore parsing errors for individual lines
        }
    }

    private void calculateStatistics(PingResult result) {
        if (result.packetsSent == 0) {
            result.packetsSent = result.packetCount;
        }

        if (result.packetsLost == 0 && result.packetsSent > 0) {
            result.packetsLost = result.packetsSent - result.packetsReceived;
        }

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

    @Schema(description = "Complete ping result with network statistics and system information")
    public static class PingResult {

        @Schema(description = "The target IP address or hostname that was pinged", example = "8.8.8.8")
        public String address;

        @Schema(description = "UTC timestamp when the ping was executed (YYYY-MM-DD HH:MM:SS format)", example = "2025-07-20 15:31:00")
        public String timestamp;

        @Schema(description = "Username of the person who requested the ping", example = "CodeMarco05")
        public String requestedBy;

        @Schema(description = "Overall success status - true if at least one packet was received", example = "true")
        public boolean success;

        @Schema(description = "Process exit code from ping command (0 = success, >0 = failure)", example = "0")
        public int exitCode;

        @Schema(description = "Error message if ping failed, null if successful", example = "Ping failed - host unreachable or timeout")
        public String errorMessage;

        @Schema(description = "Total time taken to execute ping command in milliseconds", example = "3045")
        public long totalExecutionTimeMs;

        // Ping Configuration
        @Schema(description = "Number of ping packets that were configured to be sent", example = "4")
        public int packetCount;

        @Schema(description = "Timeout configured for each ping packet in seconds", example = "3")
        public int timeoutSeconds;

        // Packet Statistics
        @Schema(description = "Actual number of packets sent by ping command", example = "4")
        public int packetsSent = 0;

        @Schema(description = "Number of packets that received a response", example = "4")
        public int packetsReceived = 0;

        @Schema(description = "Number of packets that were lost (sent - received)", example = "0")
        public int packetsLost = 0;

        @Schema(description = "Packet loss percentage calculated as (lost/sent)*100", example = "0.0")
        public double packetLossPercentage = 0.0;

        // Latency Statistics
        @Schema(description = "Minimum response time in milliseconds across all packets", example = "14.2")
        public double minLatencyMs = 0.0;

        @Schema(description = "Maximum response time in milliseconds across all packets", example = "18.7")
        public double maxLatencyMs = 0.0;

        @Schema(description = "Average response time in milliseconds across all packets", example = "16.3")
        public double avgLatencyMs = 0.0;

        @Schema(description = "Standard deviation of response times indicating consistency", example = "1.8")
        public double stdDeviationMs = 0.0;

        @Schema(description = "Array of individual response times in milliseconds for each packet", example = "[14.2, 16.1, 18.7, 16.2]")
        public List<Double> latencies = new ArrayList<>();

        // Network Information
        @Schema(description = "Time To Live value from ping responses (indicates network hops)", example = "116")
        public int ttl = 0;

        @Schema(description = "ICMP sequence numbers from each ping response", example = "[1, 2, 3, 4]")
        public List<Integer> sequences = new ArrayList<>();

        // Raw Output
        @Schema(description = "Complete raw output from ping command for debugging purposes")
        public String rawOutput;

        @Schema(description = "Error output from ping command if any errors occurred")
        public String errorOutput;

        // System Information
        @Schema(description = "Operating system where ping was executed", example = "Linux")
        public String operatingSystem = System.getProperty("os.name");

        @Schema(description = "Java version used to execute the ping", example = "17.0.2")
        public String javaVersion = System.getProperty("java.version");
    }
}
