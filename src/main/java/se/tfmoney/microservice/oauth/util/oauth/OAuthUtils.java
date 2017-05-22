package se.tfmoney.microservice.oauth.util.oauth;

import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import se.tfmoney.microservice.oauth.model.AuthenticationToken;
import se.tfmoney.microservice.oauth.util.database.jpa.Database;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("date", formatter.format(Calendar.getInstance().getTime()));
        parameters.put("token", accessToken);
        return !Database.getObjects(
                "from AuthenticationToken WHERE accessToken = :token AND expirationDate > date(:date)", parameters)
                        .isEmpty();
    }

    public static boolean isAuthenticated(HttpServletRequest request) throws Exception
    {
        OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.HEADER);
        String accessToken = oauthRequest.getAccessToken();
        return isAuthenticated(accessToken);
    }

    public static boolean hasAnyRole(String accessToken, String... acceptedRoles) throws Exception
    {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("date", formatter.format(Calendar.getInstance().getTime()));
        parameters.put("token", accessToken);
        List tokens = Database.getObjects(
                "from AuthenticationToken WHERE accessToken = :token AND expirationDate > date(:date)", parameters);
        if (tokens.isEmpty())
            return false;

        AuthenticationToken dbToken = (AuthenticationToken) tokens.get(0);

        for (String role : acceptedRoles) //TODO: can this be implemeted better?
        {
            parameters = new HashMap<>();
            parameters.put("username", dbToken.username);
            parameters.put("role", role);
            if (!Database.getObjects("from UserRoles WHERE username = :username AND role = :role", parameters)
                         .isEmpty())
                return true;
        }
        return false;
    }
}
