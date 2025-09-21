package com.observe.os1.v1.occconect;

import com.observe.os1.models.ClusterClient;
import io.quarkus.logging.Log;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.openapi.internal.models.examples.Example;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import javax.management.Query;

@Path("/v1/occconector")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class Connector {

    @Inject
    TokenManager tokenManager;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response newConnection(
            @QueryParam("name")
            @NotNull
            @Parameter(
                    description = "Name of the client you want to connect",
                    example = "my-client"
            )
            String name,

            @QueryParam("hostOrIp")
            @NotNull
            @Parameter(
                    description = "Hostname or IP address of the client you want to connect",
                    examples = {
                            @ExampleObject(
                                    description = "Example IP address",
                                    value = "100.4.2.1"
                            ),
                            @ExampleObject(
                                    description = "Example hostname",
                                    value = "my-client.local"
                            )
                    }
            )
            String hostOrIp,

            @QueryParam("port")
            @NotNull
            @Parameter(
                    description = "Port number of the client you want to connect",
                    example = "8080"
            )
            int port,

            @QueryParam("client-token")
            @NotNull
            @Parameter(
                    description = "Token for authentication",
                    example = "somerandomtoken12345"
            )
            String clientToken,

            @QueryParam("observe-token")
            @NotNull
            @Parameter(
                    description = "Token for authentication",
                    example = "somerandomtoken12345"
            )
            String observeToken
    ) {
        //verify the observe token
        String token = tokenManager.loadToken();
        if (!token.equals(observeToken)) {
            Log.warn("Invalid observe token provided for new connection: " + name);
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid observe token").build();
        }

        // check passed and now creating the new connection
        Log.info("Creating new connection: " + name + " at " + hostOrIp + ":" + port);

        new ClusterClient(name, hostOrIp, port, clientToken).createClient();
        return Response.ok().build();
    }

}
