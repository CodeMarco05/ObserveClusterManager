package com.observe.os1.systemDataControllers;

import com.observe.os1.models.SystemInfoModel;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class SystemInfoModelControllerTest {

    @Inject
    SystemInfoController controller;

    @BeforeEach
    @Transactional
    public void clearDb() {
        SystemInfoModel.deleteAll();
    }


    // Test if SystemInfo entry is created on startup
    @Test
    @Transactional
    public void testSystemInfoEntryExists() {
        controller.onStartup(null); // simulate startup event

        SystemInfoModel infoObj = SystemInfoModel.findAll().firstResult();

        assertNotNull(infoObj, "SystemInfo should have been created on startup");
        assertNotNull(infoObj.startDateTime, "Start time should be set");

        // TODO temporary user should be created on first startup

    }

    // Test 3 startup events
    @Test
    @Transactional
    public void testThreeStartupSimulations() {
        controller.onStartup(null); // simulate first startup
        controller.onStartup(null); // simulate second startup
        controller.onStartup(null); // simulate third startup

        long count = SystemInfoModel.count();
        assertEquals(3, count, "Should have 3 startup records");
    }
}
