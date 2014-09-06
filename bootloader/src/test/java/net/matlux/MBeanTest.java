package net.matlux;


import static org.junit.Assert.*;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.management.ManagementFactory;

public class MBeanTest {

	private NreplServer nreplServer;

	@Before
	public void setUp() {
		System.out.println("@Before - setUp " + MBeanTest.class.getSimpleName());
		nreplServer = new NreplServer(1111, false, false, true, false);
		nreplServer.registerMBean();
	}

	@After
	public void tearDown() {
		try {
			nreplServer.stop();
			nreplServer.unregisterMBean();
		} catch (Throwable t) {
			System.out.println("ignore failure of unregistration!");
		}
		System.out.println("@After - tearDown " + MBeanTest.class.getSimpleName());
	}

	@Test
	public void testRegisterUnregister() throws Exception {
		ObjectName objectName = MBeanRegistration.getObjectName();
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		assertTrue(mbs.isRegistered(objectName));
		nreplServer.unregisterMBean();
		assertFalse(mbs.isRegistered(objectName));
	}

	@Test(expected = RuntimeException.class)
	public void testRegisterTwice() {
		NreplServer nreplServer2 = new NreplServer(1112, false, false, true, false);
		nreplServer2.registerMBean();
	}

	@Test(expected = RuntimeException.class)
	public void testUnregisterTwice() {
		nreplServer.unregisterMBean();
		nreplServer.unregisterMBean();
	}

	@Test
	public void testStartViaJMX() throws Exception {
		int port = 9875;
		JMXServiceURL url = new JMXServiceURL(String.format("service:jmx:rmi:///jndi/rmi://localhost:%d/jmxrmi", port));
		JMXConnectorServer jmxServer = createJMXServer(port, url);
		try {
			jmxServer.start();
			assertFalse(nreplServer.isStarted());
			JMXConnector connector = JMXConnectorFactory.connect(url);
			try {
				MBeanServerConnection conn = connector.getMBeanServerConnection();
				ObjectName objectName = MBeanRegistration.getObjectName();
				NreplMBean proxy = JMX.newMBeanProxy(conn, objectName, NreplMBean.class);
				assertFalse(proxy.isStarted());
				proxy.start();
				assertTrue(proxy.isStarted());
			} finally {
				connector.close();
			}
			assertTrue(nreplServer.isStarted());
		} finally {
			jmxServer.stop();
		}
	}

	@Test
	public void testStartStopViaJMX() throws Exception {
		int port = 9876;
		JMXServiceURL url = new JMXServiceURL(String.format("service:jmx:rmi:///jndi/rmi://localhost:%d/jmxrmi", port));
		JMXConnectorServer jmxServer = createJMXServer(port, url);
		try {
			jmxServer.start();
			assertFalse(nreplServer.isStarted());
			JMXConnector connector = JMXConnectorFactory.connect(url);
			try {
				MBeanServerConnection conn = connector.getMBeanServerConnection();
				ObjectName objectName = MBeanRegistration.getObjectName();
				NreplMBean proxy = JMX.newMBeanProxy(conn, objectName, NreplMBean.class);
				assertFalse(proxy.isStarted());
				proxy.start();
				assertTrue(proxy.isStarted());
				proxy.stop();
				assertFalse(proxy.isStarted());
			} finally {
				connector.close();
			}
		} finally {
			jmxServer.stop();
		}
		assertFalse(nreplServer.isStarted());
	}


	private JMXConnectorServer createJMXServer(int port, JMXServiceURL url) throws Exception {
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		java.rmi.registry.LocateRegistry.createRegistry(port);
		JMXConnectorServer connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
		return connectorServer;
	}

}
