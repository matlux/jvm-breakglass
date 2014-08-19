package net.matlux;



import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import clojure.lang.Atom;
import clojure.lang.Symbol;
import clojure.lang.Var;
import clojure.lang.RT;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.StandardMBean;

/**
 * Hello world!
 *
 */
public class NreplServer implements Map<String,Object>, NreplMBean
{
	
	private Map<String, Object> objMap = new HashMap<String, Object>();
	final static public int a=1225;


	static public NreplServer instance=null;
	
	final static public int DEFAULT_PORT=1112;
    final static private Var USE = RT.var("clojure.core", "use");
    final static private Symbol SERVER_SOCKET = Symbol.intern("net.matlux.server.nrepl");
    final static private Var START_REPL_SERVER = RT.var("net.matlux.server.nrepl", "start-server-now");
    final static private Var STOP_REPL_SERVER = RT.var("net.matlux.server.nrepl","stop-server-now");
	final static private Var SERVER = RT.var("net.matlux.server.nrepl", "server");

	private int port;

	public NreplServer(int port, boolean startOnCreation) {
		this.port = port;
		System.out.println("Configuring ReplStartup on Port=" + port);
		try {
			USE.invoke(SERVER_SOCKET);
		} catch (Throwable t) {
			System.out.println("Repl startup caught an error: " + t);
		}

		if (startOnCreation) {
			start();
		}

		objMap.put("a_test_obj", "this is a test String.");
		instance=this;
	}

    public NreplServer(int port) {
		this(port, true);
	}

	public static void main(String[] args) throws Exception {
    	int port=DEFAULT_PORT;
    	if(args.length > 0) {
    		port = Integer.parseInt(args[0]);
    	}

    	new NreplServer(port);
    }

	@Override
    public void start() {
		try {
			START_REPL_SERVER.invoke(port);
			System.out.println("Repl started successfully on Port=" + port);
		} catch (Throwable t) {
			System.out.println("Repl startup caught an error: " + t);
		}
	}

	@Override
	public void stop() {
		try {
			STOP_REPL_SERVER.invoke();
			System.out.println("Repl stopped successfully");
		} catch (Throwable t) {
			System.out.println("Repl stop caught an error: " + t);
		}
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public boolean isStarted() {
		return SERVER.deref() != null;
	}

	public void registerMBean() {
		MBeanRegistration.registerNreplServerAsMBean(this);
	}

	public void unregisterMBean() {
		MBeanRegistration.unregisterNreplServerAsMBean();
	}

	public Object getObj(String key) {
		return objMap.get(key);
	}
	public void setObjMap(Map<String, Object> objMap) {
		this.objMap.putAll(objMap);
	}

	@Override
	public int size() {
		return objMap.size();
	}

	@Override
	public boolean isEmpty() {
		return objMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return objMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return objMap.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return objMap.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		return objMap.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return objMap.remove(key);
	}

	@Override
	public void putAll(Map m) {
		objMap.putAll(m);
		
	}

	@Override
	public void clear() {
		objMap.clear();
	}

	@Override
	public Set keySet() {
		return objMap.keySet();
	}

	@Override
	public Collection values() {
		return objMap.values();
	}

	@Override
	public Set entrySet() {
		return objMap.entrySet();
	}

}
