package se.tfmoney.microservice.tfMSSOMicro.model.token;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Marcus Münger on 2017-05-16.
 */
@XmlRootElement(name = "AuthorizationCode")
@ApiModel(value = "AuthorizationCode",
          description = "An authorization code that can be exchanged by a client into an access token. An authorization code is a code representing a logged in user")
public class AuthorizationCode
{
    @Id
    @Column(name = "token")
    @XmlElement(name = "token")
    @ApiModelProperty(name = "token", value = "the token")
    public String token;
}
