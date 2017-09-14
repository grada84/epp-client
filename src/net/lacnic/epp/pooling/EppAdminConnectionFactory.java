package net.lacnic.epp.pooling;

import javax.net.ssl.SSLSocket;

import net.lacnic.epp.EppAdminConnection;
import net.lacnic.epp.SSLFactory;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class EppAdminConnectionFactory extends BasePooledObjectFactory<EppAdminConnection> {
	private static EppAdminConnection connection = null;

	@Override
	public EppAdminConnection create() throws Exception {
		if (connection == null || connection.getSocket().isClosed()) {
			try {
				SSLSocket socket = SSLFactory.createSSLSocket();
				connection = new EppAdminConnection(socket);
			} catch (Exception e) {
				System.err.println(e.getStackTrace());
			}
		}
		return connection;
	}

	@Override
	public PooledObject<EppAdminConnection> wrap(EppAdminConnection obj) {
		return new DefaultPooledObject<EppAdminConnection>(obj);
	}

	@Override
	public boolean validateObject(PooledObject<EppAdminConnection> p) {
		EppAdminConnection epp = p.getObject();
		if (epp.getSocket() == null)
			return false;
		return !epp.getSocket().isClosed();
	}

	@Override
	public void activateObject(PooledObject<EppAdminConnection> p) throws Exception {
		super.activateObject(p);
	}

	@Override
	public void passivateObject(PooledObject<EppAdminConnection> p) throws Exception {
		super.passivateObject(p);
	}

}
