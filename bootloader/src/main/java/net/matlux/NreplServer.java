package net.matlux;



import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import clojure.lang.Keyword;
import clojure.lang.Symbol;
import clojure.lang.Var;
import clojure.lang.RT;

/**
 * Embeds a loopback-only nREPL server and exposes named application objects.
 * <p>There is one listener per class loader. Starting another instance replaces
 * that listener; stopping any instance stops it. The public {@link #instance}
 * reference points to the most recently constructed instance for Clojure lookup.
 * Registry access follows {@link HashMap} semantics and is not thread-safe.
 * Callers must coordinate registry changes with REPL evaluation.
 */
public class NreplServer implements Map<String,Object>, NreplMBean
{
	private static final Logger LOGGER = Logger.getLogger(NreplServer.class.getSimpleName());

	/** Most recently constructed registry, used by the Clojure introspection helpers. */
	static public volatile NreplServer instance=null;
	
	/** Default listening port used by the command-line entry point. */
	final static public int DEFAULT_PORT=1112;
    final static private Var USE = RT.var("clojure.core", "use");
    final static private Symbol REPL_SERVER_NS = Symbol.intern("net.matlux.server.nrepl");
    final static private Var START_REPL_SERVER = RT.var("net.matlux.server.nrepl", "start-server-now");
    final static private Var STOP_REPL_SERVER = RT.var("net.matlux.server.nrepl","stop-server-now");
	final static private Var SERVER = RT.var("net.matlux.server.nrepl", "server");

	private final Map<String, Object> objMap = new HashMap<String, Object>();
	private final boolean logExceptionStack;
	private final boolean propagateException;
	private volatile int port;


    /**
     * Creates an object registry and optionally starts and registers the listener.
     * @param port listening port, or zero to let the operating system choose
     * @param startOnCreation whether to start immediately
     * @param registerMBeanOnCreation whether to register {@code net.matlux:name=Nrepl}
     * @param propagateException whether start/stop failures throw instead of returning false
     * @param logExceptionStack whether lifecycle errors include stack traces in logs
     */
	public NreplServer(int port, boolean startOnCreation, boolean registerMBeanOnCreation, boolean propagateException, boolean logExceptionStack) {
		this.port = port;
		this.propagateException = propagateException;
		this.logExceptionStack = logExceptionStack;
		LOGGER.info("Creating ReplStartup for Port=" + port);
		try {
			// Recent Clojure versions defer user namespace setup until explicit init.
			RT.init();
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

    /**
     * Starts the listener and registers its MBean; start failures are logged.
     * @param port listening port, or zero for automatic selection
     */
    public NreplServer(int port) {
		this(port, true,true,false,true);
	}

    /**
     * Starts a listener, optionally taking its port from the first argument.
     * @param args optional port argument
     * @throws Exception if startup or registration fails
     */
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
			Map<?, ?> started = (Map<?, ?>) START_REPL_SERVER.invoke(port);
			port = ((Number) started.get(Keyword.intern("port"))).intValue();
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

    /** Registers the JMX switch; duplicate registration throws a RuntimeException. */
	public void registerMBean() {
		MBeanRegistration.registerNreplServerAsMBean(this, logExceptionStack);
	}

    /** Unregisters the JMX switch; a missing registration throws a RuntimeException. */
	public void unregisterMBean() {
		MBeanRegistration.unregisterNreplServerAsMBean(logExceptionStack);
	}

    /**
     * Looks up an application object for use from Clojure.
     * @param key registry key
     * @return the registered object, or null
     */
	public Object getObj(String key) {
		return objMap.get(key);
	}
    /**
     * Merges entries into this registry without removing existing keys.
     * @param objMap entries to add or replace
     */
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
	public void putAll(Map<? extends String, ? extends Object> m) {
		objMap.putAll(m);
		
	}

	@Override
	public void clear() {
		objMap.clear();
	}

	@Override
	public Set<String> keySet() {
		return objMap.keySet();
	}

	@Override
	public Collection<Object> values() {
		return objMap.values();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return objMap.entrySet();
	}

}
