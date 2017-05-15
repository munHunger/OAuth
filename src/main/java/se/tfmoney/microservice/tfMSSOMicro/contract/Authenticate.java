package se.tfmoney.microservice.tfMSSOMicro.contract;

import io.swagger.annotations.*;
import se.tfmoney.microservice.tfMSSOMicro.model.AuthenticationRequest;
import se.tfmoney.microservice.tfMSSOMicro.model.AuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Marcus Münger on 2017-05-12.
 */
@Path("/auth")
@Api(description = "Authenticates and validates users", value = "Authenticate")
public interface Authenticate
{
    @POST
    @Path("/authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Authenticates the user", notes = "Creates a new oauth token for the authenticated user")
    @ApiResponses(value = {@ApiResponse(code = HttpServletResponse.SC_OK, message = "Successfully authenticated",
                                        response = AuthenticationToken.class)})
    Response authenticate(
            @Context
                    HttpServletRequest request,
            @ApiParam(value = "A request for authentication containing all needed fields for auth")
                    AuthenticationRequest authRequest) throws Exception;

    @POST
    @Path("/authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Authenticates the user", notes = "Creates a new oauth token for the authenticated user")
    @ApiResponses(value = {@ApiResponse(code = HttpServletResponse.SC_OK, message = "Successfully authenticated",
                                        response = AuthenticationToken.class)})
    Response authenticate(
            @ApiParam(value = "A request for authentication containing all needed fields for auth")
                    AuthenticationRequest authRequest) throws Exception;
}
