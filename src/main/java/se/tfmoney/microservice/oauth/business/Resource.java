package se.tfmoney.microservice.oauth.business;

import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.springframework.stereotype.Component;
import se.tfmoney.microservice.oauth.util.oauth.OAuthUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Created by Marcus Münger on 2017-05-18.
 */
@Path("/res")
@Component
public class Resource
{
    @Context
    private HttpServletRequest request;

    @GET
    public Response getResource() throws Exception
    {
        try
        {
            if (OAuthUtils.isAuthenticated(request))
                return Response.ok("Hello World!").build();
            else
                return Response.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        } catch (OAuthProblemException e)
        {
            final OAuthResponse response = OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
                                                          .error(e)
                                                          .buildJSONMessage();
            return Response.status(response.getResponseStatus()).entity(response.getBody()).build();
        }
    }
}
