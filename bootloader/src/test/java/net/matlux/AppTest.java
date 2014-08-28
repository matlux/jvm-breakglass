package net.matlux;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
        USE.invoke(DATA_NS);
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
    final static private Var FIRST = RT.var("clojure.core", "first");
    final static private Var REST = RT.var("clojure.core", "rest");
    final static private Var TYPE = RT.var("clojure.core", "type");
    final static private Var DIFF = RT.var("clojure.data", "diff");
    final static private Symbol REPL_CLIENT_NS = Symbol.intern("cl-java-introspector.client-example");
    final static private Symbol TEST_APP_NS = Symbol.intern("cl-java-introspector.test-app");
    final static private Symbol DATA_NS = Symbol.intern("clojure.data");
    final static private Var REMOTE_EXECUTE = RT.var("cl-java-introspector.client-example", "remote-execute");
    final static private Var REMOTE_CODE_FIXTURE = RT.var("cl-java-introspector.test-app", "test1");
    final static private Var REMOTE_CODE_RESULT_FIXTURE = RT.var("cl-java-introspector.test-app", "fixture-test1-result");
    final static private Var REMOTE_CODE_FIXTURE2 = RT.var("cl-java-introspector.test-app", "test2");
    final static private Var REMOTE_CODE_RESULT_FIXTURE2 = RT.var("cl-java-introspector.test-app", "fixture-test2-result");



    private Object remoteConnect2ReplAndRunSomeCommands(Var codefixture, int port) {
        return REMOTE_EXECUTE.invoke("localhost", port, codefixture.deref());
    }
    
    private void canSuccessfullyRunRemoteCommands(Var codeFixture,Var expectedResult, int port) {
        Object result = remoteConnect2ReplAndRunSomeCommands(codeFixture, 1112);
        System.out.println("testStartApp result=" + result);
        System.out.println("testStartApp expected=" + expectedResult.deref());
        System.out.println("diff=" + DIFF.invoke(result, expectedResult.deref()));

        assertTrue(result.equals(expectedResult.deref()));
        //assertTrue(FIRST.invoke(result).equals(FIRST.invoke(expectedResult.deref())));
    }
    private void setupFixtureDataOnServer(NreplServer server) {
        server.put("a_test_obj", "this is a test String.");
        System.out.println("Added a_test_obj to server");
        Employee originalEmployee = new Employee(EMPLOYEE_FNAME1,EMPLOYEE_LNAME1,new Address(STREET1, ZIPCODE1, CITY1) );
        server.put("employee1", originalEmployee);
        System.out.println("Added employe1 to server");
    }
    
    private void connectionOnPortRefused(int port) {
        assertTrue(remoteConnect2ReplAndRunSomeCommands(REMOTE_CODE_FIXTURE, port).equals("cannot connect"));
    }


    @Test
    public void testStartApp()
    {
        NreplServer server = new NreplServer(1112,false,false,true);
        setupFixtureDataOnServer(server);
        connectionOnPortRefused(1112);
        server.start();
        canSuccessfullyRunRemoteCommands(REMOTE_CODE_FIXTURE, REMOTE_CODE_RESULT_FIXTURE, 1112);
        server.stop();
    }
   @Test
    public void testSpringApp()
    {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/spring/server-test.xml");
        canSuccessfullyRunRemoteCommands(REMOTE_CODE_FIXTURE2, REMOTE_CODE_RESULT_FIXTURE2, 1112);
        NreplServerSpring server = (NreplServerSpring)context.getBean("repl");
        
        server.stop();
        server.unregisterMBean();
        context.close();
    }
    
    @Test
    public void testAutoStartStopApp()
    {
        NreplServer server = new NreplServer(1112,true,false,true);
        setupFixtureDataOnServer(server);
        
        canSuccessfullyRunRemoteCommands(REMOTE_CODE_FIXTURE, REMOTE_CODE_RESULT_FIXTURE, 1112);
        
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
        canSuccessfullyRunRemoteCommands(REMOTE_CODE_FIXTURE, REMOTE_CODE_RESULT_FIXTURE, 1112);
        
        server.stop();
        server.unregisterMBean();
        
        connectionOnPortRefused(1112);
    }
    
    @Test
    public void testRegisterStartStopApp()
    {
        NreplServer server = new NreplServer(1112,false,false,true); //start server listening onto port number
        try {
          server.registerMBean();
          setupFixtureDataOnServer(server);

          connectionOnPortRefused(1112);
        
          server.start();
          canSuccessfullyRunRemoteCommands(REMOTE_CODE_FIXTURE, REMOTE_CODE_RESULT_FIXTURE, 1112);
        
        } finally {
        
          server.stop();
          server.unregisterMBean();
        
        }
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
    {    //todo: start multiple servers from different threads
        NreplServer server = new NreplServer(1113); //start server listening onto port number
        assertEquals(1113, server.getPort());
        server.stop();
        server.unregisterMBean();
    }
}


