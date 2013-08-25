package net.matlux;



import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


import clojure.lang.Symbol;
import clojure.lang.Var;
import clojure.lang.RT;
/**
 * Hello world!
 *
 */
public class NreplServer implements Map<String,Object>
{
	
	private Map<String, Object> objMap = new HashMap<String, Object>();
	final static public int a=1225;


	static public NreplServer instance=null;
	
	final static public int DEFAULT_PORT=1112;
    final static private Var USE = RT.var("clojure.core", "use");
    final static private Symbol SERVER_SOCKET = Symbol.intern("net.matlux.server.nrepl");
    final static private Var CREATE_REPL_SERVER = RT.var("net.matlux.server.nrepl","start-server-now");



    public NreplServer(int port) {
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

    	new NreplServer(port);
    }

    


	public Object getObj(String key) {
		return objMap.get(key);
	}
	public void setObjMap(Map<String, Object> objMap) {
		this.objMap = objMap;
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
		return objMap.put(key,value);
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
