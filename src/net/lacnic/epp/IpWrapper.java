package net.lacnic.epp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.lacnic.epp.util.TipoXML;
import net.lacnic.web.registro.api.request.Contact;
import net.lacnic.web.registro.api.request.DsData;
import net.lacnic.web.registro.api.request.IPNetworkRange;
import net.lacnic.web.registro.api.request.IpRequest;
import net.lacnic.web.registro.api.request.ReverseDNS;

public class IpWrapper {

	private String modificador;

	public IpWrapper(String modificador) {
		setModificador(modificador);
	}

	public String armarInfoIP(IpRequest ip) throws IOException {
		Map<String, Object> mapa = createMapIP(ip);
		String s = UtilWrapper.procesarXML(TipoXML.IPNETWORK_INFO, mapa);
		return s;
	}

	public String armarCreateIP(IpRequest ip) throws IOException {
		Map<String, Object> mapa = createMapIP(ip);
		String s = UtilWrapper.procesarXML(TipoXML.IPNETWORK_CREATE, mapa);
		return s;
	}

	public String armarEditarBloque(IpRequest ip) throws IOException {
		Map<String, Object> mapa = createMapIP(ip);
		String s = UtilWrapper.procesarXML(TipoXML.IPNETWORK_UPDATE, mapa);
		return s;
	}

	public String armarEditarAsnAnnouncerBloque(IpRequest ip) throws IOException {
		Map<String, Object> mapa = new UtilWrapper(getModificador()).createMapCommons(ip.getUser(), ip.getIp());
		mapa.put("asn", ip.getAsn());
		mapa.put("roid", ip.getRoid());

		String s = UtilWrapper.procesarXML(TipoXML.IPNETWORK_UPDATE_ASN, mapa);
		return s;
	}

	public String armarEdicionIpContacto(IpRequest ip) throws IOException {
		Map<String, Object> mapa = new UtilWrapper(getModificador()).createMapCommons(ip.getUser(), ip.getIp());
		mapa.put("roid", ip.getRoid());
		mapa.put("ipnetwork_contacts", subArmarContactsBloque(ip.getContacts()));

		String s = UtilWrapper.procesarXML(TipoXML.IPNETWORK_UPDATE_CONTACTS, mapa);
		return s;
	}

	public String armarDeleteIP(IpRequest ip) throws IOException {
		Map<String, Object> mapa = createMapIP(ip);
		String s = UtilWrapper.procesarXML(TipoXML.IPNETWORK_DELETE, mapa);
		return s;
	}
	
	private Map<String, Object> createMapIP(IpRequest ip) throws IOException {
		Map<String, Object> mapa = new UtilWrapper(getModificador()).createMapCommons(ip.getUser(), ip.getIp());
		mapa.put("id", ip.getOrgRequest() != null ? ip.getOrgRequest().getId() : "");
		mapa.put("roid", ip.getRoid());
		mapa.put("type", ip.getType());
		mapa.put("ipnetwork_range", subArmarRange(ip.getIpnetwork_range()));
		mapa.put("ipnetwork_reverses_dns", subArmarReversesDNS(ip.getIpnetwork_reverses_dns()));
		mapa.put("ipnetwork_reverses_dns_add", subArmarReversesDNS(ip.getIpnetwork_reverses_dns_add()));
		mapa.put("ipnetwork_reverses_dns_rem", subArmarReversesDNS(ip.getIpnetwork_reverses_dns_rem()));
		mapa.put("ipnetwork_dsdata_dns_add", subArmarDsDataDNS(ip.getIpnetwork_dsdata_dns_add()));
		mapa.put("ipnetwork_dsdata_dns_rem", subArmarDsDataDNS(ip.getIpnetwork_dsdata_dns_rem()));
		mapa.put("ipnetwork_contacts", subArmarContactsBloque(ip.getContacts()));
		return mapa;
	}

	private String subArmarReversesDNS(List<ReverseDNS> reversesDNS) throws IOException {
		Map<String, Object> mapa = UtilWrapper.createEmptyCommons();
		String r = "";
		for (ReverseDNS reverse : reversesDNS) {
			mapa.put("ipnetwork_range", subArmarRange(reverse.getIpnetwork_range()));
			mapa.put("ipnetwork_reverses_dns_hostnames", subArmarHostnamesParaReverso(reverse.getHostnames()));
			r = r.concat(UtilWrapper.procesarXML(TipoXML.IPNETWORK_REVERSES_DNS, mapa));
		}
		return r;
	}
	
	private String subArmarDsDataDNS(List<DsData> dsDatas) throws IOException {
		Map<String, Object> mapa = UtilWrapper.createEmptyCommons();
		String r = "";
		for (DsData ds : dsDatas) {
			mapa.put("ipnetwork_range", subArmarRange(ds.getIpnetwork_range()));
			mapa.put("keyTag", ds.getKeyTag());
			mapa.put("alg", ds.getAlg());
			mapa.put("digestType", ds.getDigestType());
			mapa.put("digest", ds.getDigest());
			r = r.concat(UtilWrapper.procesarXML(TipoXML.IPNETWORK_DSDATA_DNS, mapa));
		}
		return r;
	}

	private String subArmarRange(IPNetworkRange range) throws IOException {
		Map<String, Object> mapa = UtilWrapper.createEmptyCommons();
		mapa.put("start_address", range.getStart_address());
		mapa.put("end_address", range.getEnd_address());
		mapa.put("version", range.getVersion());
		return UtilWrapper.procesarXML(TipoXML.IPNETWORK_RANGE, mapa);
	}

	private String subArmarContactsBloque(List<Contact> contacts) throws IOException {
		Map<String, Object> mapa = UtilWrapper.createEmptyCommons();
		String r = "";
		for (Contact contact : contacts) {
			mapa.put("contact_type", contact.getTipoContacto().toString());
			mapa.put("handle", contact.getHandle());
			r = r.concat(UtilWrapper.procesarXML(TipoXML.IPNETWORK_CONTACT, mapa));
		}
		return r;
	}

	private String subArmarHostnamesParaReverso(List<String> hostnames) throws IOException {
		Map<String, Object> mapa = UtilWrapper.createEmptyCommons();
		String r = "";
		for (String host : hostnames) {
			mapa.put("hostname", host);
			r = r.concat(UtilWrapper.procesarXML(TipoXML.IPNETWORK_REVERSES_DNS_HOSTNAME, mapa));
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
