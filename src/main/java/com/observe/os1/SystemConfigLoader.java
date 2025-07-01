package com.observe.os1;

import io.smallrye.config.ConfigMapping;

import java.time.Duration;
import java.util.Optional;

@ConfigMapping(prefix = "observe")
public interface SystemConfigLoader {
    Optional<CpuMonitoring> cpuMonitoring();

    interface CpuMonitoring {
        Optional<Boolean> enabled();
        Optional<String> cpuUsageFile();
        Optional<Duration> interval(); // unterstützt Formate wie "5s", "1m"
    }
}
