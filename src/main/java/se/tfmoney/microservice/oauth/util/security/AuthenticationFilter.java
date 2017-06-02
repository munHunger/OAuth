package se.tfmoney.microservice.oauth.util.security;

import se.tfmoney.microservice.oauth.model.error.ErrorMessage;
import se.tfmoney.microservice.oauth.model.token.NonceToken;
import se.tfmoney.microservice.oauth.util.annotations.NonceRequired;
import se.tfmoney.microservice.oauth.util.jwt.JSONWebToken;
import se.tfmoney.microservice.oauth.util.nonce.NonceUtils;
import se.tfmoney.microservice.oauth.util.oauth.OAuthUtils;

import javax.annotation.Priority;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Marcus Münger on 2017-05-19.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter
{
    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException
    {
        String nonceToken = "invalidToken";
        try
        {
            nonceToken = NonceToken.generateToken().token;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        Method method = resourceInfo.getResourceMethod();
        if (method.isAnnotationPresent(NonceRequired.class))
        {
            String token = requestContext.getHeaderString("nonce");
            try
            {
                if (!NonceUtils.isNonceValid(token))
                {
                    requestContext.abortWith(Response.status(HttpServletResponse.SC_UNAUTHORIZED)
                                                     .entity(new ErrorMessage("Unauthorized",
                                                                              "Could not verify nonce token"))
                                                     .header("nonce", nonceToken)
                                                     .build());
                    return;
                }
            } catch (Exception e)
            {
                requestContext.abortWith(Response.status(HttpServletResponse.SC_UNAUTHORIZED)
                                                 .entity(new ErrorMessage("Error validating nonce token",
                                                                          "Token could not be validated and the validator threw an exception"))
                                                 .header("nonce", nonceToken)
                                                 .build());
                return;
            }
        }
        if (!method.isAnnotationPresent(PermitAll.class))
        {
            if (method.isAnnotationPresent(DenyAll.class))
            {
                requestContext.abortWith(Response.status(HttpServletResponse.SC_UNAUTHORIZED)
                                                 .entity("")
                                                 .header("nonce", nonceToken)
                                                 .build());
                return;
            }
            if (method.isAnnotationPresent(RolesAllowed.class))
            {
                RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
                List<String> rolesAllowed = Arrays.asList(rolesAnnotation.value());
                String authzHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
                authzHeader = authzHeader == null || authzHeader.length() < "Bearer ".length() ? null : authzHeader.substring(
                        "Bearer ".length());
                if (!JSONWebToken.isSigned(authzHeader))
                {
                    try
                    {
                        authzHeader = OAuthUtils.convertToAccessToken(authzHeader).accessToken;
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        requestContext.abortWith(Response.status(HttpServletResponse.SC_UNAUTHORIZED)
                                                         .entity(new ErrorMessage("Error getting access code",
                                                                                  "Could not convert authentication token into an access token"))
                                                         .header("nonce", nonceToken)
                                                         .build());
                        return;
                    }
                }
                try
                {
                    if (OAuthUtils.isAuthenticated(authzHeader))
                    {
                        SecurityContext oldContext = requestContext.getSecurityContext();
                        ApplicationSecurityContext applicationSecurityContext = new ApplicationSecurityContext(
                                authzHeader, oldContext.isSecure());
                        requestContext.setSecurityContext(applicationSecurityContext);
                        boolean allowed = false;
                        for (String role : rolesAllowed)
                            allowed = allowed || applicationSecurityContext.isUserInRole(role);
                        if (!allowed)
                        {
                            requestContext.abortWith(Response.status(HttpServletResponse.SC_UNAUTHORIZED)
                                                             .entity("")
                                                             .header("nonce", nonceToken)
                                                             .build());
                            return;
                        }
                    }
                    else
                    {
                        requestContext.abortWith(Response.status(HttpServletResponse.SC_UNAUTHORIZED)
                                                         .entity("")
                                                         .header("nonce", nonceToken)
                                                         .build());
                        return;
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
