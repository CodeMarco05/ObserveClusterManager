package com.observe.os1.v1.connect;

import com.observe.os1.auth.UserService;
import com.observe.os1.models.ClientModel;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v1/connect/new-client")
public class NewClientResource {

    @Inject
    UserService userService;

    public static class ClientRequest {
        public String name;
        public String password;
        public String roles; // Comma-separated list of roles
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createNewTemporaryConnection(ClientRequest newClient) {
        //validate the input
        if (newClient == null || newClient.name == null || newClient.password == null || newClient.roles == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Name, password, and roles cannot be null")
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .build();
        }

        //check if the client already exists
        try {
            ClientModel clientModel = userService.registerClient(newClient.name, newClient.password, newClient.roles);

            ClientRequest response = new ClientRequest();
            response.name = clientModel.name;
            response.roles = clientModel.roles;
            response.password = "********"; // Do not return the password in the response

            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .build();
        }catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error during saving of new client: " + e.getMessage())
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .build();
        }
    }
}
