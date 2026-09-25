package net.matlux;

import clojure.lang.RT;
import clojure.lang.Keyword;
import clojure.lang.Symbol;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.junit.Assert.*;

/** Real socket, Spring and concurrent lifecycle checks, run by Failsafe. */
public class AppIT {
    private NreplServer server;

    @BeforeClass
    public static void loadClient() {
        RT.var("clojure.core", "require").invoke(Symbol.intern("cl-java-introspector.client-example"));
    }

    @After
    public void stopServer() {
        if (server != null) server.stop();
    }

    private Object evaluate(String code) {
        return RT.var("cl-java-introspector.client-example", "remote-execute")
                .invoke("127.0.0.1", server.getPort(), code);
    }

    private void assertEval(String expected, String code) {
        assertEquals(RT.readString(expected), evaluate(code));
    }

    @Test(timeout = 30000)
    public void evaluatesMultipleFormsAndRegisteredObjectsOverSocket() {
        server = new NreplServer(0, true, false, true, false);
        assertTrue(server.getPort() > 0);
        Map<?, ?> handle = (Map<?, ?>) RT.var("net.matlux.server.nrepl", "server").deref();
        ServerSocket listener = (ServerSocket) handle.get(Keyword.intern("server-socket"));
        assertTrue(listener.getInetAddress().isLoopbackAddress());
        server.put("value", "hello");
        assertEval("(2 42 \"hello\")", "(+ 1 1) (* 6 7) (.getObj net.matlux.NreplServer/instance \"value\")");
        assertTrue(server.stop());
        assertFalse(server.isStarted());
        assertEquals("cannot connect", evaluate("(+ 1 1)"));
    }

    @Test(timeout = 30000)
    public void clonedClientSessionCanEvaluateAndClose() {
        server = new NreplServer(0, true, false, true, false);
        Object result = RT.var("clojure.core", "load-string").invoke(
                "(require '[clojure.tools.nrepl :as repl]) " +
                "(with-open [conn (repl/connect :host \"127.0.0.1\" :port " + server.getPort() + ")] " +
                "  (let [client (repl/client conn 5000) " +
                "        session (:new-session (first (repl/message client {:op \"clone\"})))] " +
                "    (when-not session (throw (Exception. \"Missing cloned session\"))) " +
                "    (try (doall (repl/response-values " +
                "                 (repl/message client {:op \"eval\" :session session :code \"(+ 40 2)\"}))) " +
                "         (finally (doall (repl/message client {:op \"close\" :session session}))))))");
        assertEquals(RT.readString("(42)"), result);
    }

    @Test(timeout = 30000)
    public void restartAndRepeatedStopLeaveWorkingListener() {
        server = new NreplServer(0, false, false, true, false);
        assertFalse(server.isStarted());
        assertTrue(server.start());
        int boundPort = server.getPort();
        assertTrue(server.start());
        assertEquals(boundPort, server.getPort());
        assertEval("(42)", "(+ 40 2)");
        assertTrue(server.stop());
        assertTrue(server.stop());
        server.setPort(0);
        assertTrue(server.start());
        assertEval("(42)", "(+ 40 2)");
    }

    @Test(timeout = 30000)
    public void bindFailureClearsStoppedHandleAndRespectsPropagation() throws Exception {
        server = new NreplServer(0, true, false, false, false);
        try (ServerSocket occupied = new ServerSocket(0, 0, InetAddress.getByName("127.0.0.1"))) {
            server.setPort(occupied.getLocalPort());
            assertFalse(server.start());
            assertFalse(server.isStarted());
            server = new NreplServer(occupied.getLocalPort(), false, false, true, false);
            assertThrows(RuntimeException.class, () -> server.start());
            assertFalse(server.isStarted());
        }
        server.setPort(0);
        assertTrue(server.start());
        assertEval("(42)", "42");
    }

    @Test(timeout = 60000)
    public void concurrentStartStopPropagatesWorkerFailures() throws Exception {
        server = new NreplServer(0, false, false, true, false);
        ExecutorService executor = Executors.newFixedThreadPool(4);
        CountDownLatch ready = new CountDownLatch(4);
        CountDownLatch begin = new CountDownLatch(1);
        List<Future<?>> workers = new ArrayList<Future<?>>();
        try {
            for (int i = 0; i < 4; i++) {
                final boolean start = i % 2 == 0;
                workers.add(executor.submit(() -> {
                    ready.countDown();
                    if (!begin.await(10, TimeUnit.SECONDS)) throw new AssertionError("start barrier");
                    for (int j = 0; j < 10; j++) {
                        assertTrue(start ? server.start() : server.stop());
                    }
                    return null;
                }));
            }
            assertTrue(ready.await(10, TimeUnit.SECONDS));
            begin.countDown();
            for (Future<?> worker : workers) worker.get(30, TimeUnit.SECONDS);
        } finally {
            begin.countDown();
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
        }
        server.stop();
        assertFalse(server.isStarted());
        server.start();
        assertEval("(42)", "(+ 40 2)");
    }

    @Test(timeout = 30000)
    public void springXmlPublishesBeansAndStopsOnContextClose() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/spring/server-test.xml");
        try {
            server = context.getBean("repl", NreplServerSpring.class);
            assertSame(context.getBean("department"), server.getObj("department"));
            server.put("department", "shadowed");
            assertSame(context.getBean("department"), server.get("department"));
            assertTrue(server.containsKey("department"));
            server.put("extra", "registry value");
            assertEquals("registry value", server.get("extra"));
            assertEval("(nil \"The Art Department\")",
                    "(require '[cl-java-introspector.spring :as spring]) " +
                    "(.getName (spring/get-bean \"department\"))");
            // The fixture intentionally swaps city/street getters for the hot-fix demo.
            assertEval("(nil \"London\")",
                    "(.setAddress (.getObj net.matlux.NreplServer/instance \"employee1\") " +
                    "(proxy [net.matlux.testobjects.Address] [\"Street\" \"Zip\" \"City\"] " +
                    "(getCity [] \"London\"))) " +
                    "(-> (.getObj net.matlux.NreplServer/instance \"employee1\") .getAddress .getCity)");
        } finally {
            context.close();
        }
        assertFalse(server.isStarted());
    }
}
