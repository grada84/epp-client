package net.lacnic.epp;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.lacnic.epp.exceptions.EppSplitterException;
import net.lacnic.epp.util.Constantes;
import net.lacnic.epp.util.TipoXML;
import net.lacnic.epp.util.UtilsFiles;
import net.lacnic.web.registro.api.request.Address;
import net.lacnic.web.registro.api.request.Phone;
import net.lacnic.web.registro.api.request.TipoCategoria;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

public class UtilWrapper {

	private static VelocityEngine ve;
	private static VelocityContext context;
	private String modificador;

	public UtilWrapper(String modificador) {
		if (ve == null || context == null) {
			ve = new VelocityEngine();
			ve.init();
		}
		setModificador(modificador);
	}

	public Map<String, Object> createMapCommons(String user, String ip) {
		Map<String, Object> mapa = new HashMap<String, Object>();
		mapa.put("clTRID", Constantes.clTRID(user, ip, getModificador()));
		return mapa;
	}

	public static Map<String, Object> createEmptyCommons() {
		Map<String, Object> mapa = new HashMap<String, Object>();
		return mapa;
	}

	static String subArmaGenericAddress(Address address) throws IOException {
		if (address == null)
			return "";
		Map<String, Object> mapa = UtilWrapper.createEmptyCommons();
		String r = "";
		mapa.put("street_address", address.getStreet_address());
		mapa.put("number_address", address.getNumber_address());
		mapa.put("complement_address", address.getComplement_address());
		mapa.put("city", address.getCity());
		mapa.put("state", address.getState());
		mapa.put("pc", address.getPc());
		mapa.put("country", address.getCountry());
		r = UtilWrapper.procesarXML(TipoXML.GENERIC_ADDRESS, mapa);
		return r;
	}

	static String subArmaGenericPhone(Phone phone) throws IOException {
		if (phone == null)
			return "";
		Map<String, Object> mapa = UtilWrapper.createEmptyCommons();
		String r = "";
		mapa.put("phone_number", "+" + phone.getCountry_code() + "." + phone.getArea_code() + phone.getPhone_number());
		mapa.put("phone_extension", phone.getPhone_extension());

		r = UtilWrapper.procesarXML(TipoXML.GENERIC_PHONE, mapa);
		return r;
	}

	static String subArmarNuevaOrgLACNICEppIP(List<String> eppIPs) throws IOException {
		Map<String, Object> mapa = UtilWrapper.createEmptyCommons();
		String r = "";

		for (String eppIP : eppIPs) {
			mapa.put("eppIP", eppIP);
			r = r.concat(UtilWrapper.procesarXML(TipoXML.ORG_CREATE_EPP_IP, mapa));
		}

		return r;
	}

	static String subArmarNuevaOrgLACNICRenewallType(List<TipoCategoria> renewalTypes) throws IOException {
		Map<String, Object> mapa = UtilWrapper.createEmptyCommons();
		String r = "";

		for (TipoCategoria renewalType : renewalTypes) {
			mapa.put("renewalType", renewalType.toString());
			r = r.concat(UtilWrapper.procesarXML(TipoXML.ORG_CREATE_RENEWALL_TYPE, mapa) + "\n");
		}

		return r;
	}

	private static String processTemplate(String template, Map<String, Object> datos) {
		try {
			context = new VelocityContext(datos);

			StringWriter w = new StringWriter();
			boolean a = Velocity.evaluate(context, w, "email-template", template);
			if (a)
				return w.toString();
			else
				return "";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String procesarXML(TipoXML tipoXML, Map<String, Object> datos) throws IOException {
		String url = Constantes.XMLS_FOLDER + tipoXML.toString().toLowerCase() + ".xml";
		byte[] encoded = UtilsFiles.getBytesFromFile(new File(url));
		String xml = new String(encoded, Constantes.UTF8);
		String res = processTemplate(xml, datos);

		try {
			res = EppSplitter.split(res);
		} catch (EppSplitterException ese) {
			System.err.println(ese);
		}
		System.out.println(res);
		return res;
	}

	public static String procesarTexto(String texto, Map<String, Object> datos) throws IOException {
		String res = processTemplate(texto, datos);
		return res;
	}

	public static String armarHello() throws IOException {
		String s = procesarXML(TipoXML.HELLO, new HashMap<String, Object>());
		return s;
	}

	public String armarLoginAdmin(String user, String pass, String userId) throws IOException {
		Map<String, Object> mapa = createMapCommons(userId, "127.0.0.1");
		mapa.put("clID", user);
		mapa.put("pw", pass);
		mapa.put("version", "1.0");
		mapa.put("lang", "en");
		mapa.put("extURI", armarLoginExtUri());
		String s = procesarXML(TipoXML.LOGIN, mapa);
		return s;
	}

	public String armarLoginExterno(String userEpp, String pass, String userId) throws IOException {
		Map<String, Object> mapa = createMapCommons(userId, "127.0.0.1");
		mapa.put("clID", userEpp);
		mapa.put("pw", pass);
		mapa.put("version", "1.0");
		mapa.put("lang", "en");
		mapa.put("extURI", armarLoginExtUri());
		String s = procesarXML(TipoXML.LOGIN, mapa);
		return s;
	}

	private static String armarLoginExtUri() throws IOException {
		Map<String, Object> mapa = createEmptyCommons(); // revisar NICO F
															// NUEVO
		String s = procesarXML(TipoXML.LOGIN_EXT_URI, mapa);
		return s;
	}

	public String armarLogutAdmin(String userId) throws IOException {
		Map<String, Object> mapa = createMapCommons(userId, "127.0.0.1");
															// NUEVO
		String s = procesarXML(TipoXML.LOGOUT, mapa);
		return s;

	}

	public String armarLogutExt(String userId) throws IOException {
		Map<String, Object> mapa = createMapCommons(userId, "127.0.0.1");
																// NUEVO
		String s = procesarXML(TipoXML.LOGOUT, mapa);
		return s;

	}

	public String getModificador() {
		return modificador;
	}

	public void setModificador(String modificador) {
		this.modificador = modificador;
	}

}
