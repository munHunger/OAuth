package se.munhunger.oauth.model.client;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Marcus Münger on 2017-05-16.
 */
@XmlRootElement(name = "ClientRequest")
@ApiModel(value = "ClientRequest", description = "A request for creating a new client endpoint")
public class ClientRequest
{
    @XmlElement(name = "http_redirects")
    @SerializedName("http_redirects")
    @ApiModelProperty(name = "http_redirects",
                      value = "A list of redirect URLs. i.e. A list of valid URLs to redirect to after the user is authenticated at the SSO. These URLs should reasonably be part of the client base URL",

                      example = "https://localhost/user/verifyLogin, https://localhost/admin/verifyLogin",
                      required = true)
    public String[] httpRedirects;

    @XmlElement(name = "client_id")
    @SerializedName("client_id")
    @ApiModelProperty(name = "client_id",
                      value = "A requested client ID. Note that it is unlikely that this ID will be the final registered ID. This can be seen as more of a suggestion. Max-length is 16 characters",
                      example = "24MSSO", required = true)
    public String clientID;

    @XmlElement(name = "client_name")
    @SerializedName("client_name")
    @ApiModelProperty(name = "client_name", value = "A \"user-friendly\" name that identifies the client",
                      example = "money transfer service", required = true)
    public String clientName;

    @XmlElement(name = "jwt_key")
    @SerializedName("jwt_key")
    @ApiModelProperty(name = "jwt_key",
                      value = "The key to encrypt ID tokens(JWT) with. Note that this will be stored in plaintext in the database",
                      example = "CorrectHorseBatteryStaple", required = true)
    public String jwtKey;
}
