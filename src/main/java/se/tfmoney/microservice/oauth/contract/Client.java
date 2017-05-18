package se.tfmoney.microservice.oauth.contract;

import io.swagger.annotations.*;
import se.tfmoney.microservice.oauth.model.client.ClientRequest;
import se.tfmoney.microservice.oauth.model.client.RegisteredClient;
import se.tfmoney.microservice.oauth.model.error.ErrorMessage;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Marcus Münger on 2017-05-16.
 */
@Path("/oauth/client")
@Api(value = "Client", description = "Endpoints used by the clients for authenticating the user")
public interface Client
{
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Registers a new client",
                  notes = "Registers a new client as part of the SSO group. Note that this is not a user, it is merely a middleware")
    @ApiResponses(value = {@ApiResponse(code = HttpServletResponse.SC_OK, message = "The new client was registered",
                                        response = RegisteredClient.class), @ApiResponse(
            code = HttpServletResponse.SC_BAD_REQUEST, message = "Could not create the client",
            response = ErrorMessage.class)})
    Response createClient(
            @ApiParam(value = "The request for a new client", required = true)
                    ClientRequest request) throws Exception;
}