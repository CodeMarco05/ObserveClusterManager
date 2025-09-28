package org.observe.service;

import com.observe.openapi.api.DiskApi;
import com.observe.openapi.model.DiskStatistic;
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
public class DiskService {

    @Inject
    @RestClient
    PrometheusRestClient prometheusRestClient;

    public List<DiskStatistic> diskStatistic(Integer startTime, Integer endTime, Integer step) {
        Response diskSizeResponse = prometheusRestClient.universalQuery(
                Queries.TOTAL_DISK_SIZE_IN_GB_FOR_ALL_SYSTEMS_SUMMED_UP.getQuery(),
                startTime.toString(),
                endTime.toString(),
                step.toString()
        );

        Response diskUsageResponse = prometheusRestClient.universalQuery(
                Queries.TOTAL_DISK_USAGE_IN_GB_FOR_ALL_SYSTEMS_SUMMED_UP.getQuery(),
                startTime.toString(),
                endTime.toString(),
                step.toString()
        );

        PrometheusResponse diskSize = diskSizeResponse.readEntity(PrometheusResponse.class);
        PrometheusResponse diskUsage = diskUsageResponse.readEntity(PrometheusResponse.class);

        List<DiskStatistic> diskStatistics = new ArrayList<>();

        if (diskSize.getData().getResult().size() != diskUsage.getData().getResult().size()){
            throw new WebApplicationException("Queries returned different number of results", Response.Status.INTERNAL_SERVER_ERROR);
        }

        for(int i = 0; i < diskSize.getData().getResult().getFirst().getValues().size(); i++){
            DiskStatistic diskStat = new DiskStatistic();
            diskStat.setUnixTime(Integer.parseInt(diskSize.getData().getResult().getFirst().getValues().get(i).getFirst()));
            diskStat.setTotalAvailableSpaceAllDisksInGb(diskSize.getData().getResult().getFirst().getValues().get(i).get(1));
            diskStat.setTotalUsedSpaceAllDisksInGb(diskUsage.getData().getResult().getFirst().getValues().get(i).get(1));

            diskStatistics.add(diskStat);
        }
        return diskStatistics;
    }
}
