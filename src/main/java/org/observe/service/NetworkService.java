package org.observe.service;

import com.observe.openapi.model.Network;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
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
}
