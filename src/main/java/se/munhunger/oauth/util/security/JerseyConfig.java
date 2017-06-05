package se.munhunger.oauth.util.security;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

/**
 * Created by Marcus Münger on 2017-05-19.
 */
public class JerseyConfig extends ResourceConfig
{
    public JerseyConfig()
    {
        register(RolesAllowedDynamicFeature.class);
    }
}
