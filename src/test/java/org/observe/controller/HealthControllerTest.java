package org.observe.controller;

import com.observe.openapi.model.HealthResponse;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class HealthControllerTest {

    @Inject
    HealthController healthController;

    @Test
    void testHealthEndpoint() {
        HealthResponse response = healthController.healthCheck();
        Log.info(response.toString());
    }
}
