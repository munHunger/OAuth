package se.tfmoney.microservice.tfMSSOMicro.business;

import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.apache.oltu.oauth2.rs.response.OAuthRSResponse;
import org.springframework.stereotype.Component;
import se.tfmoney.microservice.tfMSSOMicro.contract.Resource;
import se.tfmoney.microservice.tfMSSOMicro.util.Database;
import se.tfmoney.microservice.tfMSSOMicro.util.properties.Settings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Created by Marcus Münger on 2017-05-15.
 */
@Component
public class ResourceBean implements Resource
{
    private Database database = Database.getSingleton();

    @Context
    HttpServletRequest request;

    @Override
    public Response get() throws OAuthSystemException
    {
        try
        {
            // Make the OAuth Request out of this request
            OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.HEADER);
            // Get the access token
            String accessToken = oauthRequest.getAccessToken();

            // Validate the access token
            if (!database.isValidToken(accessToken))
            {
                // Return the OAuth error message
                OAuthResponse oauthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                                                             .setRealm(
                                                                     Settings.getStringSetting("resource_server_name"))
                                                             .setError(OAuthError.ResourceResponse.INVALID_TOKEN)
                                                             .buildHeaderMessage();

                //return Response.status(Response.Status.UNAUTHORIZED).build();
                return Response.status(Response.Status.UNAUTHORIZED)
                               .header(OAuth.HeaderType.WWW_AUTHENTICATE,
                                       oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE))
                               .build();
            }
            // Return the resource
            return Response.status(Response.Status.OK).entity(accessToken).build();
        } catch (OAuthProblemException e)
        {
            // Check if the error code has been set
            String errorCode = e.getError();
            if (OAuthUtils.isEmpty(errorCode))
            {

                // Return the OAuth error message
                OAuthResponse oauthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                                                             .setRealm(
                                                                     Settings.getStringSetting("resource_server_name"))
                                                             .buildHeaderMessage();

                // If no error code then return a standard 401 Unauthorized response
                return Response.status(Response.Status.UNAUTHORIZED)
                               .header(OAuth.HeaderType.WWW_AUTHENTICATE,
                                       oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE))
                               .build();
            }

            OAuthResponse oauthResponse = OAuthRSResponse.errorResponse(HttpServletResponse.SC_UNAUTHORIZED)
                                                         .setRealm(Settings.getStringSetting("resource_server_name"))
                                                         .setError(e.getError())
                                                         .setErrorDescription(e.getDescription())
                                                         .setErrorUri(e.getUri())
                                                         .buildHeaderMessage();

            return Response.status(Response.Status.BAD_REQUEST)
                           .header(OAuth.HeaderType.WWW_AUTHENTICATE,
                                   oauthResponse.getHeader(OAuth.HeaderType.WWW_AUTHENTICATE))
                           .build();
        }
    }
}
