package org.observe.service;

import com.observe.openapi.model.Network;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.observe.rest.client.PrometheusResponse;
import org.observe.rest.client.PrometheusRestClient;
import org.observe.rest.client.Queries;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class NetworkService {

    @Inject
    @RestClient
    PrometheusRestClient prometheusRestClient;

    public List<Network> getNetworkIn(Integer startTime, Integer endTime, Integer step) {
        Response json = prometheusRestClient.getCpuUsage(
                Queries.NETWORK_IN_IN_BYTES_PER_SECOND.getQuery(),
                startTime.toString(),
                endTime.toString(),
                step.toString()
        );

        PrometheusResponse response = json.readEntity(PrometheusResponse.class);

        List<Network> networkList = new ArrayList<>();

        response.getData().getResult().getFirst().getValues().forEach(network -> {
            Network net = new Network();
            net.setUnixTime(Integer.parseInt(network.getFirst()));
            double doubleValue = Double.parseDouble(network.get(1));

            net.setValue((int) doubleValue);
            networkList.add(net);
        });

        return networkList;
    }

    public List<Network> getNetworkOut(Integer startTime, Integer endTime, Integer step) {
        Response json = prometheusRestClient.getCpuUsage(
                Queries.NETWORK_OUT_IN_BYTES_PER_SECOND.getQuery(),
                startTime.toString(),
                endTime.toString(),
                step.toString()
        );

        PrometheusResponse response = json.readEntity(PrometheusResponse.class);

        List<Network> networkList = new ArrayList<>();

        response.getData().getResult().getFirst().getValues().forEach(network -> {
            Network net = new Network();
            net.setUnixTime(Integer.parseInt(network.getFirst()));
            double doubleValue = Double.parseDouble(network.get(1));

            net.setValue((int) doubleValue);
            networkList.add(net);
        });

        return networkList;
    }

    public Integer pingToCloudflare() {
        try {
            String ipAddress = "1.1.1.1"; // Cloudflare DNS
            String os = System.getProperty("os.name").toLowerCase();

            ProcessBuilder processBuilder;
            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("ping", "-n", "1", "-w", "5000", ipAddress);
            } else {
                processBuilder = new ProcessBuilder("ping", "-c", "1", "-W", "5", ipAddress);
            }

            long startTime = System.currentTimeMillis();
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            long endTime = System.currentTimeMillis();

            if (exitCode == 0) {
                long latency = endTime - startTime;
                return (int) latency;
            } else {
                Log.error("Cloudflare host is not reachable via ping.");
                throw new WebApplicationException("Cloudflare host is not reachable.", Response.Status.SERVICE_UNAVAILABLE);
            }
        } catch (Exception e) {
            Log.error("Error during ping: " + e.getMessage());
            throw new WebApplicationException(e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
