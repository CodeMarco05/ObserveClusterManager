package com.observe.os1;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;
import io.smallrye.config.WithName;

@ConfigMapping(prefix = "mockserver")
public interface MockServerConfig {

    @WithName("port")
    @WithDefault("9999")
    int port();

    @WithName("host")
    @WithDefault("localhost")
    String baseUrl();
}
