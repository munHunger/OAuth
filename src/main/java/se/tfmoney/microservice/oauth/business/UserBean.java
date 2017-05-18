package se.tfmoney.microservice.oauth.business;

import org.springframework.stereotype.Component;
import se.tfmoney.microservice.oauth.contract.User;
import se.tfmoney.microservice.oauth.util.database.jpa.Database;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcus Münger on 2017-05-18.
 */
@Component
public class UserBean implements User
{
    @Override
    public Response createUser(String username, String password, String clientID) throws Exception
    {
        Map<String, Object> param = new HashMap<>();
        param.put("username", username);
        boolean isUserAvailable = Database.getObjects("from User WHERE username = :username", param).isEmpty();

        param = new HashMap<>();
        param.put("id", clientID);
        boolean clientExists = !Database.getObjects("from RegisteredClient WHERE clientID = :id", param).isEmpty();
        if (isUserAvailable && clientExists)
        {
            se.tfmoney.microservice.oauth.model.user.User newUser = new se.tfmoney.microservice.oauth.model.user.User();
            newUser.username = username;
            newUser.password = org.apache.commons.codec.digest.DigestUtils.sha256Hex(password);
            Database.saveObject(newUser);
            return Response.status(HttpServletResponse.SC_NO_CONTENT).build();
        }
        else
            return Response.status(HttpServletResponse.SC_CONFLICT).build();
    }
}
