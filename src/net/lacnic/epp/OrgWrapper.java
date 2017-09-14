package net.lacnic.epp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.lacnic.epp.util.TipoXML;
import net.lacnic.web.registro.api.request.Contact;
import net.lacnic.web.registro.api.request.OrgRequest;
import net.lacnic.web.registro.api.request.TipoContacto;

public class OrgWrapper {

	private String modificador;

	public OrgWrapper(String modificador) {
		setModificador(modificador);
	}

	public String armarNuevaOrgLACNIC(OrgRequest orgRequest) throws IOException {
		Map<String, Object> mapa = createMapOrg(orgRequest);
		String s = UtilWrapper.procesarXML(TipoXML.ORG_CREATE, mapa);
		return s;
	}

	public String armarEdicionOrg(OrgRequest orgRequest) throws IOException {
		Map<String, Object> mapa = createMapOrg(orgRequest);
		String s = UtilWrapper.procesarXML(TipoXML.ORG_UPDATE, mapa);
		return s;
	}
	
	public String armarEdicionOrgContactos(OrgRequest orgRequest) throws IOException {
		Map<String, Object> mapa = new UtilWrapper(getModificador()).createMapCommons(orgRequest.getUser(), orgRequest.getIp());
		mapa.put("id", orgRequest.getId());
		mapa.put("org_contacts", subArmarContactsOrg(orgRequest.getContacts()));

		String s = UtilWrapper.procesarXML(TipoXML.ORG_UPDATE_CONTACTS, mapa);
		return s;
	}
	

	public String armarEdicionOrgContacto(String user, String ip, String identificadorOrganizacion, String handleContacto, TipoContacto tipoContacto) throws IOException {
		Map<String, Object> mapa = new UtilWrapper(getModificador()).createMapCommons(user, ip);

		mapa.put("organization", identificadorOrganizacion);
		mapa.put("handle", handleContacto);
		mapa.put("contact_type", tipoContacto.toString());
		String s = UtilWrapper.procesarXML(TipoXML.ORG_UPDATE_CONTACT, mapa);

		return s;
	}

	private Map<String, Object> createMapOrg(OrgRequest orgRequest) throws IOException {
		Map<String, Object> mapa = new UtilWrapper(getModificador()).createMapCommons(orgRequest.getUser(), orgRequest.getIp());

		mapa.put("id", orgRequest.getId());
		mapa.put("name", orgRequest.getName());
		mapa.put("legacy", String.valueOf(orgRequest.getLegacy()).toLowerCase());
		mapa.put("renewal_date", orgRequest.getRenewal_date());

		mapa.put("org_contacts", subArmarContactsOrg(orgRequest.getContacts()));

		mapa.put("responsible", orgRequest.getResponsible());
		mapa.put("type", orgRequest.getType().toString());
		mapa.put("epp_password", orgRequest.getEpp_password());
		mapa.put("resources_class", orgRequest.getResources_class().toString());
		mapa.put("epp_status", orgRequest.getEpp_status().toString());

		final String xml = UtilWrapper.subArmaGenericAddress(orgRequest.getAddress());
		mapa.put("generic_address", xml);
		mapa.put("generic_phone", UtilWrapper.subArmaGenericPhone(orgRequest.getPhone()));
		mapa.put("epp_ip_authorized", UtilWrapper.subArmarNuevaOrgLACNICEppIP(orgRequest.getEpp_ip_authorized()));
		mapa.put("category", UtilWrapper.subArmarNuevaOrgLACNICRenewallType(orgRequest.getCategory()));

		return mapa;
	}

	private String subArmarContactsOrg(List<Contact> contacts) throws IOException {
		Map<String, Object> mapa = UtilWrapper.createEmptyCommons();
		String r = "";

		for (Contact contact : contacts) {
			mapa.put("contact_type", contact.getTipoContacto().toString());
			mapa.put("handle", contact.getHandle());
			r = r.concat(UtilWrapper.procesarXML(TipoXML.ORG_CONTACT, mapa));
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
