package net.matlux;

/** JMX switch for the shared nREPL listener. */
public interface NreplMBean {

    /**
     * Reads this instance's configured port.
     * @return configured port, or the actual bound port after successful startup
     */
	int getPort();

    /**
     * Changes the port used on the next start; does not restart a running listener.
     * @param port port from 0 to 65535; zero requests an available port
     */
	void setPort(int port);

    /**
     * Checks the shared listener state.
     * @return whether the class loader's shared listener is running
     */
	boolean isStarted();

    /**
     * Starts or replaces the shared listener, bound to 127.0.0.1.
     * @return true on success, false on failure when propagation is disabled
     * @throws RuntimeException on failure when propagation is enabled
     */
	boolean start();

    /**
     * Stops the shared listener; stopping an already stopped listener succeeds.
     * @return true on success, false on failure when propagation is disabled
     * @throws RuntimeException on failure when propagation is enabled
     */
	boolean stop();

}
