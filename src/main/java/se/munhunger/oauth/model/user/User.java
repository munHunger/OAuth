package se.munhunger.oauth.model.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import se.munhunger.oauth.util.database.jpa.Database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Column(name = "number", length = 12)
    @XmlElement(name = "number")
    @ApiModelProperty(name = "number", value = "The phonenumber of the user")
    public String number;

    public User()
    {
    }

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public String getRolesCSV() throws Exception
    {
        StringBuilder builder = new StringBuilder();
        getRoles().stream().forEach(o -> {
            UserRoles role = (UserRoles) o;
            if (builder.length() > 0)
                builder.append(";");
            builder.append(role.role);
        });
        return builder.toString();
    }

    public List getRoles() throws Exception
    {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("username", username);
        return Database.getObjects("from UserRoles WHERE username = :username", parameters);
    }
}
