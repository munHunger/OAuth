package se.tfmoney.microservice.tfMSSOMicro.business;

import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.springframework.stereotype.Component;
import se.tfmoney.microservice.tfMSSOMicro.contract.Token;
import se.tfmoney.microservice.tfMSSOMicro.model.AuthenticationToken;
import se.tfmoney.microservice.tfMSSOMicro.util.database.jpa.Database;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
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
@Component
public class TokenBean implements Token
{
    @Context
    private HttpServletRequest servletRequest;

    @Override
    public Response authorize() throws Exception
    {
        try
        {
            OAuthTokenRequest oauthRequest = new OAuthTokenRequest(servletRequest);

            Map<String, Object> params = new HashMap<>();
            params.put("id", oauthRequest.getClientId());
            params.put("secret", org.apache.commons.codec.digest.DigestUtils.sha256Hex(oauthRequest.getClientSecret()));
            boolean validClientCredentials = !Database.getObjects(
                    "from RegisteredClient WHERE clientID = :id AND clientSecret = :secret", params).isEmpty();

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            params = new HashMap<>();
            params.put("token", oauthRequest.getCode());
            params.put("clientID", oauthRequest.getClientId());
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
