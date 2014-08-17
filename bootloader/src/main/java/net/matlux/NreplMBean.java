package net.matlux;

public interface NreplMBean {

	int getPort();

	void setPort(int port);

	boolean isStarted();

	void start();

	void stop();

}
