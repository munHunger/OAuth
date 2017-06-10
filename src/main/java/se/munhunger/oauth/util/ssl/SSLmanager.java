package se.munhunger.oauth.util.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * Created by marcu on 2017-06-10.
 */
public class SSLmanager
{
	public static void init()
	{
		try
		{
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			Path ksPath = Paths.get(System.getProperty("java.home"), "lib", "security", "cacerts");
			keyStore.load(Files.newInputStream(ksPath), "changeit".toCharArray());
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			URL certPath = SSLmanager.class.getClassLoader().getResource("cert.der");
			try(FileInputStream caInput = new FileInputStream(certPath.getFile()))
			{
				Certificate crt = cf.generateCertificate(caInput);
				keyStore.setCertificateEntry("DSTRootCAX3", crt);
			}
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(keyStore);
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, tmf.getTrustManagers(), null);
			SSLContext.setDefault(sslContext);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}