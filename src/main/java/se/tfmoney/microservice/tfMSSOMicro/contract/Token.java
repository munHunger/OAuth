package se.tfmoney.microservice.tfMSSOMicro.contract;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Marcus Münger on 2017-05-15.
 */
@Path("/token")
public interface Token
{
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    Response authorize(
            @Context
                    HttpServletRequest request) throws OAuthSystemException;
}
