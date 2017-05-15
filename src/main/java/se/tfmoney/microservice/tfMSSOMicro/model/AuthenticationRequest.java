package se.tfmoney.microservice.tfMSSOMicro.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Marcus Münger on 2017-05-12.
 */
@XmlRootElement(name = "AuthenticationRequest")
@ApiModel
public class AuthenticationRequest
{
    @XmlElement(name = "username")
    @ApiModelProperty(name = "username", value = "A users unique identifying name", example = "dudemaster47")
    public String username;

    @XmlElement(name = "password")
    @ApiModelProperty(name = "password", value = "The password of the specified user", example = "S0s3cUr3")
    public String password;
}
