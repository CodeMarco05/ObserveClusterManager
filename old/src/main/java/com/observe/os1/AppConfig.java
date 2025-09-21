package com.observe.os1;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "app")
public interface AppConfig {
    Prometheus prometheus();

    interface Prometheus {
        @WithName("base-url")
        @WithDefault("http://localhost:9090")
        String baseUrl();
    }
}
