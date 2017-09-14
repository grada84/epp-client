package net.lacnic.epp;

import java.io.IOException;
import java.util.Map;

import net.lacnic.epp.util.TipoXML;
import net.lacnic.web.registro.api.request.UserRequest;

public class UserWrapper {
	private String modificador;

	public UserWrapper(String modificador) {
		setModificador(modificador);
	}

	public String armarEdicionUsuario(UserRequest user) throws IOException {
		Map<String, Object> mapa = createMapUser(user);
		String s = UtilWrapper.procesarXML(TipoXML.CONTACT_UPDATE, mapa);
		return s;
	}

	public String armarNuevoContactoLACNIC(UserRequest user) throws IOException {
		Map<String, Object> mapa = createMapUser(user);

		String s = UtilWrapper.procesarXML(TipoXML.CONTACT_CREATE, mapa);
		return s;
	}

	private Map<String, Object> createMapUser(UserRequest user) throws IOException {
		Map<String, Object> mapa = new UtilWrapper(getModificador()).createMapCommons(user.getUser(), user.getIp());
		mapa.put("id", user.getId());
		mapa.put("name", user.getName());
		mapa.put("email", user.getEmail());
		mapa.put("password", user.getPassword());
		mapa.put("reminder_password", user.getReminder_password());
		mapa.put("language", user.getLanguage());
		mapa.put("generic_address", UtilWrapper.subArmaGenericAddress(user.getAddress()));
		mapa.put("generic_phone", UtilWrapper.subArmaGenericPhone(user.getPhone()));

		return mapa;
	}

	public String getModificador() {
		return modificador;
	}

	public void setModificador(String modificador) {
		this.modificador = modificador;
	}
}
