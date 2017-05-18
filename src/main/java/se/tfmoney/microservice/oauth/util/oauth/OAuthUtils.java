package se.tfmoney.microservice.oauth.util.oauth;

import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import se.tfmoney.microservice.oauth.util.database.jpa.Database;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcus Münger on 2017-05-18.
 */
public class OAuthUtils
{
    public static boolean isAuthenticated(HttpServletRequest request) throws Exception
    {
        OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.HEADER);
        String accessToken = oauthRequest.getAccessToken();

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("token", accessToken);
        return !Database.getObjects("from AuthenticationToken WHERE accessToken = :token", parameters).isEmpty();
    }
}
