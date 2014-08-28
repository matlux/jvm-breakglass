package net.matlux;



import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import clojure.lang.Symbol;
import clojure.lang.Var;
import clojure.lang.RT;

/**
 * NreplServer
 *
 */
public class NreplServer implements Map<String,Object>, NreplMBean
{
	private static final Logger LOGGER = Logger.getLogger(NreplServer.class.getSimpleName());

	static public NreplServer instance=null;
	
	final static public int DEFAULT_PORT=1112;
    final static private Var USE = RT.var("clojure.core", "use");
    final static private Symbol REPL_SERVER_NS = Symbol.intern("net.matlux.server.nrepl");
    final static private Var START_REPL_SERVER = RT.var("net.matlux.server.nrepl", "start-server-now");
    final static private Var STOP_REPL_SERVER = RT.var("net.matlux.server.nrepl","stop-server-now");
	final static private Var SERVER = RT.var("net.matlux.server.nrepl", "server");

	private final Map<String, Object> objMap = new HashMap<String, Object>();
	private final boolean logExceptionStack;
	private final boolean propagateException;
	private int port;


	public NreplServer(int port, boolean startOnCreation, boolean registerMBeanOnCreation, boolean propagateException, boolean logExceptionStack) {
		this.port = port;
		this.propagateException = propagateException;
		this.logExceptionStack = logExceptionStack;
		LOGGER.info("Creating ReplStartup for Port=" + port);
		try {
			USE.invoke(REPL_SERVER_NS);
		} catch (Throwable t) {
			LOGGER.log(Level.SEVERE, "Repl initialization caught an error", t);
		}

		if (startOnCreation) {
			start();
		}

		if (registerMBeanOnCreation) {
			registerMBean();
		}

		instance=this;
	}

    public NreplServer(int port) {
		this(port, true,true,false,true);
	}

	public static void main(String[] args) throws Exception {
    	int port=DEFAULT_PORT;
    	if(args.length > 0) {
    		port = Integer.parseInt(args[0]);
    	}

    	new NreplServer(port);
    }

	@Override
    public boolean start() {
		try {
			START_REPL_SERVER.invoke(port);
			LOGGER.info("Repl started successfully on Port = " + port);
		} catch (Throwable t) {
			if (logExceptionStack) LOGGER.log(Level.SEVERE, "Repl startup caught an error", t);
			else LOGGER.log(Level.INFO, "Repl startup caught an error");
			if (propagateException) throw new RuntimeException("Repl startup caught an error", t);
			return false;
		}
		return true;
	}

	@Override
	public boolean stop() {
		try {
			STOP_REPL_SERVER.invoke();
			LOGGER.info("Repl stopped successfully");
		} catch (Throwable t) {
			if (logExceptionStack) LOGGER.log(Level.SEVERE, "Repl stop caught an error", t);
			else LOGGER.log(Level.INFO, "Repl stop caught an error");
			if (propagateException) throw new RuntimeException("Repl stop caught an error", t);
			return false;
		}
		return true;
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
		MBeanRegistration.registerNreplServerAsMBean(this, logExceptionStack);
	}

	public void unregisterMBean() {
		MBeanRegistration.unregisterNreplServerAsMBean(logExceptionStack);
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
