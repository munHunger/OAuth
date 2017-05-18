package se.tfmoney.microservice.oauth.business;

import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;
import se.tfmoney.microservice.oauth.contract.Client;
import se.tfmoney.microservice.oauth.model.client.ClientRequest;
import se.tfmoney.microservice.oauth.model.client.ClientURL;
import se.tfmoney.microservice.oauth.model.client.RegisteredClient;
import se.tfmoney.microservice.oauth.model.error.ErrorMessage;
import se.tfmoney.microservice.oauth.util.database.jpa.Database;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcus Münger on 2017-05-16.
 */
@Component
public class ClientBean implements Client
{
    @Override
    public Response createClient(
            @ApiParam(value = "The request for a new client", required = true)
                    ClientRequest request) throws Exception
    {
        if (request.clientID.length() > 16)
            return Response.status(HttpServletResponse.SC_BAD_REQUEST)
                           .entity(new ErrorMessage("Could not create client", "ClientID was too long"))
                           .build();
        RegisteredClient client = new RegisteredClient();
        client.clientSecret = new BigInteger(64 * 5, new SecureRandom()).toString(32);
        client.clientID = request.clientID + new BigInteger((32 - request.clientID.length()) * 5,
                                                            new SecureRandom()).toString(32);

        RegisteredClient dbSafeClient = new RegisteredClient();
        dbSafeClient.clientID = client.clientID;
        dbSafeClient.clientSecret = org.apache.commons.codec.digest.DigestUtils.sha256Hex(client.clientSecret);
        try
        {
            List<Object> objectsToSave = new ArrayList<>();
            objectsToSave.add(dbSafeClient);
            for (String url : request.httpRedirects)
                objectsToSave.add(new ClientURL(client.clientID, url));

            Database.saveObjects(objectsToSave);
        } catch (Exception e)
        {
            e.printStackTrace();
            return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                           .entity(new ErrorMessage("Could not create client",
                                                    "Could not save the object in the database"))
                           .build();
        }
        return Response.ok(client).build();
    }
}
