package se.tfmoney.microservice.oauth.business;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.stereotype.Component;
import se.tfmoney.microservice.oauth.model.NonceToken;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Marcus Münger on 2017-06-01.
 */
@Component
@Path("/nonce")
@Api(value = "OAuth", description = "Creates new nonce tokens")
public class NonceBean
{
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get a new token", notes = "Creates a new nonce token that validates one request")
    @ApiResponse(code = HttpServletResponse.SC_OK, message = "A new token was generated", response = NonceToken.class)
    public Response getToken() throws Exception
    {
        return Response.ok().header("nonce", NonceToken.generateToken().token).build();
    }
}
