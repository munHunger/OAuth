package se.tfmoney.microservice.oauth.business;

import io.swagger.annotations.*;
import org.springframework.stereotype.Component;
import se.tfmoney.microservice.oauth.model.client.ClientRequest;
import se.tfmoney.microservice.oauth.model.client.ClientURL;
import se.tfmoney.microservice.oauth.model.client.RegisteredClient;
import se.tfmoney.microservice.oauth.model.error.ErrorMessage;
import se.tfmoney.microservice.oauth.model.token.NonceToken;
import se.tfmoney.microservice.oauth.util.database.jpa.Database;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marcus Münger on 2017-05-16.
 */
@Path("/oauth")
@Api(value = "OAuth", description = "Endpoints used by the clients for authenticating the user")
@Component
public class ClientBean
{
    @GET
    @Path("/client/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation(value = "Translate clientID", notes = "Translates a clientID into its user-friendly name")
    @ApiResponses(value = {@ApiResponse(code = HttpServletResponse.SC_OK,
                                        message = "The user-friendly name attached to the client noted by the submitted clientID"), @ApiResponse(
            code = HttpServletResponse.SC_NOT_FOUND, message = "Could not find the client noted by the clientID")})
    public Response idToName(
            @PathParam("id")
                    String clientID) throws Exception
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", clientID);
        List client = Database.getObjects("from RegisteredClient WHERE clientID = :id", params);
        if (client.isEmpty())
            return Response.status(HttpServletResponse.SC_NOT_FOUND)
                           .header("nonce", NonceToken.generateToken().token)
                           .build();
        else
            return Response.ok(((RegisteredClient) client.get(0)).clientName)
                           .header("nonce", NonceToken.generateToken().token)
                           .build();
    }

    @POST
    @Path("/client")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Registers a new client",
                  notes = "Registers a new client as part of the SSO group. Note that this is not a user, it is merely a middleware")
    @ApiResponses(value = {@ApiResponse(code = HttpServletResponse.SC_OK, message = "The new client was registered",
                                        response = RegisteredClient.class), @ApiResponse(
            code = HttpServletResponse.SC_BAD_REQUEST, message = "Could not create the client",
            response = ErrorMessage.class)})
    public Response createClient(
            @HeaderParam("nonce")
                    String nonce,
            @ApiParam(value = "The request for a new client", required = true)
                    ClientRequest request) throws Exception
    {
        if (request.clientID.length() > 16)
            return Response.status(HttpServletResponse.SC_BAD_REQUEST)
                           .entity(new ErrorMessage("Could not create client", "ClientID was too long"))
                           .header("nonce", NonceToken.generateToken())
                           .build();
        RegisteredClient client = new RegisteredClient();
        client.clientSecret = new BigInteger(64 * 5, new SecureRandom()).toString(32);
        client.clientID = request.clientID + new BigInteger((32 - request.clientID.length()) * 5,
                                                            new SecureRandom()).toString(32);
        client.clientName = request.clientName;
        client.jwtKey = request.jwtKey;

        RegisteredClient dbSafeClient = new RegisteredClient();
        dbSafeClient.clientID = client.clientID;
        dbSafeClient.clientSecret = org.apache.commons.codec.digest.DigestUtils.sha256Hex(client.clientSecret);
        dbSafeClient.clientName = request.clientName;
        dbSafeClient.jwtKey = request.jwtKey;
        try
        {
            List<Object> objectsToSave = new ArrayList<>();
            objectsToSave.add(dbSafeClient);
            for (String url : request.httpRedirects)
                objectsToSave.add(new ClientURL(client.clientID, url));

            Database.saveObjects(objectsToSave);
        } catch (Exception e)
        {
            e.printStackTrace();
            return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                           .entity(new ErrorMessage("Could not create client",
                                                    "Could not save the object in the database"))
                           .header("nonce", NonceToken.generateToken().token)
                           .build();
        }
        return Response.ok(client).header("nonce", NonceToken.generateToken().token).build();
    }
}
