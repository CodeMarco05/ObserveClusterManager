package org.observe.controller;

import com.observe.openapi.api.HealthCheckApi;
import com.observe.openapi.model.HealthResponse;
import io.swagger.annotations.Api;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.observe.auth.ApiKeySecured;
import org.observe.rest.client.PrometheusResponse;
import org.observe.rest.client.PrometheusRestClient;
import org.observe.rest.client.Queries;
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
