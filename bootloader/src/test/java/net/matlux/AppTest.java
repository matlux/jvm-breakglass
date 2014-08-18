package net.matlux;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.matlux.NreplServer;
import net.matlux.testobjects.Address;
import net.matlux.testobjects.Employee;

import static net.matlux.fixtures.Fixtures.*;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testStartBasicAccessStopApp()
    {
    	NreplServer server = new NreplServer(1112); //start server listening onto port number
    	Employee originalEmployee = new Employee(EMPLOYEE_FNAME1,EMPLOYEE_LNAME1,new Address(STREET1, ZIPCODE1, CITY1) );
    	server.put("employee", originalEmployee);

    	
    	Employee emp = (Employee)server.get("employee");
    	
    	assertEquals(1112, server.getPort());
    	assertTrue(emp.equals(originalEmployee));
    	
    	
    	server.stop();
    }
    /*public void testStartStopServerThreadSafety1()
    {
    	NreplServer server = new NreplServer(1112); //start server listening onto port number
    	server.stop();
    	server.start();
    	server.stop();
    	server.start();
    	server.stop();
    	server.start();
    	server.stop();
    }*/
}
