package net.lacnic.epp.interfaces;

import net.lacnic.web.registro.api.request.ASNRequest;
import net.lacnic.web.registro.api.request.IpRequest;
import net.lacnic.web.registro.api.request.OrgRequest;
import net.lacnic.web.registro.api.request.TipoContacto;
import net.lacnic.web.registro.api.request.UserRequest;
import net.lacnic.web.registro.api.response.ResponseObject;

/**
 * Interfaz que define los métodos expuestos por el cliente.
 *
 */
public interface EppInterface {

	/**
	 * 
	 * @param org
	 * @return
	 * @throws Exception
	 */
	ResponseObject editarOrganizacion(OrgRequest org) throws Exception;

	/**
	 * 
	 * @param org
	 * @return
	 * @throws Exception
	 */
	ResponseObject crearOrganizacionLACNIC(OrgRequest org) throws Exception;

	ResponseObject editarOrganizacionContacto(String user, String ip, String identificadorOrganizacion, String handleContacto, TipoContacto tipoContacto) throws Exception;

	ResponseObject editarOrganizacionContactos(OrgRequest org) throws Exception;

	/**
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	ResponseObject modificarUsuario(UserRequest user) throws Exception;

	/**
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	ResponseObject crearUsuarioLACNIC(UserRequest user) throws Exception;

	ResponseObject crearUsuarioLACNIC(String xml) throws Exception;

	ResponseObject infoIP(IpRequest ip) throws Exception;

	ResponseObject createIP(IpRequest ip) throws Exception;

	ResponseObject editarIP(IpRequest ip) throws Exception;

	ResponseObject editarAsnAnnouncerIP(IpRequest ip) throws Exception;

	ResponseObject editarIPContacto(IpRequest ip) throws Exception;

	ResponseObject deleteIP(IpRequest ip) throws Exception;

	ResponseObject createDomain(IpRequest ip) throws Exception;

	ResponseObject deleteDomain(IpRequest ip) throws Exception;

	/**
	 * Subasigna recursos y crea la organización que será dueña del bloque
	 * subasignado
	 * 
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	ResponseObject subasignarYCrearOrg(IpRequest ip) throws Exception;

	String infoASN(ASNRequest ip) throws Exception;

	ResponseObject editarASNContacto(ASNRequest asn) throws Exception;

}
