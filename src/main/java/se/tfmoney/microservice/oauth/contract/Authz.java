package se.tfmoney.microservice.oauth.contract;

import io.swagger.annotations.Api;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by Marcus Münger on 2017-05-16.
 */
@Path("/oauth/authz")
@Api(value = "Authz", description = "Creates authorization requests")
public interface Authz
{
    @POST
    Response authenticateRequest() throws Exception;
}
