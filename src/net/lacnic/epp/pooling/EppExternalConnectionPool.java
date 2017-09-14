package net.lacnic.epp.pooling;

import net.lacnic.epp.EppExternalConnection;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class EppExternalConnectionPool extends GenericObjectPool<EppExternalConnection> {
	
	private int minIdle = 2;

	public EppExternalConnectionPool(PooledObjectFactory<EppExternalConnection> factory) {
		super(factory);
		setMinIdle(minIdle);
	}

	
}
