package net.lacnic.epp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.lacnic.epp.util.TipoXML;
import net.lacnic.web.registro.api.request.ASNRequest;
import net.lacnic.web.registro.api.request.Contact;

public class AsnWrapper {

	private String modificador;

	public AsnWrapper(String modificador) {
		setModificador(modificador);
	}

	public String armarInfoASN(ASNRequest asn) throws IOException {
		Map<String, Object> mapa = createMapASN(asn);
		String s = UtilWrapper.procesarXML(TipoXML.ASN_INFO, mapa);
		return s;
	}

	public String armarEdicionASNContacto(ASNRequest asn) throws IOException {
		Map<String, Object> mapa = createMapASN(asn);
		mapa.put("asn_contacts", subArmarContactsASN(asn.getContacts()));

		return UtilWrapper.procesarXML(TipoXML.ASN_UPDATE_CONTACTS, mapa);
	}

	private Map<String, Object> createMapASN(ASNRequest asn) throws IOException {
		Map<String, Object> mapa = new UtilWrapper(getModificador()).createMapCommons(asn.getUser(), asn.getIp());
		mapa.put("number", asn.getNumber());
		return mapa;
	}

	private String subArmarContactsASN(List<Contact> contacts) throws IOException {
		Map<String, Object> mapa = UtilWrapper.createEmptyCommons();
		String r = "";

		for (Contact contact : contacts) {
			mapa.put("contact_type", contact.getTipoContacto().toString());
			mapa.put("handle", contact.getHandle());
			r = r.concat(UtilWrapper.procesarXML(TipoXML.ASN_CONTACT, mapa));
		}

		return r;
	}

	public String getModificador() {
		return modificador;
	}

	public void setModificador(String modificador) {
		this.modificador = modificador;
	}

}
