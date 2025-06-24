package com.observe.os1;

import com.observe.os1.models.Client;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {


    @GET
    public List<Client> getAllUsers() {
        return Client.listAll();
    }

    @POST
    @Transactional
    public Response createUser(Client user) {
        user.persist();
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long id) {
        Client client = Client.findById(id);
        if (client == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(client).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        boolean deleted = Client.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}