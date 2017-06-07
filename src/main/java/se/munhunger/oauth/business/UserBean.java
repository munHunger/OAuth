package se.munhunger.oauth.business;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.*;
import org.springframework.stereotype.Component;
import se.munhunger.oauth.model.token.NonceToken;
import se.munhunger.oauth.model.user.User;
import se.munhunger.oauth.model.user.UserData;
import se.munhunger.oauth.model.user.UserRoles;
import se.munhunger.oauth.util.database.jpa.Database;
import se.munhunger.oauth.util.jwt.JSONWebToken;
import se.munhunger.oauth.util.oauth.OAuthUtils;
import se.munhunger.oauth.util.properties.Settings;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.*;

/**
 * Created by Marcus Münger on 2017-05-18.
 */
@Path("/oauth")
@Api(value = "OAuth", description = "Endpoints for managing users")
@Component
public class UserBean
{
	@GET
	@Path("/user")
	@RolesAllowed({"USER"})
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Gets the information about the logged in user",
				  notes = "Gets all the saved information about the logged in user")
	@ApiResponses(
			value = {@ApiResponse(code = HttpServletResponse.SC_OK, message = "User was created")})
	public Response getUserData(
			@HeaderParam("Authorization")
					String accessToken) throws Exception
	{
		if(accessToken == null)
			return Response.status(HttpServletResponse.SC_UNAUTHORIZED).build();
		if(accessToken.toUpperCase().startsWith("BEARER "))
			accessToken = accessToken.substring("BEARER ".length());
		if(!JSONWebToken.isSigned(accessToken))
			accessToken = OAuthUtils.convertToAccessToken(accessToken).accessToken;

		Claims claims;
		claims = JSONWebToken.decryptToken(Settings.getStringSetting("jwt_key"), accessToken);
		UserData user = new UserData();
		user.roles = Arrays.asList(claims.getSubject().split(";"));
		user.username = claims.get("user").toString();
		user.phoneNumber = claims.get("number").toString();
		return Response.ok(user).build();
	}

	@POST
	@Path("/user")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Create a new user",
				  notes = "Creates a new user at the current OAuth service. All registered clients can create a new user")
	@ApiResponses(
			value = {@ApiResponse(code = HttpServletResponse.SC_NO_CONTENT, message = "User was created"), @ApiResponse(
					code = HttpServletResponse.SC_CONFLICT, message = "User already exists")})
	public Response createUser(
			@ApiParam(value = "The URL to redirect to after done.",
					  example = "http://localhost:9090/swagger")
			@FormParam("redirect_uri")
					String redirectUri,
			@ApiParam(
					value = "The identification of the new user. Note that there are not restrictions on minimum length",
					required = true, defaultValue = "dudeMaster43")
			@FormParam("username")
					String username,
			@ApiParam(
					value = "The password of the new user. This will be hashed in the database. Note that there are no restrictions on complexity or length",
					required = true,
					defaultValue = "CorrectHorseBatteryStaple")
			@FormParam("password")
					String password,
			@ApiParam(value = "The identification of the client that is creating the user", required = true,
					  defaultValue = "24MCCM7d9gfg7s8dfg798dg")
			@FormParam("client_id")
					String clientID,
			@ApiParam(value = "The phonenumber of the user to be registered", required = true)
			@FormParam("number")
					String number) throws Exception
	{
		Map<String, Object> param = new HashMap<>();
		param.put("username", username);
		boolean isUserAvailable = Database.getObjects("from User WHERE username = :username", param).isEmpty();

		param = new HashMap<>();
		param.put("id", clientID);
		boolean clientExists = !Database.getObjects("from RegisteredClient WHERE clientID = :id", param).isEmpty();
		if(isUserAvailable && clientExists)
		{
			User newUser = new User();
			newUser.username = username;
			newUser.password = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password);
			newUser.number = number;
			List objects = new ArrayList<>();
			objects.add(newUser);
			objects.add(new UserRoles(newUser.username, "USER"));
			Database.saveObjects(objects);
			return Response.status(HttpServletResponse.SC_FOUND).location(new URI(redirectUri))
					.build();
		}
		else
			return Response.status(HttpServletResponse.SC_CONFLICT)
					.header("nonce", NonceToken.generateToken().token)
					.build();
	}
}
