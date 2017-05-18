package se.tfmoney.microservice.tfMSSOMicro.model.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Marcus Münger on 2017-05-17.
 */
@Entity
@Table(name = "user")
@XmlRootElement(name = "user")
@ApiModel(value = "User", description = "A representation of a user")
public class User
{
    @Id
    @Column(name = "username", length = 64)
    @XmlElement(name = "username")
    @ApiModelProperty(name = "username", value = "The username, or identification of the user")
    public String username;
    @Column(name = "password", length = 64)
    @XmlElement(name = "password")
    @ApiModelProperty(name = "password", value = "The password of the user. This will be hashed in the database")
    public String password;
}
