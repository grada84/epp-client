package net.lacnic.epp.pooling;

import net.lacnic.epp.EppAdminConnection;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class EppAdminConnectionPool extends GenericObjectPool<EppAdminConnection> {

	private int minIdle = 2;

	public EppAdminConnectionPool(PooledObjectFactory<EppAdminConnection> factory) {
		super(factory);
		setMinIdle(minIdle);

	}

}
