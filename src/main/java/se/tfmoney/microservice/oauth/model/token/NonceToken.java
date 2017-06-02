package se.tfmoney.microservice.oauth.model.token;

import se.tfmoney.microservice.oauth.util.database.jpa.Database;
import se.tfmoney.microservice.oauth.util.nonce.NonceUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Marcus Münger on 2017-06-01.
 */
@Entity
@Table(name = "nonce")
public class NonceToken
{
    @Id
    @Column(name = "token")
    public String token;

    @Column(name = "expiration_date")
    public String expirationDate;

    public static NonceToken generateToken() throws Exception
    {
        NonceToken token = new NonceToken();
        token.token = new BigInteger(32 * 5, new SecureRandom()).toString(32);
        DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);
        token.expirationDate = formater.format(calendar.getTime());
        Database.saveObject(token);
        NonceUtils.cleanOldTokens();
        return token;
    }
}
