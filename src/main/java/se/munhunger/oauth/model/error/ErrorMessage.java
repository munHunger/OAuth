package se.munhunger.oauth.model.error;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Marcus Münger on 2017-04-28.
 */
@XmlRootElement(name = "ErrorMessage")
@ApiModel(description = "A code/message pair describing what went wrong")
public class ErrorMessage
{
    @XmlElement(name = "code")
    @ApiModelProperty(value = "The code describing what went wrong", example = "Error during division")
    public String code;
    @XmlElement(name = "message")
    @ApiModelProperty(value = "A text describing why it went wrong", example = "Divisor was 0")
    public String message;

    public ErrorMessage()
    {
    }

    public ErrorMessage(String code, String message)
    {
        this.code = code;
        this.message = message;
    }
}
