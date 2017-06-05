package se.munhunger.oauth.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created by Marcus Münger on 2017-05-19.
 */
@Entity
@Table(name = "roles")
public class UserRoles implements Serializable
{
    @Id
    @Column(name = "username")
    public String username;
    @Id
    @Column(name = "role")
    public String role;

    public UserRoles() {}

    public UserRoles(String username, String role)
    {
        this.username = username;
        this.role = role;
    }
}
