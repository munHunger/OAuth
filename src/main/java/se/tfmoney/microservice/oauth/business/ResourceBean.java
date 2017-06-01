package se.tfmoney.microservice.oauth.business;

import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import se.tfmoney.microservice.oauth.model.NonceToken;
import se.tfmoney.microservice.oauth.util.nonce.NonceUtils;
import se.tfmoney.microservice.oauth.util.oauth.OAuthUtils;

import javax.servlet.http.HttpServletResponse;
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
@Path("/res")
public class ResourceBean
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Authenticates a user",
                  notes = "Authenticates a user and returns either an access token or an authentication token depending on what is requested and what the client allows. If authenticated the user will be redirected to the redirect_uri with the url-pattern: {redirect_uri}(#access_token={access_token}&expires_in={time})|(?code={auth_token})")
    public Response authenticateRequest(
            @HeaderParam("Authorization")
                    String accessToken,
            @HeaderParam("nonce")
                    String nonce) throws Exception
    {
        if (OAuthUtils.isAuthenticated(accessToken) && NonceUtils.isNonceValid(nonce))
            return Response.ok(
                    "{\"authenticated\":true, \"isUser\":" + OAuthUtils.hasAnyRole(accessToken, "USER") + "}")
                           .header("nonce", NonceToken.generateToken().token)
                           .build();
        else
            return Response.status(HttpServletResponse.SC_UNAUTHORIZED)
                           .header("nonce", NonceToken.generateToken().token)
                           .build();
    }
}
