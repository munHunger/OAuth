package se.tfmoney.microservice.tfMSSOMicro.model.token;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Marcus Münger on 2017-05-16.
 */
@XmlRootElement(name = "ClientAuthenticationRequest")
@ApiModel(value = "ClientAuthenticationRequest", description = "A request for authentication")
public class ClientAuthenticationRequest
{
    @XmlElement(name = "auth_token")
    @SerializedName("auth_token")
    @ApiModelProperty(name = "auth_token", value = "An authentication token from a properly authenticated user")
    public String authToken;

    @XmlElement(name = "client_id")
    @SerializedName("client_id")
    @ApiModelProperty(name = "client_id",
                      value = "An identifier for this client. For an authentication to successful this must be one of the client IDs registered for the auth_token")
    public String clientID;

    @XmlElement(name = "client_secret")
    @SerializedName("client_secret")
    @ApiModelProperty(name = "client_secret", value = "The secret for the client.")
    public String clientSecret;
}
