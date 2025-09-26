package org.observe.controller;

import com.observe.openapi.api.NetworkApi;
import com.observe.openapi.model.Network;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.observe.service.NetworkService;

import java.util.List;

@ApplicationScoped
public class NetworkController implements NetworkApi {

    @Inject
    NetworkService networkService;

    @Override
    public List<Network> networkIn(Integer startTime, Integer endTime, Integer step) {
        return networkService.getNetworkIn(startTime, endTime, step);
    }

    @Override
    public List<Network> networkOut(Integer startTime, Integer endTime, Integer step) {
        return networkService.getNetworkOut(startTime, endTime, step);
    }
}
