package org.observe.service;

import com.observe.openapi.model.HealthResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.observe.rest.client.PrometheusResponse;
import org.observe.rest.client.PrometheusRestClient;
import org.observe.rest.client.Queries;

@ApplicationScoped
public class HealthService {

    @ConfigProperty(name = "quarkus.application.version", defaultValue = "dev")
    String appVersion;


    @Inject
    @RestClient
    PrometheusRestClient restClient;

    public HealthResponse healthCheck() {
        HealthResponse healthResponse
                = new HealthResponse();
        healthResponse.setHealth("OK");
        healthResponse.setVersion(appVersion);



        try{
            // look for a prometheus connection
            Response response = restClient.universalQuery(
                    Queries.UP_QUERY.getQuery(),
                    String.valueOf(System.currentTimeMillis() / 1000),
                    String.valueOf(System.currentTimeMillis() / 1000),
                    "10"
            );

            if (response.getStatus() != 200) {
                healthResponse.setHealth("WARN");
                healthResponse.setPrometheusConnection("NOT OK");
                return healthResponse;
            } else {
                healthResponse.setPrometheusConnection("OK");
            }

            // Convert to PrometheusResponse
            PrometheusResponse prometheusResponse = response.readEntity(PrometheusResponse.class);

            // look for a node-exporter connection
            String nodeExporterStatus = "NOT OK";
            for (var result : prometheusResponse.getData().getResult()) {
                if (result.getMetric().get("job").equals("node-exporter")) {
                    nodeExporterStatus = "OK";
                    break;
                }
            }

            healthResponse.setNodeExporterConnection(nodeExporterStatus);
        } catch (Exception e) {
            throw new WebApplicationException("Error connecting to Prometheus", e, Response.Status.INTERNAL_SERVER_ERROR);
        }



        return healthResponse;
    }
}
