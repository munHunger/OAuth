package se.tfmoney.microservice.oauth.util.http;

import com.google.gson.Gson;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import se.tfmoney.microservice.oauth.model.http.HttpResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for making HTTP requests.
 * This class manages CRUD operations of different types
 * Created by Marcus Münger on 2017-04-27.
 */
public class HttpRequest
{
    /**
     * Create a PUT request.
     *
     * @param url        the url to PUT to.
     * @param data       the data to send. Note that this will be converted to a JSON.
     * @param returnType the class to return to. i.e. if the result should be of type T, then returnType should be
     *                   T.class.
     *
     * @return A @{@link HttpResponse} object with the statuscode of the request as well as the data returned from the
     * server
     *
     * @throws IOException
     * @throws UnauthorizedException
     */
    public static HttpResponse putRequest(String url, Object data,
                                          Class returnType) throws IOException, UnauthorizedException
    {
        return request(url, "PUT", data, returnType);
    }

    /**
     * Create a POST request.
     *
     * @param url        the url to POST to.
     * @param data       the data to send. Note that this will be converted to a JSON.
     * @param returnType the class to return to. i.e. if the result should be of type T, then returnType should be
     *                   T.class.
     *
     * @return A @{@link HttpResponse} object with the statuscode of the request as well as the data returned from the
     * server
     *
     * @throws IOException
     * @throws UnauthorizedException
     */
    public static HttpResponse postRequest(String url, Object data,
                                           Class returnType) throws IOException, UnauthorizedException
    {
        return request(url, "POST", data, returnType);
    }

    /**
     * Sends a POST request
     *
     * @param url        the url to request
     * @param data       the data to send in the body. This should either be null or something that can be parsed as a
     *                   JSON.
     * @param headers    a map of key-value pairs to use as headers.
     * @param parameters a map of key-value pairs to use as GET query parameters
     * @param returnType the class to return to. i.e. if the result should be of type T, then returnType should be
     *                   T.class.
     *
     * @return A @{@link HttpResponse} object with the statuscode of the request as well as the data returned from the
     * server
     *
     * @throws IOException
     * @throws UnauthorizedException
     */
    public static HttpResponse postRequest(String url, Map<String, String> headers, Map<String, String> parameters,
                                           Object data, Class returnType) throws IOException, UnauthorizedException
    {
        StringBuilder parameterBuilder = new StringBuilder();
        for (String key : parameters.keySet())
            parameterBuilder.append(
                    (parameterBuilder.length() == 0 ? "?" : "&") + key + "=" + URLEncoder.encode(parameters.get(key),
                                                                                                 "UTF-8"));
        url += parameterBuilder.toString();
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        for (String header : headers.keySet())
            con.setRequestProperty(header, headers.get(header));

        String json = data != null ? new Gson().toJson(data) : "";
        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
        out.write(json);
        out.close();
        return parseInput(con, returnType);
    }

    /**
     * Sends a POST request with the body as form-data.
     *
     * @param url        the url to POST to
     * @param form       a map of key value pairs to use in the form-data
     * @param returnType the class to return to. i.e. if the result should be of type T, then returnType should be
     *                   T.class.
     *
     * @return A @{@link HttpResponse} object with the statuscode of the request as well as the data returned from the
     * server
     *
     * @throws IOException
     * @throws UnauthorizedException
     */
    public static HttpResponse postForm(String url, Map<String, String> form,
                                        Class returnType) throws IOException, UnauthorizedException
    {
        return postForm(url, new HashMap<>(), form, returnType);
    }

    /**
     * Sends a POST request with the body as form-data.
     *
     * @param url        the url to POST to
     * @param headers    the headers to send along with the data
     * @param form       a map of key value pairs to use in the form-data
     * @param returnType the class to return to. i.e. if the result should be of type T, then returnType should be
     *                   T.class.
     *
     * @return A @{@link HttpResponse} object with the statuscode of the request as well as the data returned from the
     * server
     *
     * @throws IOException
     * @throws UnauthorizedException
     */
    public static HttpResponse postForm(String url, Map<String, String> headers, Map<String, String> form,
                                        Class returnType) throws IOException, UnauthorizedException
    {

        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);
        for (String key : form.keySet())
            postMethod.addParameter(key, form.get(key));

        for (String key : headers.keySet())
            postMethod.setRequestHeader(key, headers.get(key));

        httpClient.executeMethod(postMethod);
        int statusCode = postMethod.getStatusCode();
        if (statusCode != HttpServletResponse.SC_UNAUTHORIZED)
        {
            String response = postMethod.getResponseBodyAsString();
            if (returnType != null)
            {
                return new HttpResponse(new Gson().fromJson(response, returnType), statusCode);
            }
            return new HttpResponse(postMethod.getResponseBodyAsString(), statusCode);
        }
        else
            throw new se.tfmoney.microservice.oauth.util.http.UnauthorizedException();
    }

    /**
     * Create a request with a specified method.
     *
     * @param url        the url to request.
     * @param method     the method to use. PUT, POST...
     * @param data       the data to send. Note that this will be converted to a JSON.
     * @param returnType the class to return to. i.e. if the result should be of type T, then returnType should be
     *                   T.class.
     *
     * @return A @{@link HttpResponse} object with the statuscode of the request as well as the data returned from the
     * server
     *
     * @throws IOException
     * @throws UnauthorizedException
     */
    private static HttpResponse request(String url, String method, Object data,
                                        Class returnType) throws IOException, UnauthorizedException
    {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setDoOutput(true);
        con.setRequestMethod(method);
        con.setRequestProperty("Content-Type", "application/json");
        String json = new Gson().toJson(data);
        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
        out.write(json);
        out.close();
        return parseInput(con, returnType);
    }

    /**
     * @throws IOException
     * @throws UnauthorizedException
     * @see #getRequest(String, Map, Map, Class)
     */
    public static HttpResponse getRequest(String url, Class returnType) throws IOException, UnauthorizedException
    {
        return getRequest(url, new HashMap<>(), returnType);
    }

    /**
     * @throws IOException
     * @throws UnauthorizedException
     * @see #getRequest(String, Map, Map, Class)
     */
    public static HttpResponse getRequest(String url, Map<String, String> headers,
                                          Class returnType) throws IOException, UnauthorizedException
    {
        return getRequest(url, headers, new HashMap<>(), returnType);
    }

    /**
     * Sends a GET request
     *
     * @param url        the url to request
     * @param headers    a map of key-value pairs to use as headers.
     * @param parameters a map of key-value pairs to use as GET query parameters
     * @param returnType the class to return to. i.e. if the result should be of type T, then returnType should be
     *                   T.class.
     *
     * @return A @{@link HttpResponse} object with the statuscode of the request as well as the data returned from the
     * server
     *
     * @throws IOException
     * @throws UnauthorizedException
     */
    public static HttpResponse getRequest(String url, Map<String, String> headers, Map<String, String> parameters,
                                          Class returnType) throws IOException, UnauthorizedException
    {
        StringBuilder parameterBuilder = new StringBuilder();
        for (String key : parameters.keySet())
            parameterBuilder.append((parameterBuilder.length() == 0 ? "?" : "&") + key + "=" + parameters.get(key));
        url += parameterBuilder.toString();
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        for (String header : headers.keySet())
            con.setRequestProperty(header, headers.get(header));
        return parseInput(con, returnType);
    }

    /**
     * Reads the input of a HttpURLConnection and returns it in a appropriate format.
     * This function will also check that the responseCode seems OK, or Unauthorized.
     *
     * @param con        the HTTP connection to fetch data from and to request with
     * @param returnType the class to return to. i.e. if the result should be of type T, then returnType should be
     *                   T.class.
     *
     * @return A @{@link HttpResponse} object with the statuscode of the request as well as the data returned from the
     * server
     *
     * @throws IOException
     * @throws UnauthorizedException
     */
    private static HttpResponse parseInput(HttpURLConnection con,
                                           Class returnType) throws IOException, UnauthorizedException
    {
        int code = con.getResponseCode();
        if (code != 401)
        {
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader((code < 300 && code >= 200) ? con.getInputStream() : con.getErrorStream())))
            {
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null)
                {
                    response.append(inputLine);
                }
                if (returnType != null)
                {
                    return new HttpResponse(new Gson().fromJson(response.toString(), returnType), code);
                }
                Map<String, String> headers = new HashMap<>();
                Map<String, List<String>> httpHeaders = con.getHeaderFields();
                httpHeaders.keySet().stream().forEach(key -> headers.put(key, httpHeaders.get(key).get(0)));
                return new HttpResponse(response.toString(), code, headers);
            }
        }
        else
        {
            throw new UnauthorizedException();
        }
    }
}