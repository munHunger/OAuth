package se.munhunger.oauth;

import se.munhunger.oauth.util.security.JerseyConfig;
import se.munhunger.oauth.util.ssl.SSLmanager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by Marcus Münger on 2017-06-02.
 */
public class Startup implements ServletContextListener
{
	@Override
	public void contextInitialized(ServletContextEvent arg0)
	{
		SSLmanager.init();
		new JerseyConfig();
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0)
	{
	}
}