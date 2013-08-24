package net.matlux;



import java.util.HashMap;
import java.util.Map;

import clojure.lang.Symbol;
import clojure.lang.Var;
import clojure.lang.RT;
/**
 * Hello world!
 *
 */
public class ReplStartup 
{
	
	final static public int a=1225;
	private Map<String, Object> objMap = new HashMap<String, Object>();
	static public ReplStartup instance=null;
	
	final static public int DEFAULT_PORT=1112;
    final static private Var USE = RT.var("clojure.core", "use");
    final static private Symbol SERVER_SOCKET = Symbol.intern("server.socket");
    final static private Var CREATE_REPL_SERVER = RT.var("server.socket","create-repl-server");

    ReplStartup(int port) {
    	USE.invoke(SERVER_SOCKET);
        CREATE_REPL_SERVER.invoke(port);
        instance=this;
    }

    public static void main(String[] args) throws Exception {
    	int port=DEFAULT_PORT;
    	if(args.length > 0) {
    		port = Integer.parseInt(args[0]);
    	}
    		
    	new ReplStartup(port);
    }

	public Object getObj(String key) {
		return objMap.get(key);
	}
	public void setObjMap(Map<String, Object> objMap) {
		this.objMap = objMap;
	}

}
