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
public class NreplServerStartup 
{
	
	private Map<String, Object> objMap = new HashMap<String, Object>();
	final static public int a=1225;


	static public NreplServerStartup instance=null;
	
	final static public int DEFAULT_PORT=1112;
    final static private Var USE = RT.var("clojure.core", "use");
    final static private Symbol SERVER_SOCKET = Symbol.intern("net.matlux.server.nrepl");
    final static private Var CREATE_REPL_SERVER = RT.var("net.matlux.server.nrepl","start-server-now");



    public NreplServerStartup(int port) {
    	System.out.println("starting ReplStartup on Port=" + port);
    	try {
        	USE.invoke(SERVER_SOCKET);
    		CREATE_REPL_SERVER.invoke(port);
    		System.out.println("Repl started successfully");
    	} catch (Throwable t) {
    		System.out.println("Repl startup caught an error: " + t);
    	}
    	
        instance=this;
    }

    public static void main(String[] args) throws Exception {
    	int port=DEFAULT_PORT;
    	if(args.length > 0) {
    		port = Integer.parseInt(args[0]);
    	}

    	new NreplServerStartup(port);
    }

    


	public Object getObj(String key) {
		return objMap.get(key);
	}
	public void setObjMap(Map<String, Object> objMap) {
		this.objMap = objMap;
	}

}
