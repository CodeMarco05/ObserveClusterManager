package com.observe.os1.v1.connect;

import com.observe.os1.auth.JwtTokenGenerator;
import com.observe.os1.auth.ClientService;
import com.observe.os1.models.ClientModel;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v1/connect/auth")
public class AuthResource {

    @Inject
    ClientService clientService;

    @Inject
    JwtTokenGenerator jwtTokenGenerator;

    public static class LoginRequest {
        public String name;
        public String password;
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(LoginRequest loginRequest) {
        ClientModel client = clientService.authenticate(loginRequest.name, loginRequest.password);

        // Check if the client arguments given are valid for a user
        if (client == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // Generate JWT token for the authenticated client
        String token = jwtTokenGenerator.generateToken(client);
        return Response.ok(token)
                .header("Authorization", "Bearer " + token)
                .build();
    }
}
