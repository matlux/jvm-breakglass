package net.matlux;

import java.lang.management.ManagementFactory;
import javax.management.*;
import javax.management.remote.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class MBeanIT {
    private NreplServer server;
    private MBeanServer platform;
    private ObjectName name;

    @Before
    public void register() throws Exception {
        platform = ManagementFactory.getPlatformMBeanServer();
        name = MBeanRegistration.getObjectName();
        server = new NreplServer(0, false, true, true, false);
    }

    @After
    public void cleanUp() throws Exception {
        try {
            if (server != null) server.stop();
        } finally {
            if (platform != null && platform.isRegistered(name)) platform.unregisterMBean(name);
        }
    }

    @Test
    public void registrationAndUnregistration() {
        assertTrue(platform.isRegistered(name));
        server.unregisterMBean();
        assertFalse(platform.isRegistered(name));
    }

    @Test
    public void duplicateRegistrationAndMissingUnregistrationFail() {
        assertThrows(RuntimeException.class, () -> server.registerMBean());
        server.unregisterMBean();
        assertThrows(RuntimeException.class, () -> server.unregisterMBean());
    }

    @Test(timeout = 30000)
    public void remoteJmxCanReadAttributesChangePortAndStartStop() throws Exception {
        // RMI exports on an OS-assigned port; no fixed-port registry to leak.
        JMXConnectorServer connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(
                new JMXServiceURL("service:jmx:rmi://127.0.0.1"), null, platform);
        try {
            connectorServer.start();
            try (JMXConnector connector = JMXConnectorFactory.connect(connectorServer.getAddress())) {
                NreplMBean proxy = JMX.newMBeanProxy(connector.getMBeanServerConnection(), name, NreplMBean.class);
                assertFalse(proxy.isStarted());
                proxy.setPort(0);
                assertEquals(0, proxy.getPort());
                assertTrue(proxy.start());
                assertTrue(proxy.isStarted());
                assertTrue(proxy.getPort() > 0);
                assertEquals(server.getPort(), proxy.getPort());
                assertTrue(proxy.stop());
                assertFalse(proxy.isStarted());
                assertTrue(proxy.stop());
            }
        } finally {
            connectorServer.stop();
        }
    }
}
