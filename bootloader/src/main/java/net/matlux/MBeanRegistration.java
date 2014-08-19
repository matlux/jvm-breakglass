package net.matlux;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import java.lang.management.ManagementFactory;

public final class MBeanRegistration {

	public static void registerNreplServerAsMBean(NreplMBean nreplServer) {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			registerMBean(mbs, getObjectName(), nreplServer);
		} catch (Exception e) {
			System.out.println("MBean Registration not successful: " + e);
		}
	}

	public static void unregisterNreplServerAsMBean() {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			mbs.unregisterMBean(getObjectName());
		} catch (Exception e) {
			System.out.println("MBean Unregistration not successful: " + e);
		}
	}

	static ObjectName getObjectName() throws MalformedObjectNameException {
		return new ObjectName("net.matlux:name=Nrepl");
	}

	private static void registerMBean(MBeanServer mbs, ObjectName objectName, NreplMBean nreplServer) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		StandardMBean mbean = new StandardMBean(nreplServer, NreplMBean.class, false);
		mbs.registerMBean(mbean, objectName);
	}

	private MBeanRegistration() {
	}

}
