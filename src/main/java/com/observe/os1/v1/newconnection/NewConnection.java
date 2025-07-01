package com.observe.os1.v1.newconnection;

import com.observe.os1.models.NewConnectionModel;
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

import javax.print.attribute.standard.Media;

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
            description = "Only callable from localhost. Generates a new temporary password for a connection and saves it in the database with a timestamp."
    )
    @APIResponse(
            responseCode = "200",
            description = "Erfolgreiche Erstellung",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = String.class),
                    example = "\"abC1-xyZ7\""
            )
    )
    @APIResponse(
            responseCode = "403",
            description = "Nicht erlaubt: nur localhost",
            content = @Content(schema = @Schema(type = SchemaType.STRING), example = "\"Zugriff verweigert: Nur lokale Verbindungen sind erlaubt.\"")
    )
    @APIResponse(
            responseCode = "500",
            description = "Interner Serverfehler",
            content = @Content(schema = @Schema(type = SchemaType.STRING), example = "\"Fehler beim Speichern der Verbindung: ...\"")
    )
    public Response createNewTemporaryConnection(@Context ContainerRequestContext request) {

        //filter if request comes from localhost
        String remoteAddress = request.getHeaderString("X-Forwarded-For");
        if (remoteAddress == null){
            remoteAddress = request.getUriInfo().getRequestUri().getHost();
        }

        if (!isLocal(remoteAddress)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Zugriff verweigert: Nur lokale Verbindungen sind erlaubt.")
                    .build();
        }

        // build new temporary password
        String tmpPassword = TmpPasswordGenerator.genPassword(4, 3);

        //save the temporary password in the database with the timestamp
        NewConnectionModel newConnectionModel = new NewConnectionModel();
        newConnectionModel.setTimestamp(new java.sql.Timestamp(System.currentTimeMillis()));
        newConnectionModel.setTemporaryPassword(tmpPassword);

        //save the new connection model
        try {
            newConnectionModel.persist();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Fehler beim Speichern der Verbindung: " + e.getMessage())
                    .build();
        }

        // return the temporary password as json response
        return  Response.ok(newConnectionModel).build();
    }

    private boolean isLocal(String ip) {
        return ip.equals("127.0.0.1") || ip.equals("::1") || ip.startsWith("172.") || ip.equals("localhost") || ip.equals("0:0:0:0:0:0:0:1");
    }
}
