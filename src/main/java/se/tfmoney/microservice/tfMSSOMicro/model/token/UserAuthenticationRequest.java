package se.tfmoney.microservice.tfMSSOMicro.model.token;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Marcus Münger on 2017-05-16.
 */
@XmlRootElement(name = "UserAuthenticationRequest")
@ApiModel(value = "UserAuthenticationRequest", description = "A request for authentication towards a set of clients")
public class UserAuthenticationRequest
{
    @XmlElement(name = "client_id")
    @SerializedName("client_id")
    @ApiModelProperty(name = "client_id",
                      value = "A list of clients to authenticate towards. All these clients will be able to use the authentication token.",
                      required = true)
    public String[] clientID;

    @XmlElement(name = "username")
    @ApiModelProperty(name = "username", value = "The username of the user to authenticate", required = true,
                      example = "DudeMaster84")
    public String username;

    @XmlElement(name = "password")
    @ApiModelProperty(name = "password", value = "The password associated with the user that is being authenticated",
                      required = true, example = "S0_s3CUr3")
    public String password;
}
