package com.observe.os1.auth;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.security.*;

@ApplicationScoped
public class RsaKeyProvider {

    private KeyPair keyPair;

    @PostConstruct
    public void init() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(4096); // 4096 bits key size
            this.keyPair = keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate RSA key pair", e);
        }
    }

    public PrivateKey getPrivateKey() {
        if (keyPair == null) {
            throw new IllegalStateException("Key pair not initialized");
        }
        return keyPair.getPrivate();
    }

    public PublicKey getPublicKey() {
        if (keyPair == null) {
            throw new IllegalStateException("Key pair not initialized");
        }
        return keyPair.getPublic();
    }

    public String getPublicKeyPem() {
        if (keyPair == null) {
            throw new IllegalStateException("Key pair not initialized");
        }
        return "-----BEGIN PUBLIC KEY-----\n" +
                java.util.Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()) +
                "\n-----END PUBLIC KEY-----";
    }

    public String getPrivateKeyPem() {
        if (keyPair == null) {
            throw new IllegalStateException("Key pair not initialized");
        }
        return "-----BEGIN PRIVATE KEY-----\n" +
                java.util.Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()) +
                "\n-----END PRIVATE KEY-----";
    }
}
