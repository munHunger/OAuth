package se.tfmoney.microservice.tfMSSOMicro.business;

import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.stereotype.Component;
import se.tfmoney.microservice.tfMSSOMicro.contract.Token;
import se.tfmoney.microservice.tfMSSOMicro.util.properties.Settings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Created by Marcus Münger on 2017-05-15.
 */
@Component //TODO: is this strictly needed
public class TokenBean implements Token
{
    @Override
    public Response authorize(
            @Context
                    HttpServletRequest request) throws OAuthSystemException
    {
        try
        {
            OAuthTokenRequest oauthRequest = new OAuthTokenRequest(request);
            OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

            // check if clientid is valid
            if (!checkClientId(oauthRequest.getClientId()))
            {
                return buildInvalidClientIdResponse();
            }

            // check if client_secret is valid
            if (!checkClientSecret(oauthRequest.getClientSecret()))
            {
                return buildInvalidClientSecretResponse();
            }

            // do checking for different grant types
            if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.AUTHORIZATION_CODE.toString()))
            {
                if (!checkAuthCode(oauthRequest.getParam(OAuth.OAUTH_CODE)))
                {
                    return buildBadAuthCodeResponse();
                }
            }
            else if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.PASSWORD.toString()))
            {
                if (!checkUserPass(oauthRequest.getUsername(), oauthRequest.getPassword()))
                {
                    return buildInvalidUserPassResponse();
                }
            }
            else if (oauthRequest.getParam(OAuth.OAUTH_GRANT_TYPE).equals(GrantType.REFRESH_TOKEN.toString()))
            {
                // refresh token is not supported in this implementation
                buildInvalidUserPassResponse();
            }

            final String accessToken = oauthIssuerImpl.accessToken();
            //database.addToken(accessToken);

            OAuthResponse response = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
                                                    .setAccessToken(accessToken)
                                                    .setExpiresIn("3600")
                                                    .buildJSONMessage();
            return Response.status(response.getResponseStatus()).entity(response.getBody()).build();
        } catch (OAuthProblemException e)
        {
            OAuthResponse res = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                               .error(e)
                                               .buildJSONMessage();
            return Response.status(res.getResponseStatus()).entity(res.getBody()).build();
        }
    }

    private Response buildInvalidClientIdResponse() throws OAuthSystemException
    {
        OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                                .setError(OAuthError.TokenResponse.INVALID_CLIENT)
                                                .setErrorDescription("Auth failed")
                                                .buildJSONMessage();
        return Response.status(response.getResponseStatus()).entity(response.getBody()).build();
    }

    private Response buildInvalidClientSecretResponse() throws OAuthSystemException
    {
        OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                                                .setError(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT)
                                                .setErrorDescription("Auth failed")
                                                .buildJSONMessage();
        return Response.status(response.getResponseStatus()).entity(response.getBody()).build();
    }

    private Response buildBadAuthCodeResponse() throws OAuthSystemException
    {
        OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                                .setError(OAuthError.TokenResponse.INVALID_GRANT)
                                                .setErrorDescription("invalid authorization code")
                                                .buildJSONMessage();
        return Response.status(response.getResponseStatus()).entity(response.getBody()).build();
    }

    private Response buildInvalidUserPassResponse() throws OAuthSystemException
    {
        OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                                                .setError(OAuthError.TokenResponse.INVALID_GRANT)
                                                .setErrorDescription("invalid username or password")
                                                .buildJSONMessage();
        return Response.status(response.getResponseStatus()).entity(response.getBody()).build();
    }

    private boolean checkClientId(String clientId)
    {
        return true;
    }

    private boolean checkClientSecret(String secret)
    {
        return true;
    }

    private boolean checkAuthCode(String authCode)
    {
        //return database.isValidAuthCode(authCode);
        return true;
    }

    private boolean checkUserPass(String user, String pass)
    {
        return Settings.getStringSetting("password").equals(pass) && Settings.getStringSetting("username").equals(user);
    }
}
