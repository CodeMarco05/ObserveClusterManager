package com.observe.os1.v1.metrics.responseModels;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "Complete ping result with network statistics and system information")
public class PingResourceResponse {

    @Schema(description = "The target IP address or hostname that was pinged", examples = { "8.8.8.8" })
    public String address;

    @Schema(description = "UTC timestamp when the ping was executed (YYYY-MM-DD HH:MM:SS format)", examples = { "2025-07-23 09:26:35" })
    public String timestamp;

    @Schema(description = "Username of the person who requested the ping", examples = { "CodeMarco05" })
    public String requestedBy;

    @Schema(description = "Overall success status - true if at least one packet was received", examples = { "true" })
    public boolean success;

    @Schema(description = "Process exit code (0 = success, >0 = failure)", examples = { "0" })
    public int exitCode;

    @Schema(description = "Error message if ping failed, null if successful")
    public String errorMessage;

    @Schema(description = "Total time taken to execute connectivity test in milliseconds", examples = { "1045" })
    public long totalExecutionTimeMs;

    @Schema(description = "Number of connectivity tests configured to be performed", examples = { "4" })
    public int packetCount;

    @Schema(description = "Timeout configured for each test in seconds", examples = { "3" })
    public int timeoutSeconds;

    @Schema(description = "Number of connectivity tests performed", examples = { "4" })
    public int packetsSent = 0;

    @Schema(description = "Number of successful connectivity tests", examples = { "4" })
    public int packetsReceived = 0;

    @Schema(description = "Number of failed connectivity tests", examples = { "0" })
    public int packetsLost = 0;

    @Schema(description = "Failure percentage calculated as (lost/sent)*100", examples = { "0.0" })
    public double packetLossPercentage = 0.0;

    @Schema(description = "Minimum response time in milliseconds", examples = { "14.2" })
    public double minLatencyMs = 0.0;

    @Schema(description = "Maximum response time in milliseconds", examples = { "18.7" })
    public double maxLatencyMs = 0.0;

    @Schema(description = "Average response time in milliseconds", examples = { "16.3" })
    public double avgLatencyMs = 0.0;

    @Schema(description = "Standard deviation of response times", examples = { "1.8" })
    public double stdDeviationMs = 0.0;

    @Schema(description = "Array of individual response times in milliseconds", examples = { "[14.2, 16.1, 18.7, 16.2]" })
    public List<Double> latencies = new ArrayList<>();

    @Schema(description = "Estimated Time To Live value", examples = { "64" })
    public int ttl = 0;

    @Schema(description = "Sequence numbers from each connectivity test", examples = { "[1, 2, 3, 4]" })
    public List<Integer> sequences = new ArrayList<>();

    @Schema(description = "Complete output from connectivity test")
    public String rawOutput;

    @Schema(description = "Error output if any errors occurred")
    public String errorOutput;

    @Schema(description = "Operating system where test was executed", examples = { "Linux" })
    public String operatingSystem = System.getProperty("os.name");

    @Schema(description = "Java version used to execute the test", examples = { "21.0.1" })
    public String javaVersion = System.getProperty("java.version");

    @Schema(description = "Resolved IP address of the target", examples = { "8.8.8.8" })
    public String resolvedIpAddress;

    @Schema(description = "Time taken for DNS lookup in milliseconds", examples = { "12.5" })
    public double dnsLookupTimeMs = 0.0;
}