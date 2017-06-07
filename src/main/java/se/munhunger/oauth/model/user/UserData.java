package se.munhunger.oauth.model.user;

import com.google.gson.annotations.SerializedName;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by marcu on 2017-06-06.
 */
@XmlRootElement(name = "UserData")
public class UserData
{
	@XmlElement(name = "username")
	public String username;
	@XmlElement(name = "phone_number")
	@SerializedName("phone_number")
	public String phoneNumber;
	@XmlElement(name = "roles")
	public List<String> roles;
}
