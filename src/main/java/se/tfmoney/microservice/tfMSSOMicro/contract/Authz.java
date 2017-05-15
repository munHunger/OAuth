package se.tfmoney.microservice.tfMSSOMicro.contract;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;

/**
 * Created by Marcus Münger on 2017-05-15.
 */
@Path("/authz")
public interface Authz
{
    @GET
    Response authorize(
            @Context
                    HttpServletRequest request) throws URISyntaxException, OAuthSystemException;
}
