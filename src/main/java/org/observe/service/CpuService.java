package org.observe.service;

import com.observe.openapi.model.CpuUsage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.observe.rest.client.CpuClient;
import org.observe.rest.client.PrometheusResponse;

@ApplicationScoped
public class CpuService {

    @Inject
    @RestClient
    CpuClient cpuClient;

    public CpuUsage getCpuUsageNow() {
        PrometheusResponse json = cpuClient.getCpuUsage(
                "avg by (instance,mode) (rate(node_cpu_seconds_total{mode!='idle'}[5s]))",
                System.currentTimeMillis() / 1000 + "",
                System.currentTimeMillis() / 1000 + "",
                "10"
        );

        return null;
    }
}
