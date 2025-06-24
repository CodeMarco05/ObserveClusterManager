package com.observe.os1;

import com.observe.os1.models.Client;
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
    public Response createUser(Client user) {
        user.persist();
        return Response.status(Response.Status.CREATED).entity(user).build();
    }

    @GET
    @Path("/{id}")
    public Client getUser(@PathParam("id") String id) {
        return Client.findById(new org.bson.types.ObjectId(id));
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") String id) {
        boolean deleted = Client.deleteById(new org.bson.types.ObjectId(id));
        return deleted ? Response.noContent().build() : Response.status(404).build();
    }

}