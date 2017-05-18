package se.tfmoney.microservice.oauth.util.swagger;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Marcus Münger on 2017-04-25.
 */
public class ApiOriginFilter implements javax.servlet.Filter
{
    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException
    {
        HttpServletResponse res = (HttpServletResponse) response;
        res.addHeader("Access-Control-Allow-Origin", "*");
        res.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
        res.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy()
    {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }
}
