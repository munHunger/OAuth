package se.tfmoney.microservice.oauth.util.oauth;

import io.jsonwebtoken.Claims;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import se.tfmoney.microservice.oauth.util.database.jpa.Database;
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
