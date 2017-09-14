package net.lacnic.epp.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.RandomStringUtils;

public class Constantes {

	private static String JBOSSCONFURI() {
		String property = System.getProperty("jboss.server.config.url");

		if (property != null && !property.isEmpty())
			return property.substring(5);
		else {
			property = System.getProperty("user.home");
			if (property != null && !property.isEmpty())
				return property.concat("/sugar-conf/");
			else
				return null;
		}
	}

	public static Properties getEppProperties() {
		Properties pp = new Properties();
		try {
			FileInputStream file = new FileInputStream(JBOSSCONFURI() + "epp.properties");
			pp.load(file);
			System.out.println(pp);
			System.out.println("pp------------------------------------------------------------------------");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pp;
	}

	public static String clTRID(String user, String ip, String modificador) {
		return modificador + "-" + user + "-" + ip + "-" + RandomStringUtils.randomAlphanumeric(16);
	}

	public static String XMLS_FOLDER = JBOSSCONFURI() + "xmls/";
	public static String CERTS_FOLDER = JBOSSCONFURI() + "certs/";
	public static String UTF8 = "UTF-8";
	public static String url = "epp.endpoint.url";
	public static String port = "epp.endpoint.port";
	
	public static String userAdminNumber = "user.admin.number";
	public static String userAdminId = "user.admin.id";
	public static String userAdminPassword = "user.admin.password";
	
	public static String userExtNumber = "user.ext.number";
	public static String userExtId = "user.ext.id";
	public static String userExtPassword = "user.ext.password";
	
	
	
	

}