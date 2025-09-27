package org.observe.controller;

import com.observe.openapi.api.HealthCheckApi;
import com.observe.openapi.model.HealthResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.observe.auth.ApiKeySecured;
import org.observe.service.HealthService;

@ApiKeySecured
@ApplicationScoped
public class HealthController implements HealthCheckApi {

    @Inject
    HealthService healthService;

    @Override
    public HealthResponse healthCheck() {
        return healthService.healthCheck();
    }
}
