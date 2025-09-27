package org.observe.service;

import com.observe.openapi.model.Memory;
import io.quarkus.logging.Log;
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
public class MemoryService {

    @Inject
    @RestClient
    PrometheusRestClient prometheusRestClient;

    public List<Memory> getMemoryUsageInGb(Integer startTime, Integer endTime, Integer step) {
        Response json = prometheusRestClient.universalQuery(
                Queries.RAM_USAGE_IN_GB.getQuery(),
                startTime.toString(),
                endTime.toString(),
                step.toString()
        );

        PrometheusResponse response = json.readEntity(PrometheusResponse.class);

        List<Memory> memoryList = mapResponseToMemoryList(response);

        Log.info(response.toString());
        return memoryList;
    }

    public List<Memory> getTotalMemoryInGb(Integer startTime, Integer endTime, Integer step) {
        Response json = prometheusRestClient.universalQuery(
                Queries.TOTAL_RAM_IN_GB.getQuery(),
                startTime.toString(),
                endTime.toString(),
                step.toString()
        );

        PrometheusResponse response = json.readEntity(PrometheusResponse.class);

        List<Memory> memoryList = mapResponseToMemoryList(response);

        Log.info(response.toString());
        return memoryList;
    }

    private List<Memory> mapResponseToMemoryList(PrometheusResponse response) {
        List<Memory> memoryList = new ArrayList<>();

        response.getData().getResult().getFirst().getValues().forEach(value -> {
            Memory memory = new Memory();
            memory.setUnixTime(Integer.parseInt(value.getFirst()));
            memory.setValue(value.get(1));

            memoryList.add(memory);
        });

        return memoryList;
    }
}
