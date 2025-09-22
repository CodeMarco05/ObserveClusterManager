package org.observe.controller;

import com.observe.openapi.api.HealthCheckApi;
import com.observe.openapi.model.HealthResponse;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

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
