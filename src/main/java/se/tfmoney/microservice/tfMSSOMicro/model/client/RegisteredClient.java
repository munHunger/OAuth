package se.tfmoney.microservice.tfMSSOMicro.model.client;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Marcus Münger on 2017-05-16.
 */
@Entity
@Table(name = "client")
@ApiModel(
        description = "Information about a newly registered client. Note that this should NEVER leave the client and can only be sent out once")
@XmlRootElement(name = "client")
public class RegisteredClient
{
    @Id
    @Column(name = "client_id", length = 32)
    @XmlElement(name = "client_id")
    @SerializedName("client_id")
    @ApiModelProperty(name = "client_id",
                      value = "An identifier for this client. This is public information. Length of this is 32 characters",
                      example = "24MSSOiu4536jb2354i5u34", required = true)
    public String clientID;

    @Column(name = "client_secret", length = 64)
    @XmlElement(name = "client_secret")
    @SerializedName("client_secret")
    @ApiModelProperty(name = "client_secret",
                      value = "A client secret. Note that this is PRIVATE information and should NEVER leave the client. This means that it can never be sent to frontend. Length of this 128 characters",
                      example = "iu4536jb2354i5u34df689g76sd9f8gsdf876sdf8g76", required = true)
    public String clientSecret;
}
