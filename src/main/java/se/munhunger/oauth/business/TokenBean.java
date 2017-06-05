package se.munhunger.oauth.business;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.springframework.stereotype.Component;
import se.munhunger.oauth.model.client.RegisteredClient;
import se.munhunger.oauth.model.token.AuthenticationToken;
import se.munhunger.oauth.model.token.NonceToken;
import se.munhunger.oauth.model.user.User;
import se.munhunger.oauth.util.database.jpa.Database;
import se.munhunger.oauth.util.jwt.JSONWebToken;
import se.munhunger.oauth.util.oauth.OAuthUtils;
import se.munhunger.oauth.util.properties.Settings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
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
    @Path("/token/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Refresh access token", notes = "Uses a refresh token to update an access token")
    public Response refresh(
            @HeaderParam("nonce")
                    String nonce,
            @FormParam("client_id")
                    String clientID,
            @FormParam("client_secret")
                    String clientSecret,
            @FormParam("refresh_token")
                    String refreshRoken) throws Exception
    {
        try
        {
            Map<String, Object> params = new HashMap<>();
            params.put("id", clientID);
            params.put("secret", org.apache.commons.codec.digest.DigestUtils.sha256Hex(clientSecret));
            List registeredClient = Database.getObjects(
                    "from RegisteredClient WHERE clientID = :id AND clientSecret = :secret", params);
            boolean validClientCredentials = !registeredClient.isEmpty();

            if (validClientCredentials)
            {
                RegisteredClient client = (RegisteredClient) registeredClient.get(0);
                params = new HashMap<>();
                params.put("token", refreshRoken);
                List tokenList = Database.getObjects(
                        "from AuthenticationToken WHERE refreshToken = :token AND clientID = :clientID", params);
                if (!tokenList.isEmpty())
                {
                    AuthenticationToken authToken = (AuthenticationToken) tokenList.get(0);
                    OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
                    final String authorizationCode = oauthIssuerImpl.authorizationCode();

                    DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.HOUR, 1);
                    AuthenticationToken token = new AuthenticationToken(authorizationCode,
                                                                        oauthIssuerImpl.accessToken(), clientID,
                                                                        authToken.username,
                                                                        formater.format(calendar.getTime()),
                                                                        oauthIssuerImpl.refreshToken());

                    OAuthUtils.invalidateTokens(clientID, authToken.username);

                    Database.saveObject(token);

                    String accessToken = authToken.accessToken;
                    String jwt = JSONWebToken.buildToken(client.jwtKey, accessToken,
                                                         Settings.getStringSetting("issuer_id"),
                                                         new User(authToken.username,
                                                                  null).getRolesCSV() + ";authenticated", clientID,
                                                         3600000);
                    OAuthResponse response = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
                                                            .setAccessToken(jwt)
                                                            .setRefreshToken(authToken.refreshToken)
                                                            .buildJSONMessage();
                    return Response.status(response.getResponseStatus())
                                   .entity(response.getBody())
                                   .header("nonce", NonceToken.generateToken().token)
                                   .build();
                }
            }
            return Response.status(HttpServletResponse.SC_UNAUTHORIZED)
                           .header("nonce", NonceToken.generateToken().token)
                           .build();
        } catch (OAuthProblemException e)
        {
            OAuthResponse res = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                               .error(e)
                                               .buildJSONMessage();
            return Response.status(res.getResponseStatus())
                           .entity(res.getBody())
                           .header("nonce", NonceToken.generateToken().token)
                           .build();
        }
    }

    @POST
    @Path("/token")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Converts an authentication token to an access token",
                  notes = "A login endpoint for client to convert a users authentication token into an access token. A refresh token will be included in case a refresh is needed")
    public Response authorize(
            @HeaderParam("nonce")
                    String nonce,
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
            List registeredClient = Database.getObjects(
                    "from RegisteredClient WHERE clientID = :id AND clientSecret = :secret", params);
            boolean validClientCredentials = !registeredClient.isEmpty();

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            params = new HashMap<>();
            params.put("token", authCode);
            params.put("clientID", clientID);
            params.put("date", formatter.format(Calendar.getInstance().getTime()));
            if (validClientCredentials)
            {
                RegisteredClient client = (RegisteredClient) registeredClient.get(0);
                List token = Database.getObjects(
                        "from AuthenticationToken WHERE authToken = :token AND clientID = :clientID AND expirationDate > date(:date)",
                        params);
                if (!token.isEmpty())
                {
                    AuthenticationToken authToken = (AuthenticationToken) token.get(0);
                    String accessToken = authToken.accessToken;
                    String jwt = JSONWebToken.buildToken(client.jwtKey, accessToken,
                                                         Settings.getStringSetting("issuer_id"),
                                                         new User(authToken.username,
                                                                  null).getRolesCSV() + ";authenticated", clientID,
                                                         3600000);
                    OAuthResponse response = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
                                                            .setAccessToken(jwt)
                                                            .setRefreshToken(authToken.refreshToken)
                                                            .buildJSONMessage();
                    return Response.status(response.getResponseStatus())
                                   .entity(response.getBody())
                                   .header("nonce", NonceToken.generateToken().token)
                                   .build();
                }
            }
            return Response.status(HttpServletResponse.SC_UNAUTHORIZED)
                           .header("nonce", NonceToken.generateToken().token)
                           .build();
        } catch (OAuthProblemException e)
        {
            OAuthResponse res = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                               .error(e)
                                               .buildJSONMessage();
            return Response.status(res.getResponseStatus())
                           .entity(res.getBody())
                           .header("nonce", NonceToken.generateToken().token)
                           .build();
        }
    }
}
