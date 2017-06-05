package se.munhunger.oauth.model.token;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Marcus Münger on 2017-06-02.
 */
@XmlRootElement(name = "access_token")
@ApiModel(value = "AccessToken", description = "A JWT access token with a refresh token on the side")
public class AccessToken
{
    @XmlElement(name = "access_token")
    @SerializedName("access_token")
    @ApiModelProperty(name = "access_token", value = "A JWT token of an authenticated user")
    public String accessToken;

    @XmlElement(name = "refresh_token")
    @SerializedName("refresh_token")
    @ApiModelProperty(name = "refresh_token",
                      value = "A token that can be used to re-authenticate the client against the auth service")
    public String refreshToken;
}
