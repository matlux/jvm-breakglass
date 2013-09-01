package net.matlux.testserver;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.matlux.NreplServer;
import net.matlux.testobjects.Address;
import net.matlux.testobjects.Department;
import net.matlux.testobjects.Employee;


/**
 * Hello world!
 *
 */
public class SimpleServerExample 
{
	
	 
	final static public int a=1225;


	static public SimpleServerExample instance=null;
	

    public static void main(String[] args) throws Exception {
    	int port=1112;
    	if(args.length > 0) {
    		port = Integer.parseInt(args[0]);
    	}
    	System.out.println("b4 new nreplserver");
    	setupTestData(new NreplServer(port));
    }

    
	private static void setupTestData(NreplServer nreplServerStartup) {
		Map<String, Object> objMap = new HashMap<String, Object>();
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
		System.out.println("data loaded");
		

		objMap.put("department14", department);
		nreplServerStartup.setObjMap(objMap);
	}


}
