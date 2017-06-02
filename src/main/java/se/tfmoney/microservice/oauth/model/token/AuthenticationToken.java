package se.tfmoney.microservice.oauth.model.token;

import com.google.gson.annotations.Expose;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by Marcus Münger on 2017-05-12.
 */
@Entity
@Table(name = "authentication_token")
public class AuthenticationToken implements Serializable
{
    @Id
    @Column(name = "auth_token")
    public String authToken;

    @Id
    @Column(name = "access_token")
    public String accessToken;

    @Column(name = "client_id")
    @Expose(serialize = false, deserialize = false)
    public String clientID;

    @Column(name = "username")
    @Expose(serialize = false, deserialize = false)
    public String username;

    @Column(name = "expiration_date")
    @Expose(serialize = false, deserialize = false)
    public String expirationDate;

    @Column(name = "refresh_token")
    public String refreshToken;

    public AuthenticationToken()
    {}

    public AuthenticationToken(String authToken, String accessToken, String clientID, String username,
                               String expirationDate, String refreshToken)
    {
        this.authToken = authToken;
        this.accessToken = accessToken;
        this.clientID = clientID;
        this.username = username;
        this.expirationDate = expirationDate;
        this.refreshToken = refreshToken;
    }
}
