package org.observe.controller;

import com.observe.openapi.model.Network;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.List;

@QuarkusTest
public class NetworkControllerTest {

    @Inject
    NetworkController networkController;

    @Test
    void testNetworkIn() {
        List<Network> networkList = networkController.networkIn(1758912218, 1758912799, 10);

        Log.info(networkList);
    }

    @Test
    void testNetworkOut() {
        List<Network> networkList = networkController.networkOut(1758912218, 1758912799, 10);

        Log.info(networkList);
    }
}
