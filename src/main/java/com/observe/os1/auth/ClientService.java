package com.observe.os1.auth;

import com.observe.os1.models.ClientModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class ClientService {

    @Transactional
    public ClientModel registerClient(String name, String passwordHash, String roles){
        if (name == null || passwordHash == null || roles == null) {
            throw new IllegalArgumentException("Name, password, and roles cannot be null");
        }

        if(ClientModel.findByName(name) != null) {
            throw new IllegalArgumentException("Client with this name already exists");
        }

        // check the format of roles
        if (!roles.matches("^[a-zA-Z,]+$")) {
            throw new IllegalArgumentException("Roles must be a comma-separated list of alphabetic characters");
        }

        ClientModel client = new ClientModel();
        client.name = name;
        client.passwordHash = passwordHash;
        client.roles = roles;
        client.persistAndFlush();

        return client;
    }

    public ClientModel authenticate(String name, String plainPassword) {
        if (name == null || plainPassword == null) {
            throw new IllegalArgumentException("Name and password cannot be null");
        }

        ClientModel client = ClientModel.findByName(name);
        if (client == null || !BCrypt.checkpw(plainPassword, client.passwordHash)) {
            return null; // Authentication failed
        }

        return client; // Authentication successful
    }
}
