package com.observe.os1.auth;

import com.observe.os1.models.ClientModel;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Duration;

@ApplicationScoped
public class JwtTokenGenerator {

    @Inject
    RsaKeyProvider keyProvider;

    private static final String ISSUER = "observe-os1"; // TODO add the reals issuers name

    public String generateToken(ClientModel client) {
        if (client == null) {
            throw new IllegalArgumentException("Client or Client ID cannot be null");
        }

        return Jwt.issuer(ISSUER)
                .subject(client.name)
                .groups(client.getRoles())
                .expiresIn(Duration.ofHours(24))
                .jws()
                .sign(keyProvider.getPrivateKey());
    }
}
