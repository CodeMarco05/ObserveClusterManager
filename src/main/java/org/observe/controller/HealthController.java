package org.observe.controller;

import com.observe.openapi.api.HealthCheckApi;
import com.observe.openapi.model.HealthResponse;
import io.swagger.annotations.Api;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.observe.auth.ApiKeySecured;

@ApplicationScoped
@ApiKeySecured
public class HealthController implements HealthCheckApi {

    @ConfigProperty(name = "quarkus.application.version", defaultValue = "dev")
    String appVersion;

    @Override
    public HealthResponse givesBackAnAnswerForTheApplicationHealth() {
        HealthResponse response = new HealthResponse();
        response.setHealth("OK");
        response.setVersion(appVersion);
        return response;
    }
}
