package se.munhunger.oauth.model.client;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by Marcus Münger on 2017-05-16.
 */
@Entity
@Table(name = "client_url")
@XmlRootElement(name = "ClientURL")
@ApiModel(value = "ClientURL",
          description = "A valid redirect URL. i.e. a URL that is registered for return after a valid user authentication")
public class ClientURL implements Serializable
{
    @Id
    @Column(name = "client_id", length = 32)
    public String clientID;

    @Id
    @Column(name = "url", length = 256)
    @XmlElement(name = "url")
    @ApiModelProperty(value = "A valid url that should reasonably be an endpoint on the client system")
    public String url;

    public ClientURL()
    {}

    public ClientURL(String clientID, String url)
    {
        this.clientID = clientID;
        this.url = url;
    }
}
