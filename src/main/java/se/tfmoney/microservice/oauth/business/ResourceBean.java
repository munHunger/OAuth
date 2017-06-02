package se.tfmoney.microservice.oauth.business;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import se.tfmoney.microservice.oauth.model.token.NonceToken;
import se.tfmoney.microservice.oauth.util.annotations.NonceRequired;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Marcus Münger on 2017-05-29.
 */
@Component
@Path("/test")
@Api(value = "Auth test resources")
public class ResourceBean
{
    @GET
    @Path("/nonce")
    @Produces(MediaType.APPLICATION_JSON)
    @NonceRequired
    @PermitAll
    @ApiOperation(value = "Tests to see if nonce is required",
                  notes = "Permits all requests that have a valid nonce token")
    public Response nonceRequired(
            @HeaderParam("Authorization")
                    String accessToken,
            @HeaderParam("nonce")
                    String nonce) throws Exception
    {
        return Response.ok().header("nonce", NonceToken.generateToken().token).build();
    }

    @GET
    @Path("/deny")
    @Produces(MediaType.APPLICATION_JSON)
    @NonceRequired
    @DenyAll
    @ApiOperation(value = "Tests to see if all responses can be denied",
                  notes = "Denies all responses. even if nonce is valid")
    public Response denyRequests(
            @HeaderParam("Authorization")
                    String accessToken,
            @HeaderParam("nonce")
                    String nonce) throws Exception
    {
        return Response.ok().header("nonce", NonceToken.generateToken().token).build();
    }

    @GET
    @Path("/roles")
    @Produces(MediaType.APPLICATION_JSON)
    @NonceRequired
    @RolesAllowed({"USER"})
    @ApiOperation(
            value = "Tests to see if all requests where the user has the role \"USER\" and a valid nonce token are permitted",
            notes = "Permits all requests where user is in role \"USER\" and nonce token is valid")
    public Response roleRequest(
            @HeaderParam("Authorization")
                    String accessToken,
            @HeaderParam("nonce")
                    String nonce) throws Exception
    {
        return Response.ok().header("nonce", NonceToken.generateToken().token).build();
    }
}
