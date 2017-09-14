package net.lacnic.epp;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.net.ssl.SSLSocket;

import net.lacnic.epp.util.Constantes;
import net.lacnic.epp.util.UtilsFiles;
import net.lacnic.web.registro.api.response.Frame;

public class EppAdminConnection extends EppConnection {

	private final String userAdminNumber;
	private final String userAdminPassword;
	private final String userAdminId;
	private SSLSocket socket;

	public EppAdminConnection(SSLSocket sslSocket) {
		Properties properties = Constantes.getEppProperties();
		userAdminNumber = properties.getProperty(Constantes.userAdminNumber);
		userAdminId = properties.getProperty(Constantes.userAdminId);
		userAdminPassword = properties.getProperty(Constantes.userAdminPassword);
		socket = sslSocket;
		login(socket);
	}

	@Override
	void login(SSLSocket sslSocket) {
		try {
			this.utilWrapper = new UtilWrapper("a");
			this.orgWrapper = new OrgWrapper("a");
			this.ipWrapper = new IpWrapper("a");
			this.asnWrapper = new AsnWrapper("a");
			this.userWrapper = new UserWrapper("a");

			this.sslSocket = sslSocket;
			this.inStream = sslSocket.getInputStream();
			this.outStream = sslSocket.getOutputStream();
			UtilsFiles.getFrame(inStream); // obtengo greeting
			String armarLogin = utilWrapper.armarLoginAdmin(userAdminNumber, userAdminPassword, userAdminId);
			UtilsFiles.writeFrame(armarLogin, outStream); // mando login
			final Frame frame = UtilsFiles.getFrame(inStream); // obtengo
																// respuesta

			if (!frame.isValid())
				frame.throwException("Login failed");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		desloguear();
	}

	private void desloguear() throws Exception, IOException {
		String xmlLogout = utilWrapper.armarLogutAdmin(userAdminId);
		UtilsFiles.writeFrame(xmlLogout, outStream);
		UtilsFiles.getFrame(inStream);
		UtilsFiles.disconnect(sslSocket, inStream);
	}

	public void runCommand(String xmlPath) throws Exception {
		byte[] encoded = UtilsFiles.getBytesFromFile(new File(xmlPath));
		String xml = new String(encoded, Constantes.UTF8);
		UtilsFiles.writeFrame(xml, outStream);
		final Frame frame = UtilsFiles.getFrame(inStream); // obtengo
		// respuesta
		System.out.println(frame.getFrame());
		if (!frame.isValid())
			frame.throwException("Login failed");
	}

	public SSLSocket getSocket() {
		return socket;
	}

	public void setSocket(SSLSocket socket) {
		this.socket = socket;
	}

}
