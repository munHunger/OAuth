package se.tfmoney.microservice.tfMSSOMicro.contract;

import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Marcus Münger on 2017-05-15.
 */
@Path("/resource")
public interface Resource
{
    @GET
    @Produces(MediaType.TEXT_HTML)
    Response get() throws OAuthSystemException;
}
