package com.observe.os1;

import com.observe.os1.models.ClientModel;
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
    public List<ClientModel> getAllUsers() {
        return ClientModel.listAll();
    }

    @POST
    @Transactional
    public Response createUser(ClientModel user) {
        user.persist();
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long id) {
        ClientModel clientModel = ClientModel.findById(id);
        if (clientModel == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(clientModel).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteUser(@PathParam("id") Long id) {
        boolean deleted = ClientModel.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}