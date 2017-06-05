package se.munhunger.oauth.model.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Marcus Münger on 2017-04-28.
 */
public class HttpResponse
{
    public Object data;
    public int statusCode;
    public Map<String, String> headers;

    public HttpResponse(Object data, int statusCode, Map<String, String> headers)
    {
        this.data = data;
        this.statusCode = statusCode;
        this.headers = headers;
    }

    public HttpResponse(Object data, int statusCode)
    {
        this(data, statusCode, new HashMap<>());
    }

    /**
     * Checks if the status code is an error code or not.
     * i.e. if 200 <= statusCode < 300 holds
     *
     * @return true iff the status code is an error code or not
     */
    public boolean isErrorCode()
    {
        return !(statusCode >= 200 && statusCode < 300);
    }
}
