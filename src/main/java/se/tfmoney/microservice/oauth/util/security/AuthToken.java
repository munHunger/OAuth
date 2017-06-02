package se.tfmoney.microservice.oauth.util.security;

import java.security.Principal;

/**
 * Created by Marcus Münger on 2017-05-19.
 */
public class AuthToken implements Principal
{
    private String token;

    @Override
    public String getName()
    {
        return token;
    }

    public AuthToken(String token)
    {
        this.token = token;
    }
}
