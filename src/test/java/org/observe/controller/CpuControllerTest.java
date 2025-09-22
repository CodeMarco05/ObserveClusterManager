package org.observe.controller;

import com.observe.openapi.model.CpuUsage;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class CpuControllerTest {

    @Inject
    CpuController cpuController;

    @Test
    void testGivesBackInfoAboutTheCurrentCpuUsage() {
        // When
        CpuUsage actualCpuUsage = cpuController.givesBackInfoAboutTheCurrentCpuUsage();

        // Then
        assertNotNull(actualCpuUsage);
    }
}