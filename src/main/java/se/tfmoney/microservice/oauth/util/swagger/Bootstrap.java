package se.tfmoney.microservice.oauth.util.swagger;

import io.swagger.jaxrs.config.BeanConfig;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Created by Marcus Münger on 2017-05-19.
 */
public class Bootstrap extends HttpServlet
{
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.2");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost");
        beanConfig.setBasePath("/api");
        beanConfig.setResourcePackage("se.tfmoney.microservice.oauth");
        beanConfig.setScan(true);
    }
}
