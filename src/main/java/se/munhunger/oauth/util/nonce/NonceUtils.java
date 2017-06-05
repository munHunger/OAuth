package se.munhunger.oauth.util.nonce;

import se.munhunger.oauth.model.token.NonceToken;
import se.munhunger.oauth.util.database.jpa.Database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Marcus Münger on 2017-06-01.
 */
public class NonceUtils
{
    public static boolean isNonceValid(String nonce) throws Exception
    {
        Map<String, Object> param = new HashMap<>();
        param.put("token", nonce);
        List tokenList = Database.getObjects("from NonceToken WHERE token = :token", param);
        if (tokenList.isEmpty())
            return false;
        NonceToken token = (NonceToken) tokenList.get(0);
        Database.deleteObjects(token);
        cleanOldTokens();
        return true;
    }

    public static void cleanOldTokens() throws Exception
    {
        DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> param = new HashMap<>();
        param.put("date", formater.format(Calendar.getInstance().getTime()));
        for (Object o : Database.getObjects("from NonceToken WHERE expirationDate < :date", param))
            Database.deleteObjects(o);
    }
}
