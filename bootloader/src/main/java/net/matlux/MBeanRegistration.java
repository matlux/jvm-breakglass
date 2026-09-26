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

/** Registers the nREPL switch with the platform MBean server. */
public final class MBeanRegistration {

	private static final Logger LOGGER = Logger.getLogger(MBeanRegistration.class.getSimpleName());

    /**
     * Registers a switch under the shared object name.
     * @param nreplServer switch to expose
     * @param logExceptionStack whether to include stack traces in error logs
     * @throws RuntimeException if registration fails, including duplicate names
     */
	public static void registerNreplServerAsMBean(NreplMBean nreplServer, boolean logExceptionStack) {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			registerMBean(mbs, getObjectName(), nreplServer);
			LOGGER.log(Level.INFO, "MBean Registration of JVM-breakglass successful");
		} catch (Exception e) {
			if (logExceptionStack) LOGGER.log(Level.SEVERE, "MBean Registration of JVM-breakglass not successful", e);
			else LOGGER.log(Level.INFO, "MBean Registration of JVM-breakglass not successful");
			throw new RuntimeException("MBean Registration of JVM-breakglass not successful", e);
		}
	}

    /**
     * Removes the switch from the platform server.
     * @param logExceptionStack whether to include stack traces in error logs
     * @throws RuntimeException if no switch is registered or removal fails
     */
	public static void unregisterNreplServerAsMBean(boolean logExceptionStack) {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			mbs.unregisterMBean(getObjectName());
			LOGGER.log(Level.INFO, "MBean Unregistration of JVM-breakglass successful");
		} catch (Exception e) {
			if (logExceptionStack) LOGGER.log(Level.SEVERE, "MBean Unregistration of JVM-breakglass not successful", e);
			else LOGGER.log(Level.INFO, "MBean Unregistration of JVM-breakglass not successful");
			throw new RuntimeException("MBean Unregistration of JVM-breakglass not successful", e);
		}
	}

    /**
     * Returns the object name used for registration and JMX lookups.
     * @return the shared name {@code net.matlux:name=Nrepl}
     * @throws MalformedObjectNameException if the object name is invalid
     */
	public static ObjectName getObjectName() throws MalformedObjectNameException {
		return new ObjectName("net.matlux:name=Nrepl");
	}

	private static void registerMBean(MBeanServer mbs, ObjectName objectName, NreplMBean nreplServer) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
		StandardMBean mbean = new StandardMBean(nreplServer, NreplMBean.class, false);
		mbs.registerMBean(mbean, objectName);
	}

	private MBeanRegistration() {
	}

}
