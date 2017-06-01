package se.tfmoney.microservice.oauth.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import se.tfmoney.microservice.oauth.util.database.jpa.Database;
import se.tfmoney.microservice.oauth.util.nonce.NonceUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
@XmlRootElement(name = "nonce_token")
@ApiModel(description = "A nonce token that is used to deflect against replay attacks")
public class NonceToken
{
    @Id
    @XmlElement(name = "nonce")
    @SerializedName("nonce")
    @ApiModelProperty(name = "nonce",
                      value = "A token that is valid for one request. All requests need to supply a nonce token in order to deflect replay attacks",
                      required = true)
    @Column(name = "token")
    public String token;

    @Column(name = "expiration_date")
    @Expose(serialize = false, deserialize = false)
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
