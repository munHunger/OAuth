package se.tfmoney.microservice.oauth.util.security;

import se.tfmoney.microservice.oauth.util.oauth.OAuthUtils;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * Created by Marcus Münger on 2017-05-19.
 */
public class ApplicationSecurityContext implements SecurityContext
{
    private AuthToken authz;
    private boolean secure;

    public ApplicationSecurityContext(String auth, boolean secure)
    {
        this.authz = new AuthToken(auth);
        this.secure = secure;
    }

    @Override
    public Principal getUserPrincipal()
    {
        return this.authz;
    }

    @Override
    public boolean isUserInRole(String role)
    {
        try
        {
            return OAuthUtils.hasAnyRole(authz.getName(), role);
        } catch (Exception e)
        {
            return false;
        }
    }

    @Override
    public boolean isSecure()
    {
        return secure;
    }

    @Override
    public String getAuthenticationScheme()
    {
        return "Bearer";
    }
}
