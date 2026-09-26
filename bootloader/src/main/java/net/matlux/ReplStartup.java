package net.matlux;



import java.util.HashMap;
import java.util.Map;

import clojure.lang.Symbol;
import clojure.lang.Var;
import clojure.lang.RT;
/**
 * Historical socket-REPL entry point requiring an external {@code server.socket}
 * namespace that is not bundled with this artifact.
 * @deprecated Use {@link NreplServer}, which supports nREPL and JMX lifecycle control.
 */
@Deprecated
public class ReplStartup 
{
	
	private Map<String, Object> objMap = new HashMap<String, Object>();
	/** Most recently created legacy socket-REPL registry. */
	static public ReplStartup instance=null;
	
	/** Legacy default listening port. */
	final static public int DEFAULT_PORT=1112;
    final static private Var USE = RT.var("clojure.core", "use");
    final static private Symbol SERVER_SOCKET_NS = Symbol.intern("server.socket");
    final static private Var CREATE_REPL_SERVER = RT.var("server.socket","create-repl-server");

    ReplStartup(int port) {
    	USE.invoke(SERVER_SOCKET_NS);
        CREATE_REPL_SERVER.invoke(port);
        instance=this;
    }

    /**
     * Starts the legacy socket REPL, requiring an application-supplied server.socket.
     * @param args optional port argument
     * @throws Exception if initialization fails
     */
    public static void main(String[] args) throws Exception {
    	int port=DEFAULT_PORT;
    	if(args.length > 0) {
    		port = Integer.parseInt(args[0]);
    	}
    		
    	new ReplStartup(port);
    }

    /**
     * Looks up an object in the legacy registry.
     * @param key registry key
     * @return registered object, or null
     */
	public Object getObj(String key) {
		return objMap.get(key);
	}
    /**
     * Replaces the legacy registry.
     * @param objMap replacement map
     */
	public void setObjMap(Map<String, Object> objMap) {
		this.objMap = objMap;
	}

}
