package se.tfmoney.microservice.oauth.util.oauth;

import io.jsonwebtoken.Claims;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import se.tfmoney.microservice.oauth.model.token.AccessToken;
import se.tfmoney.microservice.oauth.util.database.jpa.Database;
import se.tfmoney.microservice.oauth.util.http.HttpRequest;
import se.tfmoney.microservice.oauth.util.jwt.JSONWebToken;
import se.tfmoney.microservice.oauth.util.properties.Settings;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marcus Münger on 2017-05-18.
 */
public class OAuthUtils
{
    public static AccessToken convertToAccessToken(String authToken) throws Exception
    {
        String nonce = HttpRequest.getRequest(Settings.getStringSetting("issuer_url") + "/nonce", null).headers.get(
                "nonce");
        Map<String, String> headers = new HashMap<>();
        headers.put("nonce", nonce);
        Map<String, String> param = new HashMap<>();
        param.put("grant_type", "authorization_code");
        param.put("client_id", Settings.getStringSetting("client_id"));
        param.put("client_secret", Settings.getStringSetting("client_secret"));
        param.put("code", authToken);

        return (AccessToken) HttpRequest.postForm(Settings.getStringSetting("issuer_url") + "/oauth/token", headers,
                                                  param, AccessToken.class).data;
    }

    public static boolean isAuthenticated(String accessToken) throws Exception
    {
        if (accessToken.toUpperCase().startsWith("BEARER "))
            accessToken = accessToken.substring("BEARER ".length());
        Claims claims;
        try
        {
            claims = JSONWebToken.decryptToken(Settings.getStringSetting("jwt_key"), accessToken);
            boolean correctAudience = Arrays.asList(claims.getAudience().split(";"))
                                            .contains(Settings.getStringSetting("client_id"));
            boolean correctIssuer = claims.getIssuer().equals(Settings.getStringSetting("issuer_id"));
            return correctIssuer && correctAudience;
        } catch (Exception e)
        {
            return false;
        }
    }

    public static boolean isAuthenticated(HttpServletRequest request) throws Exception
    {
        OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.HEADER);
        String accessToken = oauthRequest.getAccessToken();
        return isAuthenticated(accessToken);
    }

    public static boolean hasAnyRole(String accessToken, String... acceptedRoles) throws Exception
    {
        if (accessToken == null)
            return false;
        if (accessToken.toUpperCase().startsWith("BEARER "))
            accessToken = accessToken.substring("BEARER ".length());
        List<String> accepted = Arrays.asList(acceptedRoles);
        Claims claims;
        try
        {
            claims = JSONWebToken.decryptToken(Settings.getStringSetting("jwt_key"), accessToken);
            boolean correctAudience = Arrays.asList(claims.getAudience().split(";"))
                                            .contains(Settings.getStringSetting("client_id"));
            boolean correctIssuer = claims.getIssuer().equals(Settings.getStringSetting("issuer_id"));
            return correctAudience && correctIssuer && Arrays.stream(claims.getSubject().split(";"))
                                                             .filter(role -> accepted.contains(role))
                                                             .count() > 0;
        } catch (Exception e)
        {
            return false;
        }
    }

    public static void invalidateTokens(String clientID, String username) throws Exception
    {
        Map<String, Object> param = new HashMap<>();
        param.put("client", clientID);
        param.put("user", username);
        for (Object o : Database.getObjects("from AuthenticationToken WHERE clientID = :client AND username = :user",
                                            param))
            Database.deleteObjects(o);
    }
}
