package se.tfmoney.microservice.tfMSSOMicro.business;

import io.swagger.annotations.ApiParam;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.springframework.stereotype.Component;
import se.tfmoney.microservice.tfMSSOMicro.contract.Authenticate;
import se.tfmoney.microservice.tfMSSOMicro.model.AuthenticationRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Created by Marcus Münger on 2017-05-12.
 */
@Component
public class AuthenticateBean implements Authenticate
{
    @Override
    public Response authenticate(
            @Context
                    HttpServletRequest request,
            @ApiParam(value = "A request for authentication containing all needed fields for auth")
                    AuthenticationRequest authRequest) throws Exception
    {
        OAuthTokenRequest tokenRequest;
        OAuthIssuer issuer = new OAuthIssuerImpl(new MD5Generator());
        try
        {
            tokenRequest = new OAuthTokenRequest(request);
            //Validate client
            String authCode = tokenRequest.getCode();
            String accessToken = issuer.accessToken();
            String refreshToken = issuer.refreshToken();
            OAuthResponse res = OAuthASResponse.tokenResponse(HttpServletResponse.SC_OK)
                                               .setAccessToken(accessToken)
                                               .setExpiresIn("3600")
                                               .setRefreshToken(refreshToken)
                                               .buildJSONMessage();
            return Response.ok(res.getBody()).build();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return Response.status(HttpServletResponse.SC_NOT_IMPLEMENTED).build();
    }

    @Override
    public Response authenticate(
            @ApiParam(value = "A request for authentication containing all needed fields for auth")
                    AuthenticationRequest authRequest) throws Exception
    {
        return Response.status(HttpServletResponse.SC_SEE_OTHER).build();
    }
}
