package se.tfmoney.microservice.tfMSSOMicro.util;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Marcus Münger on 2017-05-15.
 */
public class Database
{

    public static Database singleton;

    public static Database getSingleton()
    {
        if (singleton == null)
            singleton = new Database();
        return singleton;
    }

    private Set<String> authCodes = new HashSet<>();
    private Set<String> tokens = new HashSet<>();

    public void addAuthCode(String authCode)
    {
        authCodes.add(authCode);
    }

    public boolean isValidAuthCode(String authCode)
    {
        return authCodes.contains(authCode);
    }

    public void addToken(String token)
    {
        tokens.add(token);
    }

    public boolean isValidToken(String token)
    {
        return tokens.contains(token);
    }
}