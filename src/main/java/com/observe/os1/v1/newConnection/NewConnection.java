package com.observe.os1.v1.newConnection;

import com.observe.os1.models.TmpClientModel;
import com.observe.os1.utils.TmpPasswordGenerator;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;

@Path("/v1/new-connection")
public class NewConnection {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RequestBody(
            required = false,
            content = @Content(
                    mediaType = "application/json",
                    example = "{}"
            )
    )
    @Operation(
            summary = "Create a new temporary connection password",
            description = "Only callable from localhost. Generates a new temporary password for a new connection. Then you can login with the returned temporary key."
    )
    @APIResponse(
            responseCode = "200",
            description = "Success: Returns the new temporary password.",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class),
                    example = "\"abC1-xyZ7\""
            )
    )
    @APIResponse(
            responseCode = "403",
            description = "Not allowed: Only local connections are permitted.",
            content = @Content(schema = @Schema(type = SchemaType.STRING), example = "\"Access denied. Just for local access.\"")
    )
    @APIResponse(
            responseCode = "500",
            description = "Internal Server Error: An error occurred while saving the connection.",
            content = @Content(schema = @Schema(type = SchemaType.STRING), example = "\"Error during saving of temporary password: ...\"")
    )
    public Response createNewTemporaryConnection(@Context ContainerRequestContext request) {

        //filter if request comes from localhost
        String remoteAddress = request.getHeaderString("X-Forwarded-For");
        if (remoteAddress == null){
            remoteAddress = request.getUriInfo().getRequestUri().getHost();
        }

        if (!isLocal(remoteAddress)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Access denied. Just for local access.")
                    .build();
        }

        //save the temporary password in the database with the timestamp
        TmpClientModel tmpClientModel = new TmpClientModel();

        //save the new connection model
        try {
            tmpClientModel.persist();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error during saving of temporary password: " + e.getMessage())
                    .build();
        }

        // return the temporary password as json response
        return  Response.ok(tmpClientModel).build();
    }

    private boolean isLocal(String ip) {
        return ip.equals("127.0.0.1") || ip.equals("::1") || ip.startsWith("172.") || ip.equals("localhost") || ip.equals("0:0:0:0:0:0:0:1");
    }
}
