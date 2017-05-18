package se.tfmoney.microservice.tfMSSOMicro.business;

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
import se.tfmoney.microservice.tfMSSOMicro.contract.Authz;
import se.tfmoney.microservice.tfMSSOMicro.model.AuthenticationToken;
import se.tfmoney.microservice.tfMSSOMicro.util.database.jpa.Database;
import se.tfmoney.microservice.tfMSSOMicro.util.properties.Settings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcus Münger on 2017-05-16.
 */
@Component
public class AuthzBean implements Authz
{
    @Context
    private HttpServletRequest servletRequest;

    //Internal request
    @Override
    public Response authenticateRequest() throws Exception
    {
        try
        {
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(servletRequest);
            OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
            Map<String, Object> param = new HashMap<>();
            param.put("id", oauthRequest.getParam(OAuth.OAUTH_CLIENT_ID));
            boolean clientExist = !Database.getObjects("from RegisteredClient WHERE clientID = :id", param).isEmpty();

            param = new HashMap<>();
            param.put("id", oauthRequest.getParam(OAuth.OAUTH_CLIENT_ID));
            param.put("url", oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI));
            boolean isUrlAuthorized = !Database.getObjects("from ClientURL WHERE clientID = :id AND url = :url", param)
                                               .isEmpty();

            param = new HashMap<>();
            param.put("username", oauthRequest.getParam(OAuth.OAUTH_USERNAME));
            param.put("password", org.apache.commons.codec.digest.DigestUtils.sha256Hex(OAuth.OAUTH_PASSWORD));
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
                                                                    formater.format(calendar.getTime()));

                invalidateTokens(oauthRequest.getParam(OAuth.OAUTH_CLIENT_ID),
                                 oauthRequest.getParam(OAuth.OAUTH_USERNAME));

                Database.saveObject(token);

                String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);
                if (ResponseType.CODE.toString().equals(responseType))
                    builder.setCode(authorizationCode);
                else if (ResponseType.TOKEN.toString().equals(responseType) && "TRUE".equals(
                        Settings.getStringSetting("allow_implicit_grant").toUpperCase()))
                {
                    builder.setAccessToken(token.accessToken);
                    builder.setExpiresIn(3600l); // one hour
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

    private void invalidateTokens(String clientID, String username) throws Exception
    {
        Map<String, Object> param = new HashMap<>();
        param.put("client", clientID);
        param.put("user", username);
        for (Object o : Database.getObjects("from AuthenticationToken WHERE clientID = :client AND username = :user",
                                            param))
            Database.deleteObjects(o);
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
