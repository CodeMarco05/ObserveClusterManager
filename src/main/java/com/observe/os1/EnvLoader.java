package com.observe.os1;


import io.github.cdimascio.dotenv.Dotenv;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

@Singleton
@Startup
public class EnvLoader {

    @PostConstruct
    void init() {
        String envPath = System.getenv("ENV_PATH_FOR_OBSERVE"); // Read from shell

        if (envPath == null || envPath.isEmpty()) {
            throw new IllegalStateException("ENV_PATH_FOR_OBSERVE environment variable is not set in the shell environment.");
        }

        Dotenv dotenv = Dotenv.configure()
                .filename(new java.io.File(envPath).getName())
                .directory(new java.io.File(envPath).getParent())  // Optional if not in project root
                .ignoreIfMissing()
                .load();

        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue()));
    }
}
