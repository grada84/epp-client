package net.lacnic.epp;

import java.io.IOException;
import java.util.Properties;

import javax.net.ssl.SSLSocket;

import net.lacnic.epp.util.Constantes;
import net.lacnic.epp.util.UtilsFiles;
import net.lacnic.web.registro.api.response.Frame;

public class EppExternalConnection extends EppConnection {

	private final String userExtNumber;
	private final String userExtId;
	private final String userExtPassword;
	private SSLSocket socket;

	public EppExternalConnection(SSLSocket sslSocket) throws Exception {
		Properties properties = Constantes.getEppProperties();
		userExtNumber = properties.getProperty(Constantes.userExtNumber);
		userExtId = properties.getProperty(Constantes.userExtId);
		userExtPassword = properties.getProperty(Constantes.userExtPassword);
		socket = sslSocket;
		login(socket);
	}

	@Override
	void login(SSLSocket sslSocket) throws Exception {
		this.utilWrapper = new UtilWrapper("e");
		this.orgWrapper = new OrgWrapper("e");
		this.ipWrapper = new IpWrapper("e");
		this.asnWrapper = new AsnWrapper("e");
		this.userWrapper = new UserWrapper("e");

		this.sslSocket = sslSocket;
		this.inStream = sslSocket.getInputStream();
		this.outStream = sslSocket.getOutputStream();
		UtilsFiles.getFrame(inStream); // obtengo greeting
		String armarLogin = utilWrapper.armarLoginExterno(userExtNumber, userExtPassword, userExtId);

		UtilsFiles.writeFrame(armarLogin, outStream);// mando login
		final Frame frame = UtilsFiles.getFrame(inStream); // obtengo

		if (!frame.isValid())
			frame.throwException(frame.getFrame());
	}

	@Override
	protected void finalize() throws Throwable {
		desloguear();
	}

	private void desloguear() throws Exception, IOException {
		String xmlLogout = utilWrapper.armarLogutExt(userExtId);
		UtilsFiles.writeFrame(xmlLogout, outStream);
		UtilsFiles.getFrame(inStream);
		UtilsFiles.disconnect(sslSocket, inStream);
	}

	public SSLSocket getSocket() {
		return socket;
	}

	public void setSocket(SSLSocket socket) {
		this.socket = socket;
	}

}
