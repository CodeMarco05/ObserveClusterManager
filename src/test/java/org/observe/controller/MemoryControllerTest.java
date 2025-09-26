package org.observe.controller;

import com.observe.openapi.model.Memory;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

@QuarkusTest
public class MemoryControllerTest {

    @Inject
    MemoryController controller;

    @Test
    void memoryUsageInGb() {
        List<Memory> result = controller.memoryUsageInGb(1758914733, 1758914833, 10);

        Log.info(result);
    }

    @Test
    void totalMemoryInGb() {
        List<Memory> result = controller.totalMemoryInGb(1758914733, 1758914833, 10);

        Log.info(result);
    }
}
