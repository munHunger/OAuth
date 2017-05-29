package se.tfmoney.microservice.oauth.business;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.springframework.stereotype.Component;
import se.tfmoney.microservice.oauth.model.AuthenticationToken;
import se.tfmoney.microservice.oauth.util.database.jpa.Database;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marcus Münger on 2017-05-16.
 */
@Path("/oauth")
@Api(value = "OAuth", description = "Endpoints for creating, refreshing and translating tokens")
@Component
public class TokenBean
{
    @Context
    private HttpServletRequest servletRequest;

    @POST
    @Path("/token")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Converts an authentication token to an access token",
                  notes = "A login endpoint for client to convert a users authentication token into an access token. After authentication the user will be redirected to the specified URI with the url-pattern: {redirect_uri}#access_token={access_token}&expires_in={time}")
    public Response authorize(
            @ApiParam(value = "Authentication type", allowableValues = "authorization_code")
            @FormParam("grant_type")
                    String queryParam,
            @ApiParam(value = "The public identification of the client", example = "id75pvdb25j3e7dr2d6gjsmplb18v2i2")
            @FormParam("client_id")
                    String clientID,
            @ApiParam(value = "The clients secret. Be aware and do not send this over HTTP and only over HTTPS",
                      example = "1qhdsg5uuq1rkksfmj4vrksioru08i42m71q9mu08o0rdkpup5bqjuuv45horrtq")
            @FormParam("client_secret")
                    String clientSecret,
            @ApiParam(
                    value = "Authentication code as gotten by the user to convert into an access token",
                    example = "f9b3d56df65d471cfd0e7f148b47c951")
            @FormParam("code")
                    String authCode) throws Exception
    {
        try
        {
            Map<String, Object> params = new HashMap<>();
            params.put("id", clientID);
            params.put("secret", org.apache.commons.codec.digest.DigestUtils.sha256Hex(clientSecret));
            boolean validClientCredentials = !Database.getObjects(
                    "from RegisteredClient WHERE clientID = :id AND clientSecret = :secret", params).isEmpty();

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            params = new HashMap<>();
            params.put("token", authCode);
            params.put("clientID", clientID);
            params.put("date", formatter.format(Calendar.getInstance().getTime()));
            if (validClientCredentials)
            {
                List token = Database.getObjects(
                        "from AuthenticationToken WHERE authToken = :token AND clientID = :clientID AND expirationDate > date(:date)",
                        params);
                if (!token.isEmpty())
                {
                    String accessToken = ((AuthenticationToken) token.get(0)).accessToken;
                    OAuthResponse response = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
                                                            .setAccessToken(accessToken)
                                                            .buildJSONMessage();
                    return Response.status(response.getResponseStatus()).entity(response.getBody()).build();
                }
            }
            return Response.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        } catch (OAuthProblemException e)
        {
            OAuthResponse res = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                               .error(e)
                                               .buildJSONMessage();
            return Response.status(res.getResponseStatus()).entity(res.getBody()).build();
        }
    }
}
