package net.matlux;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

import java.lang.management.ManagementFactory;

public class MBeanTest extends TestCase
{
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public MBeanTest( String testName )
	{
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite()
	{
		return new TestSuite( MBeanTest.class );
	}

	public void testRegisterUnregister() throws Exception {
		NreplServer nreplServer = new NreplServer(1111, false,false);
		nreplServer.registerMBean();
		ObjectName objectName = MBeanRegistration.getObjectName();
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		assertTrue(mbs.isRegistered(objectName));
		nreplServer.unregisterMBean();
		assertFalse(mbs.isRegistered(objectName));
	}

}
