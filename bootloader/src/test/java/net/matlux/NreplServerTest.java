package net.matlux;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class NreplServerTest {
    @Test
    public void registrySupportsLookupNullValuesReplacementAndRemoval() {
        NreplServer server = new NreplServer(0, false, false, true, false);
        assertTrue(server.isEmpty());
        assertSame(server, NreplServer.instance);
        assertNull(server.put("name", "first"));
        assertEquals("first", server.put("name", "second"));
        server.put("nullable", null);
        assertTrue(server.containsKey("nullable"));
        assertTrue(server.containsValue(null));
        assertEquals("second", server.getObj("name"));
        assertEquals(2, server.size());
        assertTrue(server.keySet().contains("name"));
        assertTrue(server.values().contains("second"));
        assertEquals(2, server.entrySet().size());
        assertEquals("second", server.remove("name"));
        server.clear();
        assertTrue(server.isEmpty());
    }

    @Test
    public void suppliedMapsMergeIntoRegistry() {
        NreplServer server = new NreplServer(0, false, false, true, false);
        server.put("keep", 1);
        Map<String, Object> additions = new HashMap<String, Object>();
        additions.put("new", 2);
        server.setObjMap(additions);
        additions.put("other", 3);
        assertFalse(server.containsKey("other"));
        server.putAll(additions);
        assertEquals(3, server.size());
        assertEquals(1, server.get("keep"));
    }

    @Test
    public void springRegistryWorksBeforeContextInjection() {
        NreplServerSpring server = new NreplServerSpring(0, false, false, true, false);
        assertTrue(server.isEmpty());
        assertEquals(0, server.size());
        server.put("object", "value");
        assertEquals("value", server.getObj("object"));
        assertTrue(server.containsKey("object"));
        assertEquals(1, server.size());
        assertFalse(server.isEmpty());
    }
}
