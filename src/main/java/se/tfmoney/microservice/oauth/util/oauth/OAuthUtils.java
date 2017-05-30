package se.tfmoney.microservice.oauth.util.oauth;

import io.jsonwebtoken.Claims;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import se.tfmoney.microservice.oauth.util.jwt.JSONWebToken;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

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
            claims = JSONWebToken.decryptToken(accessToken);
        } catch (Exception e)
        {
            return false;
        }
        return true;
    }

    public static boolean isAuthenticated(HttpServletRequest request) throws Exception
    {
        OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.HEADER);
        String accessToken = oauthRequest.getAccessToken();
        return isAuthenticated(accessToken);
    }

    public static boolean hasAnyRole(String accessToken, String... acceptedRoles) throws Exception
    {
        if (accessToken.toUpperCase().startsWith("BEARER "))
            accessToken = accessToken.substring("BEARER ".length());
        List<String> accepted = Arrays.asList(acceptedRoles);
        Claims claims;
        try
        {
            claims = JSONWebToken.decryptToken(accessToken);
            return Arrays.stream(claims.getSubject().split(";")).filter(role -> accepted.contains(role)).count() > 0;
        } catch (Exception e)
        {
            return false;
        }
    }
}
