package se.tfmoney.microservice.tfMSSOMicro.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Marcus Münger on 2017-05-12.
 */
@XmlRootElement(name = "AuthenticationToken")
@ApiModel(
        description = "A public used authentication token. This token, if valid is all that is needed to get privileges to the systems")
public class AuthenticationToken
{
    @XmlElement(name = "token")
    @ApiModelProperty(name = "token", value = "The unique authentication token ID", example = "RsT5OjbzRn430zqMLgV3Ia")
    public String token;
}
