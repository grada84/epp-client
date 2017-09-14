package net.lacnic.epp.pooling;

import javax.net.ssl.SSLSocket;

import net.lacnic.epp.EppExternalConnection;
import net.lacnic.epp.SSLFactory;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class EppExternalConnectionFactory extends BasePooledObjectFactory<EppExternalConnection> {

	private static EppExternalConnection connection = null;

	@Override
	public EppExternalConnection create() throws Exception {
		if (connection == null || connection.getSocket().isClosed() || (!connection.getSocket().isConnected())) {
			try {
				SSLSocket socket = SSLFactory.createSSLSocket();
				connection = new EppExternalConnection(socket);
			} catch (Exception e) {
				System.err.println(e.getStackTrace());
			}
		}
		return connection;
	}

	@Override
	public PooledObject<EppExternalConnection> wrap(EppExternalConnection obj) {
		return new DefaultPooledObject<EppExternalConnection>(obj);
	}

	@Override
	public boolean validateObject(PooledObject<EppExternalConnection> p) {
		EppExternalConnection epp = p.getObject();
		if (epp.getSocket() == null)
			return false;
		return !epp.getSocket().isClosed();
	}

	@Override
	public void activateObject(PooledObject<EppExternalConnection> p) throws Exception {
		super.activateObject(p);
	}

	@Override
	public void passivateObject(PooledObject<EppExternalConnection> p) throws Exception {
		super.passivateObject(p);
	}

}
