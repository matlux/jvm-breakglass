package net.matlux;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;
import net.matlux.NreplServer;
import net.matlux.testobjects.Address;
import net.matlux.testobjects.Employee;
import static net.matlux.fixtures.Fixtures.*;
import static org.junit.Assert.*;
/**
 * Unit test for simple App.
 */
public class AppTest
{
	
    @BeforeClass
    public static void oneTimeSetUp() {
        // one-time initialization code   
        USE.invoke(REPL_CLIENT_NS);
        USE.invoke(TEST_APP_NS);
    	System.out.println("@BeforeClass - oneTimeSetUp");
    }
 
    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
    	System.out.println("@AfterClass - oneTimeTearDown");
    }
 
    @Before
    public void setUp() {
        System.out.println("@Before - setUp");
    }
 
    @After
    public void tearDown() {
        System.out.println("@After - tearDown");
    }
	
    final static private Var USE = RT.var("clojure.core", "use");
    final static private Var DROP = RT.var("clojure.core", "drop");
    final static private Symbol REPL_CLIENT_NS = Symbol.intern("cl-java-introspector.client-example");
    final static private Symbol TEST_APP_NS = Symbol.intern("cl-java-introspector.test-app");
    final static private Var REMOTE_EXECUTE = RT.var("cl-java-introspector.client-example", "remote-execute");
    final static private Var REMOTE_CODE_FIXTURE = RT.var("cl-java-introspector.test-app", "test1");
    final static private Var REMOTE_CODE_RESULT_FIXTURE = RT.var("cl-java-introspector.test-app", "fixture-test1-result");



    private Object remoteConnect2ReplAndRunSomeCommands(int port) {
    	return REMOTE_EXECUTE.invoke("localhost", port, REMOTE_CODE_FIXTURE.deref());
    }
    
    private void canSuccessfullyRunRemoteCommands(int port) {
    	Object result = remoteConnect2ReplAndRunSomeCommands(1112);
    	System.out.println("testStartApp result=" + result);
    	System.out.println("testStartApp expected=" + REMOTE_CODE_RESULT_FIXTURE.deref());

    	assertTrue(result.equals(REMOTE_CODE_RESULT_FIXTURE.deref()));
    }
    private void setupFixtureDataOnServer(NreplServer server) {
		server.put("a_test_obj", "this is a test String.");
		System.out.println("Added a_test_obj to server");
    	Employee originalEmployee = new Employee(EMPLOYEE_FNAME1,EMPLOYEE_LNAME1,new Address(STREET1, ZIPCODE1, CITY1) );
    	server.put("employee1", originalEmployee);
    	System.out.println("Added employe1 to server");
    }
    
    private void connectionOnPortRefused(int port) {
    	assertTrue(remoteConnect2ReplAndRunSomeCommands(port).equals("cannot connect"));
    }


    @Test
    public void testStartApp()
    {
    	NreplServer server = new NreplServer(1112,false,false,true);
    	setupFixtureDataOnServer(server);

    	
    	connectionOnPortRefused(1112);
    	
    	
    	server.start();
    	
    	canSuccessfullyRunRemoteCommands(1112);

    	
    	server.stop();
    }
    
    @Test
    public void testAutoStartStopApp()
    {
    	NreplServer server = new NreplServer(1112,true,false,true);
    	setupFixtureDataOnServer(server);
    	
    	canSuccessfullyRunRemoteCommands(1112);
    	
    	server.stop();
    	connectionOnPortRefused(1112);
    }

    @Test
    public void testAutoRegisterStartStopApp()
    {
    	NreplServer server = new NreplServer(1112,false,true,true); //start server listening onto port number
    	setupFixtureDataOnServer(server);

    	connectionOnPortRefused(1112);
    	
    	server.start();
    	canSuccessfullyRunRemoteCommands(1112);
    	
    	server.stop();
    	server.unregisterMBean();
    	
    	connectionOnPortRefused(1112);
    }
    
    @Test
    public void testRegisterStartStopApp()
    {
    	NreplServer server = new NreplServer(1112,false,false,true); //start server listening onto port number
    	server.registerMBean();
    	setupFixtureDataOnServer(server);

    	connectionOnPortRefused(1112);
    	
    	server.start();
    	canSuccessfullyRunRemoteCommands(1112);
    	
    	
    	server.stop();
    	server.unregisterMBean();
    	
    	connectionOnPortRefused(1112);
    }

    public void testSamePortStartTwice()
    {
    	NreplServer server = new NreplServer(1113,true,false,true); //start server listening onto port number
    	NreplServer server2 = new NreplServer(1113,true,false,true); //start server listening onto port number

    	server.stop();
    	server2.stop();
    }
    @Test
    public void testStartTwice()
    {
    	NreplServer server = new NreplServer(1113); //start server listening onto port number
    	assertEquals(1113, server.getPort());
    	server.start();
    	server.stop();
    	server.unregisterMBean();
    }
    @Test
    public void testStopTwice()
    {	
    	NreplServer server = new NreplServer(1113); //start server listening onto port number
    	assertEquals(1113, server.getPort());
    	server.stop();
    	server.stop();
    	server.unregisterMBean();
    }
   
    @Test
    public void testStartStopServerThreadSafety1()
    {	//todo: start multiple servers from different threads
    	NreplServer server = new NreplServer(1113); //start server listening onto port number
    	assertEquals(1113, server.getPort());
    	server.stop();
    	server.unregisterMBean();
    }
}
