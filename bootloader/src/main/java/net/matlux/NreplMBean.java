package net.matlux;

public interface NreplMBean {

	int getPort();

	boolean isStarted();

	void start();

	void stop();

}
