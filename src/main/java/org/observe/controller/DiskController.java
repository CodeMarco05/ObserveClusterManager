package org.observe.controller;

import com.observe.openapi.api.DiskApi;
import com.observe.openapi.model.DiskStatistic;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.observe.service.DiskService;

import java.util.List;

@ApplicationScoped
public class DiskController implements DiskApi {

    @Inject
    DiskService diskService;

    @Override
    public List<DiskStatistic> diskStatistic(Integer startTime, Integer endTime, Integer step) {
        return diskService.diskStatistic(startTime, endTime, step);
    }
}
