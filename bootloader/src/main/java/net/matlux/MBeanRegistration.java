package net.matlux;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MBeanRegistration {

	private static final Logger LOGGER = Logger.getLogger(MBeanRegistration.class.getSimpleName());

	public static void registerNreplServerAsMBean(NreplMBean nreplServer) {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			registerMBean(mbs, getObjectName(), nreplServer);
			LOGGER.log(Level.SEVERE, "MBean Registration of JVM-breakglass successful");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "MBean Registration of JVM-breakglass not successful", e);
			throw new RuntimeException("MBean Registration of JVM-breakglass not successful", e);
		}
	}

	public static void unregisterNreplServerAsMBean() {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			mbs.unregisterMBean(getObjectName());
			LOGGER.log(Level.SEVERE, "MBean Unregistration of JVM-breakglass successful");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "MBean Unregistration of JVM-breakglass not successful", e);
			throw new RuntimeException("MBean Unregistration of JVM-breakglass not successful", e);
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
