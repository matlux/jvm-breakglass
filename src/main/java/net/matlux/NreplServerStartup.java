package net.matlux;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.matlux.testobjects.Address;
import net.matlux.testobjects.Department;
import net.matlux.testobjects.Employee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

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



    NreplServerStartup(int port) {
    	System.out.println("starting ReplStartup on Port=" + port);
    	try {
        	USE.invoke(SERVER_SOCKET);
    		CREATE_REPL_SERVER.invoke(port);
    		System.out.println("Repl started successfully");
    	} catch (Throwable t) {
    		System.out.println("Repl startup caught an error: " + t);
    	}
    	
        instance=this;
        setupTestData();
    }

    public static void main(String[] args) throws Exception {
    	int port=DEFAULT_PORT;
    	if(args.length > 0) {
    		port = Integer.parseInt(args[0]);
    	}
    		
    	;

    	new NreplServerStartup(port);
    }

    
	private void setupTestData() {
		// This is a nice nested object structure
		Department department = new Department("The Art Department",0L);
		department.add(new Employee("Bob","Dilan",new Address("1 Mayfair","SW1","London")));
		department.add(new Employee("Mick","Jagger",new Address("1 Time Square",null,"NY")));
		objMap.put("department", department);
		Set<Object> myFriends = new HashSet<Object>();
		myFriends.add(new Employee("Keith","Richard",new Address("2 Mayfair","SW1","London")));
		myFriends.add(new Employee("Nina","Simone",new Address("1 Gerards Street","12300","Smallville")));
		objMap.put("myFriends", myFriends);
		objMap.put("nullValue", null);
	}

	public Object getObj(String key) {
		return objMap.get(key);
	}
	public void setObjMap(Map<String, Object> objMap) {
		this.objMap = objMap;
	}

}
