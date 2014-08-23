package net.matlux;


import static org.junit.Assert.*;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.management.ManagementFactory;

public class MBeanTest
{

    private NreplServer nreplServer;

	@Before
    public void setUp() {
        System.out.println("@Before - setUp " + MBeanTest.class.getSimpleName());
    	nreplServer = new NreplServer(1111, false,false,true);
		nreplServer.registerMBean();
    }
 
    @After
    public void tearDown() {
    	try{
    		nreplServer.unregisterMBean();
    	} catch(Throwable t) {
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

		NreplServer nreplServer2 = new NreplServer(1112, false,false,true);
		nreplServer2.registerMBean();
	}
	@Test(expected = RuntimeException.class)
	public void testUnregisterTwice() {
		nreplServer.unregisterMBean();
		nreplServer.unregisterMBean();
	}

}
