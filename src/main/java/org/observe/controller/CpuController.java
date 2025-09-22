package org.observe.controller;

import com.observe.openapi.api.CpuApi;
import com.observe.openapi.model.CpuUsage;
import jakarta.inject.Inject;
import org.observe.service.CpuService;

import java.util.List;

public class CpuController implements CpuApi {

    @Inject
    CpuService cpuService;

    @Override
    public CpuUsage givesBackInfoAboutTheCurrentCpuUsage() {
        return cpuService.getCpuUsageNow();
    }

    @Override
    public List<CpuUsage> usageOverTime(Integer startTime, Integer endTime) {
        return List.of();
    }
}
