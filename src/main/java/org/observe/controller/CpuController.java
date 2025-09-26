package org.observe.controller;

import com.observe.openapi.api.CpuApi;
import com.observe.openapi.model.CpuUsage;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.observe.auth.ApiKeySecured;
import org.observe.service.CpuService;

import java.util.List;

@ApplicationScoped
@ApiKeySecured
public class CpuController implements CpuApi {

    @Inject
    CpuService cpuService;

    @Override
    public CpuUsage currentUsage() {
        return cpuService.getCpuUsageNow();
    }

    @Override
    public List<CpuUsage> usageOverTime(Integer startTime, Integer endTime, Integer step) {
        return cpuService.getCpuUsageOverTime(startTime, endTime, step);
    }
}
