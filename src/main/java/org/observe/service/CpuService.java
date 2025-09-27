package org.observe.service;


import com.observe.openapi.model.CpuUsage;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.observe.rest.client.PrometheusRestClient;
import org.observe.rest.client.PrometheusResponse;
import org.observe.rest.client.Queries;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CpuService {

    @Inject
    @RestClient
    PrometheusRestClient prometheusRestClient;

    public CpuUsage getCpuUsageNow() {
        Response json = prometheusRestClient.universalQuery(
                Queries.CPU_USAGE.getQuery(),
                System.currentTimeMillis() / 1000 + "",
                System.currentTimeMillis() / 1000 + "",
                "10"
        );

        Log.info(json.toString());

        PrometheusResponse response = json.readEntity(PrometheusResponse.class);

        Log.info(response.toString());

        CpuUsage cpuUsage = new CpuUsage();
        cpuUsage.setUsageInPercent("0");
        cpuUsage.setUnixTime(response.getFirstTimestamp());

        response.getData().getResult().forEach(result -> {
            result.getValues().forEach(value -> {
                double valueBefore = Double.parseDouble(cpuUsage.getUsageInPercent());
                double valueToAdd = Double.parseDouble(value.get(1));

                double newValue = valueBefore + valueToAdd;

                cpuUsage.setUsageInPercent(String.valueOf(newValue));
            });
        });


        return cpuUsage;
    }

    public List<CpuUsage> getCpuUsageOverTime(Integer startTime, Integer endTime, Integer step) {
        Response json = prometheusRestClient.universalQuery(
                Queries.CPU_USAGE.getQuery(),
                startTime.toString(),
                endTime.toString(),
                step.toString()
        );

        PrometheusResponse response = json.readEntity(PrometheusResponse.class);

        Log.info(response.toString());

        List<CpuUsage> cpuUsages = new ArrayList<>();

        for (int i = 0; i < response.getData().getResult().getFirst().getValues().toArray().length; i++) {
            CpuUsage cpuUsage = new CpuUsage();
            cpuUsage.setUnixTime(Integer.valueOf(response.getData().getResult().getFirst().getValues().get(i).getFirst()));

            int finalI = i;
            response.getData().getResult().forEach(result -> {
                double valueToAdd = Double.parseDouble(result.getValues().get(finalI).get(1));

                double newValue = Double.parseDouble(cpuUsage.getUsageInPercent() == null ? "0" : cpuUsage.getUsageInPercent()) + valueToAdd;

                cpuUsage.setUsageInPercent(String.valueOf(newValue));

            });
            cpuUsages.add(cpuUsage);
        }


        return cpuUsages;
    }
}
