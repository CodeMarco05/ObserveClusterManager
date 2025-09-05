package com.observe.os1.v1.occconect;

import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;

@ApplicationScoped
public class TokenManager {

    @Inject
    @ConfigProperty(name = "my.occ.connect-file-path", defaultValue = "./connect-file/observe-connect-token")
    String connectFilePath;

    @Inject
    @ConfigProperty(name = "my.occ.token-length", defaultValue = "256")
    int tokenLength;

    void tokenFileGeneration(@Observes StartupEvent ev) {
        Log.info("Generating token using connect file path: " + connectFilePath);

        Path path = Paths.get(connectFilePath);
        try {
            // check if the file exists
            if (Files.exists(path)){
                Log.info("Token file already exists: " + connectFilePath + " - skipping creation");
            }else {
                // create the file and its parent directories if they do not exist
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            }
        } catch (IOException e) {
            Log.error("Failed to create the config file for occ: " + connectFilePath, e);
            throw new RuntimeException(e);
        }

        // if there is already a file check if it is empty or not
        try {
            if (Files.size(path) == 0) {
                // generate a random token
                Log.info("Generating new token of length: " + tokenLength);
                String token = generateLongToken(tokenLength);

                //write the token to the file
                Files.write(path, Collections.singletonList(token));
                Log.info("Token written to file: " + connectFilePath);
            }


        }catch (Exception e) {
            Log.error("Failed to read the config file for occ: " + connectFilePath, e);
            throw new RuntimeException(e);
        }
    }

    private String generateLongToken(int lengthInBytes) {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[lengthInBytes];
        random.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    // Method to load the token from file and return it
    public String loadToken() {
        try {
            Path path = Paths.get(connectFilePath);
            if (!Files.exists(path) || Files.size(path) == 0) {
                Log.error("Token file does not exist or is empty: " + connectFilePath);
                return null;
            }

            String token = Files.readAllLines(path).getFirst().trim();
            Log.info("Token loaded successfully from file");
            return token;

        } catch (IOException e) {
            Log.error("Failed to load token from file: " + connectFilePath, e);
            throw new RuntimeException("Could not load token", e);
        }
    }

}
