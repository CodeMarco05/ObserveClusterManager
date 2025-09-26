package org.observe.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.observe.rest.client.PrometheusResponse;
import org.observe.rest.client.PrometheusRestClient;
import org.observe.rest.client.Queries;

@ApplicationScoped
public class GeneralService {

    @Inject
    @RestClient
    PrometheusRestClient prometheusRestClient;

    public Integer getUptimeInSeconds() {
        Response json = prometheusRestClient.getCpuUsage(
                Queries.UPTIME_IN_SECONDS.getQuery(),
                System.currentTimeMillis() / 1000 + "",
                System.currentTimeMillis() / 1000 + "",
                "10"
        );

        PrometheusResponse response = json.readEntity(PrometheusResponse.class);

        Double doubleValue = Double.parseDouble(response.getData().getResult().getFirst().getValues().getFirst().get(1));

        Integer value = doubleValue.intValue();

        return value;
    }
}
