package net.lacnic.epp;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.net.ssl.SSLSocket;

import net.lacnic.epp.exceptions.EppException;
import net.lacnic.epp.interfaces.EppInterface;
import net.lacnic.epp.util.UtilsFiles;
import net.lacnic.web.registro.api.request.ASNRequest;
import net.lacnic.web.registro.api.request.IpRequest;
import net.lacnic.web.registro.api.request.OrgRequest;
import net.lacnic.web.registro.api.request.ReverseDNS;
import net.lacnic.web.registro.api.request.TipoContacto;
import net.lacnic.web.registro.api.request.UserRequest;
import net.lacnic.web.registro.api.response.ContactResData;
import net.lacnic.web.registro.api.response.Extension;
import net.lacnic.web.registro.api.response.Frame;
import net.lacnic.web.registro.api.response.IPResData;
import net.lacnic.web.registro.api.response.OrgCreExtension;
import net.lacnic.web.registro.api.response.OrgResData;
import net.lacnic.web.registro.api.response.ResData;
import net.lacnic.web.registro.api.response.ResponseObject;
import net.lacnic.web.registro.api.utils.EPPConverter;

abstract class EppConnection implements EppInterface {

	protected UserWrapper userWrapper;
	protected OrgWrapper orgWrapper;
	protected IpWrapper ipWrapper;
	protected AsnWrapper asnWrapper;
	protected UtilWrapper utilWrapper;

	protected SSLSocket sslSocket;
	protected InputStream inStream;
	protected OutputStream outStream;

	abstract void login(SSLSocket sslSocket) throws Exception;

	/**
	 * Nueva versión del comando que permite crear un contacto en el sistema de
	 * registro autenticado como LACNIC
	 * 
	 * @param nombre
	 *            Nombre completo del contacto a crear, por ejemplo John Doe
	 * @param email
	 *            E-mail del contacto a crear, por ejemplo: john@doe.com
	 * @param endLogradouro
	 *            Dirección completa, por ejemplo: Avenida de los Hóroes, 1234,
	 *            1
	 * @param endCidade
	 *            Ciudad donde reside el contacto, por ejemplo Montevideo
	 * @param endUf
	 *            Estado donde reside el contacto a crear, por ejemplo
	 *            Montevideo
	 * @param endCep
	 *            Código postal de la ubicación del contacto a crear, por
	 *            ejemplo 11800
	 * @param endPais
	 *            Pais de residencia del contacto a crear, por ejemplo Uruguay
	 * @param telDDI
	 *            Código telefónico de paós, por ejemplo 598
	 * @param telDDD
	 *            Código telófonico de órea, por ejemplo 2
	 * @param telNumero
	 *            Nómero telefonico del contacto, por ejemplo 5551234
	 * @param extension
	 *            Extensión del nómero de contacto por ejemplo 4409
	 * @param faxNumero
	 *            Nómero de fax del cliente a crear, por ejemplo 5557890
	 * @param contrasena
	 *            Contraseóa del contacto a crear, por ejemplo MIPASS
	 * @param palabraClave
	 *            Palabra clave, por ejemplo MIPRIMERAMASCOTA
	 * @param language
	 *            Idioma del contacto a crear, por ejemplo SP
	 * @return Retorna el identificador del nuevo contacto
	 * @throws Exception
	 *             Retorna la excepción proporcionada por el servidor EPP con
	 *             los tags <reason/> correspondientes
	 */
	@Override
	public ResponseObject crearUsuarioLACNIC(UserRequest user) throws Exception {
		return crearUsuarioLACNIC(userWrapper.armarNuevoContactoLACNIC(user));
	}

	@Override
	public ResponseObject crearUsuarioLACNIC(String xml) throws Exception {
		try {

			UtilsFiles.writeFrame(xml, outStream);
			final Frame frame = UtilsFiles.getFrame(inStream);
			if (!frame.isValid())
				throw EppException.eppException(frame);

			return EPPConverter.convertResponseObject(frame, new ContactResData(), new Extension());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * Comando que permite editar los datos de un usuario de EPP
	 * 
	 * @param handleUsuario
	 *            Handle del usuario a modificar, por ejemplo ANU
	 * @param nombre
	 *            Nombre del contacto a modificar (no se debe modificar el
	 *            nombre)
	 * @param email
	 *            E-mail del contacto a modificar, por ejemplo jhon@doe.com
	 * @param endLogradouro
	 *            Dirección (calle solamente), por ejemplo: Avenida de los
	 *            Hóroes
	 * @param endNumero
	 *            Nómero de puerta de la dirección proporcionada, por ejemplo
	 *            1234
	 * @param endComplemento
	 *            Complemento de la dirección proporcionada, por ejemplo, 1
	 * @param endCidade
	 *            Ciudad donde reside el contacto, por ejemplo Montevideo
	 * @param endUf
	 *            Estado donde reside el contacto a editar, por ejemplo
	 *            Montevideo
	 * @param endCep
	 *            Código postal de la ubicación del contacto a editar, por
	 *            ejemplo 11800
	 * @param endPais
	 *            Pais de residencia del contacto a editar, por ejemplo Uruguay
	 * @param idioma
	 *            Idioma del contacto a editar, por ejemplo SP
	 * @param contrasena
	 *            Contraseóa del contacto a editar, por ejemplo MIPASS
	 * @param repitaContrasena
	 *            Repita la contraseóa
	 * @param palabraClave
	 *            Palabra clave, por ejemplo MIPRIMERAMASCOTA
	 * @param telDDI
	 *            Código telefónico de paós, por ejemplo 598
	 * @param telDDD
	 *            Código telófonico de órea, por ejemplo 2
	 * @param telNumero
	 *            Nómero telefonico del contacto a editar, por ejemplo 5551234
	 * @param extension
	 *            Extensión del nómero de contacto por ejemplo 4409
	 * @return
	 * @throws Exception
	 */
	@Override
	public ResponseObject modificarUsuario(UserRequest user) throws Exception {
		try {
			String xml = userWrapper.armarEdicionUsuario(user);

			UtilsFiles.writeFrame(xml, outStream);
			final Frame frame = UtilsFiles.getFrame(inStream);
			if (!frame.isValid())
				throw EppException.eppException(frame);

			return EPPConverter.convertResponseObject(frame, new ResData(), new Extension());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Nuevo comando que permite la creación privilegiada para una organización
	 * nueva.
	 * 
	 * @param name
	 *            Nombre de la organización a crear, por ejemplo MIEMPRESA
	 * @param street
	 *            Dirección completa de la organización, por ejemplo Avenida de
	 *            los Hóroes, 1234, 1
	 * @param city
	 *            Ciudad donde reside la organización
	 * @param state
	 *            Estado donde reside la organización
	 * @param postalCode
	 *            Código postal del lugar de residencia de la organización
	 * @param countryCode
	 *            Paós donde reside la organización, por ejemplo Uruguay
	 * @param codigoPais
	 *            Código telefónico de paós, por ejemplo 598
	 * @param codigoArea
	 *            Código telefónico de órea, por ejemplo 2
	 * @param telefono
	 *            Nómero telefónico de contacto de la organización, por ejemplo
	 *            5551234
	 * @param extension
	 *            Extensión del nómero telefónico, por ejemplo 4409
	 * @param faxNumero
	 *            Nómero de fax de la organización, por ejemplo 5556789
	 * @param email
	 *            E-mail del responsable de la organización
	 * @param pass
	 *            Contraseóa de la organización
	 * @param handleAdmin
	 *            Handle identificador existente del usuario responsable de la
	 *            organización
	 * @param responsible
	 *            Nombre del responsable
	 * @param orgType
	 *            Enumerado que representa el tipo de organización a crear
	 * @param eppPassword
	 *            Contraseóa de EPP de LACNIC
	 * @param eppIPs
	 *            Lista de rangos ips autorizados para modificar la organización
	 *            mediante EPP
	 * @param renewalTypes
	 *            Lista de enumerados que representan atributos sobre la
	 *            organización
	 * @param resourcesClass
	 *            Bandera que identifica legacidad de recursos o no
	 */
	@Override
	public ResponseObject crearOrganizacionLACNIC(OrgRequest org) throws Exception {
		try {
			String xml = orgWrapper.armarNuevaOrgLACNIC(org);
			UtilsFiles.writeFrame(xml, outStream);
			final Frame frame = UtilsFiles.getFrame(inStream);
			if (!frame.isValid())
				throw EppException.eppException(frame);

			String idOrg = frame.getFrame().split("<brorg:organization>")[1].split("</brorg:organization>")[0];
			System.out.println("Org created: " + idOrg);
			return EPPConverter.convertResponseObject(frame, new OrgResData(), new OrgCreExtension());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Comando que permite editar los datos bósicos de una organización dada
	 * 
	 * @param nombreOrg
	 *            Nombre a editar de la organización
	 * @param direccion
	 *            Dirección a editar de la organización
	 * @param numero
	 *            Nómero de calle de la dirección de la organización a editar
	 * @param complemento
	 *            Complemento de dirección de la organización a editar
	 * @param ciudad
	 *            Ciudad de residencia de la organización a editar
	 * @param estado
	 *            Estado de residencia de la organización a editar
	 * @param codigoZipOPostalCode
	 *            Código postal del lugar de residencia de la organización a
	 *            editar
	 * @param pais
	 *            Paós de residencia de la organización a editar
	 * @param codigoPais
	 *            Código telefónico del paós de la organización a editar
	 * @param codigoArea
	 *            Código telefónico de órea de la organización a editar
	 * @param telefono
	 *            Telófono de contacto de la organización
	 * @param extension
	 *            Extensión del nómero telefónica de contacto de la organización
	 *            a editar
	 * @param email
	 *            Email del responsable de la organización
	 * @param identificadorOrganizacion
	 *            Identificador de la organización, por ejemplo UY-ANTA-LACNIC
	 * @param nombreResponsable
	 *            Nombre del responsable de la organización a editar
	 * @param contrasena
	 *            Contraseóa de la organización a editar
	 * @param repitaContrasena
	 *            Repite contraseóa de la organización a editar
	 * @param palabraClave
	 *            Palabra recordatorio para la organización a editar
	 * @throws Exception
	 */
	@Override
	public ResponseObject editarOrganizacion(OrgRequest org) throws Exception {
		try {

			String xml = orgWrapper.armarEdicionOrg(org);
			UtilsFiles.writeFrame(xml, outStream);
			final Frame frame = UtilsFiles.getFrame(inStream);
			if (!frame.isValid())
				throw EppException.eppException(frame);

			return EPPConverter.convertResponseObject(frame, new ResData(), new Extension());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public ResponseObject editarOrganizacionContactos(OrgRequest org) throws Exception {
		try {

			String xml = orgWrapper.armarEdicionOrgContactos(org);
			UtilsFiles.writeFrame(xml, outStream);
			final Frame frame = UtilsFiles.getFrame(inStream);
			if (!frame.isValid())
				throw EppException.eppException(frame);

			return EPPConverter.convertResponseObject(frame, new ResData(), new Extension());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Comando que permite editar los distintos contactos de una organizacion
	 * 
	 * @param identificadorOrganizacion
	 *            Identificador de la organización a editarle sus contactos
	 * @param handleContacto
	 *            Handle del contacto a agregar
	 * @param tipoContacto
	 *            Enumerado con el tipo de contacto a crear para la organización
	 * @throws Exception
	 */
	@Override
	public ResponseObject editarOrganizacionContacto(String user, String ip, String identificadorOrganizacion, String handleContacto, TipoContacto tipoContacto) throws Exception {
		try {
			String xml = orgWrapper.armarEdicionOrgContacto(user, ip, identificadorOrganizacion, handleContacto, tipoContacto);
			UtilsFiles.writeFrame(xml, outStream);
			final Frame frame = UtilsFiles.getFrame(inStream);
			if (!frame.isValid())
				throw EppException.eppException(frame);

			return EPPConverter.convertResponseObject(frame, new ResData(), new Extension());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 
	 * Comando que eventualmente serviróa para delegar recursos de numeración
	 * 
	 * @param startAddress
	 *            Dirección de comienzo del bloque a delegar
	 * @param endAddress
	 *            Dirección de terminación del bloque a delegar
	 * @param ipVersion
	 *            Versión del protocolo del recurso a delegar
	 * @param orgId
	 *            Handle de la organización a la cual se le delegan los recursos
	 *            listados anteriormente
	 * @param adminHandle
	 *            Handle del usuario administrador que delega los bloques
	 * @param allocType
	 *            Tipo de alocación para los bloques delegados
	 * @param dsDatas
	 *            Información de ds para los recursos
	 * @param reverseDNSSections
	 *            Información de servidores de nombre para resolución reversa de
	 *            los recursos delegados
	 * @throws Exception
	 */
	@Override
	public ResponseObject createIP(IpRequest ip) throws Exception {
		try {
			String xml = ipWrapper.armarCreateIP(ip);
			System.out.println(xml);
			UtilsFiles.writeFrame(xml, outStream);
			final Frame frame = UtilsFiles.getFrame(inStream);
			if (!frame.isValid())
				throw EppException.eppException(frame);

			return EPPConverter.convertResponseObject(frame, new IPResData(), new Extension());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * 
	 * Comando que eventualmente serviróa para delegar recursos de numeración
	 * 
	 * @param startAddress
	 *            Dirección de comienzo del bloque a delegar
	 * @param endAddress
	 *            Dirección de terminación del bloque a delegar
	 * @param ipVersion
	 *            Versión del protocolo del recurso a delegar
	 * @param orgId
	 *            Handle de la organización a la cual se le delegan los recursos
	 *            listados anteriormente
	 * @param adminHandle
	 *            Handle del usuario administrador que delega los bloques
	 * @param allocType
	 *            Tipo de alocación para los bloques delegados
	 * @param dsDatas
	 *            Información de ds para los recursos
	 * @param reverseDNSSections
	 *            Información de servidores de nombre para resolución reversa de
	 *            los recursos delegados
	 * @throws Exception
	 */
	@Override
	public ResponseObject editarIP(IpRequest ip) throws Exception {
		try {
			String xml = ipWrapper.armarEditarBloque(ip);
			UtilsFiles.writeFrame(xml, outStream);
			final Frame frame = UtilsFiles.getFrame(inStream);
			if (!frame.isValid())
				throw EppException.eppException(frame);

			return EPPConverter.convertResponseObject(frame, new ResData(), new Extension());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public ResponseObject editarIPContacto(IpRequest ip) throws Exception {
		try {
			String xml = ipWrapper.armarEdicionIpContacto(ip);
			UtilsFiles.writeFrame(xml, outStream);
			final Frame frame = UtilsFiles.getFrame(inStream);
			if (!frame.isValid())
				throw EppException.eppException(frame);

			return EPPConverter.convertResponseObject(frame, new ResData(), new Extension());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public ResponseObject editarAsnAnnouncerIP(IpRequest ip) throws Exception {
		try {
			String xml = ipWrapper.armarEditarAsnAnnouncerBloque(ip);
			UtilsFiles.writeFrame(xml, outStream);
			final Frame frame = UtilsFiles.getFrame(inStream);
			if (!frame.isValid())
				throw EppException.eppException(frame);

			return EPPConverter.convertResponseObject(frame, new ResData(), new Extension());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public boolean hello() throws Exception {
		try {
			String xml = UtilWrapper.armarHello();
			if (!sslSocket.isConnected()) {
				return false;
			}
			;
			UtilsFiles.writeFrame(xml, outStream);
			final Frame frame = UtilsFiles.getFrame(inStream);
			if (frame == null)
				return false;

			if (frame.getFrame().contains("Lacnic EPP Test Server") || frame.getFrame().contains("Lacnic EPP Server")) {
				System.out.println("Hello sent trough EPP");
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public ResponseObject deleteIP(IpRequest ip) throws Exception {
		try {
			String xml = ipWrapper.armarDeleteIP(ip);
			UtilsFiles.writeFrame(xml, outStream);
			final Frame frame = UtilsFiles.getFrame(inStream);
			if (!frame.isValid())
				throw EppException.eppException(frame);

			return EPPConverter.convertResponseObject(frame, new ResData(), new Extension());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public ResponseObject infoIP(IpRequest ip) throws Exception {
		try {

			String xml = ipWrapper.armarInfoIP(ip);
			UtilsFiles.writeFrame(xml, outStream);
			final Frame frame = UtilsFiles.getFrame(inStream);
			if (!frame.isValid())
				throw EppException.eppException(frame);

			return EPPConverter.convertResponseObject(frame, new ResData(), new Extension());

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public String infoASN(ASNRequest asn) throws Exception {
		try {

			String xml = asnWrapper.armarInfoASN(asn);
			UtilsFiles.writeFrame(xml, outStream);
			final Frame frame = UtilsFiles.getFrame(inStream);
			if (!frame.isValid())
				throw EppException.eppException(frame);

			return frame.getFrame();

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public ResponseObject editarASNContacto(ASNRequest asn) throws Exception {
		try {
			String xml = asnWrapper.armarEdicionASNContacto(asn);
			UtilsFiles.writeFrame(xml, outStream);
			final Frame frame = UtilsFiles.getFrame(inStream);
			if (!frame.isValid())
				throw EppException.eppException(frame);

			return EPPConverter.convertResponseObject(frame, new ResData(), new Extension());
			// return frame.getFrame();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public InputStream getInStream() {
		return inStream;
	}

	public void setInStream(InputStream inStream) {
		this.inStream = inStream;
	}

	public OutputStream getOutStream() {
		return outStream;
	}

	public void setOutStream(OutputStream outStream) {
		this.outStream = outStream;
	}

	// TODO
	@Override
	public ResponseObject createDomain(IpRequest ipRequest) throws Exception {

		validateDomainRequest(ipRequest);

		final List<ReverseDNS> dnsNewList = ipRequest.getIpnetwork_reverses_dns_add();
		if (dnsNewList == null || dnsNewList.size() == 0)
			throw new EppException("No Reverse DNS objects present in the IpRequest submitted.");

		// Empty fields *won't* overwrite pre-existing ones
		// final IpRequest emptyIpRequest = new IpRequest(ipRequest.getRoid());
		// emptyIpRequest.setIpnetwork_reverses_dns_add(dnsNewList);
		return editarIP(ipRequest);
	}

	@Override
	public ResponseObject deleteDomain(IpRequest ipRequest) throws Exception {

		validateDomainRequest(ipRequest);

		final List<ReverseDNS> dnsRemList = ipRequest.getIpnetwork_reverses_dns_rem();
		if (dnsRemList == null || dnsRemList.size() == 0)
			throw new EppException("No Reverse DNS objects present in the IpRequest submitted.");

		// Empty fields *won't* overwrite pre-existing ones
		// final IpRequest emptyIpRequest = new IpRequest(ipRequest.getRoid());
		// emptyIpRequest.setIpnetwork_reverses_dns_rem(dnsRemList);
		return editarIP(ipRequest);
	}

	private void validateDomainRequest(IpRequest ipRequest) throws Exception {
		if (ipRequest == null)
			throw new EppException("No IpRequest object submitted.");

		if (ipRequest.getRoid() == null || ipRequest.getRoid() == "")
			throw new EppException("No ROID in the IpRequest submitted.");
	}

	@Override
	public ResponseObject subasignarYCrearOrg(IpRequest ip) throws Exception {

		final OrgRequest orgRequest = ip.getOrgRequest();
		final ResponseObject r1 = crearOrganizacionLACNIC(orgRequest);
		final String orgId = r1.getResponse().getExtension().getOrgCreData().getBrOrgOrganization();
		ip.getOrgRequest().setId(orgId);
		final ResponseObject r2 = createIP(ip);

		// unir las dos Responses
		return r2.merge(r1);
	}
}
