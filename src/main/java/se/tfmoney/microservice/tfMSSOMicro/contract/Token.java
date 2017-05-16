package se.tfmoney.microservice.tfMSSOMicro.contract;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Marcus Münger on 2017-05-15.
 */
@Path("/token")
@Api(value = "Token", description = "Token endpoint")
public interface Token
{
    @Path("/auth")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "token")
    Response authorize() throws OAuthSystemException;

    @Path("/test")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    Response test();
}
