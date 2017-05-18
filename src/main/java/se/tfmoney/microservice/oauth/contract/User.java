package se.tfmoney.microservice.oauth.contract;

import io.swagger.annotations.*;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Marcus Münger on 2017-05-18.
 */
@Path("/oauth/user")
@Api(value = "User", description = "Endpoints for managing users")
public interface User
{
    //TODO: Does this create enumeration attacks?
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create a new user",
                  notes = "Creates a new user at the current OAuth service. All registered clients can create a new user")
    @ApiResponses(
            value = {@ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = "User was created"), @ApiResponse(
                    code = HttpServletResponse.SC_CONFLICT, message = "User already exists")})
    Response createUser(
            @ApiParam(
                    value = "The identification of the new user. Note that there are not restrictions on minimum length",
                    required = true, defaultValue = "dudeMaster43")
            @QueryParam("username")
                    String username,
            @ApiParam(
                    value = "The password of the new user. This will be hashed in the database. Note that there are no restrictions on complexity or length",
                    required = true,
                    defaultValue = "CorrectHorseBatteryStaple")
            @QueryParam("password")
                    String password,
            @ApiParam(value = "The identification of the client that is creating the user", required = true,
                      defaultValue = "24MCCM7d9gfg7s8dfg798dg")
            @QueryParam("client_id")
                    String clientID) throws Exception;
}
