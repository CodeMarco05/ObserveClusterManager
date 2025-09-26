package org.observe.controller;

import com.observe.openapi.api.MemoryApi;
import com.observe.openapi.model.Memory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.observe.auth.ApiKeySecured;
import org.observe.service.MemoryService;

import java.util.List;

@ApplicationScoped
@ApiKeySecured
public class MemoryController implements MemoryApi {

    @Inject
    MemoryService memoryService;

    @Override
    public List<Memory> memoryUsageInGb(Integer startTime, Integer endTime, Integer step) {
        return memoryService.getMemoryUsageInGb(startTime, endTime, step);
    }

    @Override
    public List<Memory> totalMemoryInGb(Integer startTime, Integer endTime, Integer step) {
        return memoryService.getTotalMemoryInGb(startTime, endTime, step);
    }
}
