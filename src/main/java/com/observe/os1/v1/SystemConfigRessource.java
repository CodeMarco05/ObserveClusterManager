package com.observe.os1.v1;

import com.observe.os1.SystemConfigLoader;
import com.observe.os1.dtos.CpuMonitoringDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;

@Path("/v1/system-config")
public class SystemConfigRessource {

    @Inject
    SystemConfigLoader loader;

    @GET
    @Path("/cpu-monitoring")
    @Produces(MediaType.APPLICATION_JSON)
    public CpuMonitoringDTO getCpuMonitoring() {
        System.out.println(loader.cpuMonitoring());
        return loader.cpuMonitoring()
                .map(cpu -> new CpuMonitoringDTO(
                        cpu.enabled().orElse(null),
                        cpu.cpuUsageFile().orElse(null),
                        cpu.interval().map(Duration::toString).orElse(null) // z.B. "PT5S"
                ))
                .orElse(null); // falls nicht vorhanden, gibt null zurück
    }
}
