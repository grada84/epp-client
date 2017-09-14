package net.lacnic.epp.pooling;

import java.util.NoSuchElementException;

import org.apache.commons.pool2.ObjectPool;

import net.lacnic.epp.EppExternalConnection;

/**
 * Class that manages the EPPConnection pool
 * 
 * @author agustin
 *
 */
public class EppConnectionUtil {

	public ObjectPool<EppExternalConnection> externalPool;

	private static EppConnectionUtil instance = null;

	private EppConnectionUtil(ObjectPool<EppExternalConnection> externalPool) {
		this.externalPool = externalPool;
	}

	private EppConnectionUtil() {

	}

	public static EppConnectionUtil getInstance() {
		if (instance == null) {
			try {
				EppExternalConnectionPool externalConnectionPool = new EppExternalConnectionPool(new EppExternalConnectionFactory());
				externalConnectionPool.preparePool();
				instance = new EppConnectionUtil(externalConnectionPool);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return instance;
	}

	/*
	 * EXTERNAL
	 */

	public EppExternalConnection borrowExternalConnection() throws NoSuchElementException, IllegalStateException, Exception {
		return externalPool.borrowObject();
	}

	public void returnExternalConnection(EppExternalConnection obj) throws Exception {
		externalPool.returnObject(obj);
	}

	public int getNumIdleExternalConnection() {
		return externalPool.getNumIdle();
	}

	public int getNumActiveExternalConnection() {
		return externalPool.getNumActive();
	}

	public ObjectPool<EppExternalConnection> getExternalConnectionPool() {
		return externalPool;
	}

	public void setExternalConnectionPool(ObjectPool<EppExternalConnection> pool) {
		this.externalPool = pool;
	}

}
