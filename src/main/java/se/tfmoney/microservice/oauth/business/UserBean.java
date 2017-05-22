package se.tfmoney.microservice.oauth.business;

import io.swagger.annotations.*;
import org.springframework.stereotype.Component;
import se.tfmoney.microservice.oauth.model.user.UserRoles;
import se.tfmoney.microservice.oauth.util.database.jpa.Database;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marcus Münger on 2017-05-18.
 */
@Path("/oauth")
@Api(value = "OAuth", description = "Endpoints for managing users")
@Component
public class UserBean
{
    @POST
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create a new user",
                  notes = "Creates a new user at the current OAuth service. All registered clients can create a new user")
    @ApiResponses(
            value = {@ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = "User was created"), @ApiResponse(
                    code = HttpServletResponse.SC_CONFLICT, message = "User already exists")})
    public Response createUser(
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
                    String clientID) throws Exception
    {
        Map<String, Object> param = new HashMap<>();
        param.put("username", username);
        boolean isUserAvailable = Database.getObjects("from User WHERE username = :username", param).isEmpty();

        param = new HashMap<>();
        param.put("id", clientID);
        boolean clientExists = !Database.getObjects("from RegisteredClient WHERE clientID = :id", param).isEmpty();
        if (isUserAvailable && clientExists)
        {
            se.tfmoney.microservice.oauth.model.user.User newUser = new se.tfmoney.microservice.oauth.model.user.User();
            newUser.username = username;
            newUser.password = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password);
            List objects = new ArrayList<>();
            objects.add(newUser);
            objects.add(new UserRoles(newUser.username, "USER"));
            Database.saveObjects(objects);
            return Response.status(HttpServletResponse.SC_NO_CONTENT).build();
        }
        else
            return Response.status(HttpServletResponse.SC_CONFLICT).build();
    }
}
