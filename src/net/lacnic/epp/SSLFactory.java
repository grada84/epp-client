package net.lacnic.epp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import net.lacnic.epp.util.Constantes;

public class SSLFactory {

	public static SSLSocket createSSLSocket() {
		try {

			Properties properties = Constantes.getEppProperties();
			SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
			String host = properties.getProperty(Constantes.url);
			Integer port = Integer.valueOf(properties.getProperty(Constantes.port));
			SSLSocket sslSocket = (SSLSocket) sf.createSocket(host, port);
			return sslSocket;

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
