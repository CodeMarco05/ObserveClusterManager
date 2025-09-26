package org.observe.controller;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class GeneralControllerTest {

    @Inject
    GeneralController generalController;


    @Test
    void uptimeInSeconds() {
        Integer result = generalController.uptimeInSeconds();

        Log.info(result);
    }
}
