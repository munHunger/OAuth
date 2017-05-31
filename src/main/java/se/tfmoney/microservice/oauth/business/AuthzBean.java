package se.tfmoney.microservice.oauth.business;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.springframework.stereotype.Component;
import se.tfmoney.microservice.oauth.model.AuthenticationToken;
import se.tfmoney.microservice.oauth.model.client.RegisteredClient;
import se.tfmoney.microservice.oauth.model.user.User;
import se.tfmoney.microservice.oauth.util.database.jpa.Database;
import se.tfmoney.microservice.oauth.util.jwt.JSONWebToken;
import se.tfmoney.microservice.oauth.util.properties.Settings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static se.tfmoney.microservice.oauth.util.oauth.OAuthUtils.invalidateTokens;

/**
 * Created by Marcus Münger on 2017-05-16.
 */
@Component
@Path("/oauth")
@Api(value = "OAuth", description = "Creates authorization requests")
public class AuthzBean
{
    @Context
    private HttpServletRequest servletRequest;

    @POST
    @Path("/authz")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Authenticates a user",
                  notes = "Authenticates a user and returns either an access token or an authentication token depending on what is requested and what the client allows. If authenticated the user will be redirected to the redirect_uri with the url-pattern: {redirect_uri}(#access_token={access_token}&expires_in={time})|(?code={auth_token})")
    public Response authenticateRequest(
            @ApiParam(value = "The username of the user to authenticate", example = "DudeMaster43")
            @QueryParam("username")
                    String username,
            @ApiParam(value = "The password of the user to authenticate", example = "S0s3cUR3")
            @QueryParam("password")
                    String password,
            @ApiParam(value = "The URL to redirect to. This must be a registered URL for the client",
                      example = "http://localhost:9090/swagger")
            @QueryParam("redirect_uri")
                    String redirectUri,
            @ApiParam(value = "The ID of the client to login against", example = "id75pvdb25j3e7dr2d6gjsmplb18v2i2")
            @QueryParam("client_id")
                    String clientID,
            @ApiParam(
                    value = "The type of response/authentication to use. If token, then the authentication will be an IMPLICIT_GRANT and the client must support that type. Otherwise it will be a CODE_GRANT",
                    defaultValue = "token", allowableValues = "token, code")
            @QueryParam("response_type")
                    String type) throws Exception
    {
        try
        {
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(servletRequest);
            OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
            Map<String, Object> param = new HashMap<>();
            param.put("id", oauthRequest.getParam(OAuth.OAUTH_CLIENT_ID));
            List clientList = Database.getObjects("from RegisteredClient WHERE clientID = :id", param);
            boolean clientExist = !clientList.isEmpty();

            param = new HashMap<>();
            param.put("id", oauthRequest.getParam(OAuth.OAUTH_CLIENT_ID));
            param.put("url", oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI));
            boolean isUrlAuthorized = !Database.getObjects("from ClientURL WHERE clientID = :id AND url = :url", param)
                                               .isEmpty();

            param = new HashMap<>();
            param.put("username", oauthRequest.getParam(OAuth.OAUTH_USERNAME));
            param.put("password", org.apache.commons.codec.digest.DigestUtils.sha256Hex(
                    oauthRequest.getParam(OAuth.OAUTH_PASSWORD)));
            boolean isUserAuthenticated = !Database.getObjects(
                    "from User WHERE username = :username AND password = :password", param).isEmpty();
            if (isUserAuthenticated && clientExist && isUrlAuthorized)
            {
                OAuthASResponse.OAuthAuthorizationResponseBuilder builder = OAuthASResponse.authorizationResponse(
                        servletRequest, HttpServletResponse.SC_FOUND);
                final String authorizationCode = oauthIssuerImpl.authorizationCode();

                DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR, 1);
                AuthenticationToken token = new AuthenticationToken(authorizationCode, oauthIssuerImpl.accessToken(),
                                                                    oauthRequest.getParam(OAuth.OAUTH_CLIENT_ID),
                                                                    oauthRequest.getParam(OAuth.OAUTH_USERNAME),
                                                                    formater.format(calendar.getTime()),
                                                                    oauthIssuerImpl.refreshToken());

                invalidateTokens(oauthRequest.getParam(OAuth.OAUTH_CLIENT_ID),
                                 oauthRequest.getParam(OAuth.OAUTH_USERNAME));

                Database.saveObject(token);

                String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
                if (ResponseType.CODE.toString().equals(responseType))
                    builder.setCode(authorizationCode);
                else if (ResponseType.TOKEN.toString().equals(responseType) && "TRUE".equals(
                        Settings.getStringSetting("allow_implicit_grant").toUpperCase()))
                {
                    RegisteredClient client = (RegisteredClient) clientList.get(0);
                    String jwt = JSONWebToken.buildToken(client.jwtKey, token.accessToken,
                                                         Settings.getStringSetting("issuer_id"),
                                                         new User(username, null).getRolesCSV() + ";implicit",
                                                         client.clientID, 3600000);
                    builder.setAccessToken(jwt);
                }

                String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);
                final OAuthResponse response = builder.location(redirectURI).buildQueryMessage();
                URI url = new URI(response.getLocationUri());
                return Response.status(response.getResponseStatus()).location(url).build();
            }
            else
                return Response.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        } catch (OAuthProblemException e)
        {
            return buildError(e);
        }
    }

    private Response buildError(OAuthProblemException e) throws OAuthSystemException, URISyntaxException
    {
        final Response.ResponseBuilder responseBuilder = Response.status(HttpServletResponse.SC_FOUND);
        String redirectUri = e.getRedirectUri();

        if (OAuthUtils.isEmpty(redirectUri))
            throw new WebApplicationException(
                    responseBuilder.entity("OAuth callback url needs to be provided by client").build());
        final OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
                                                      .error(e)
                                                      .location(redirectUri)
                                                      .buildQueryMessage();
        final URI location = new URI(response.getLocationUri());
        return responseBuilder.location(location).build();
    }
}
