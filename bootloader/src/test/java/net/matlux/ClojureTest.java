package net.matlux;

import clojure.lang.Keyword;
import clojure.lang.RT;
import clojure.lang.Symbol;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/** Makes Clojure assertion failures fail the Maven unit-test phase. */
public class ClojureTest {
    @Test
    public void clojureUnitTests() {
        RT.var("clojure.core", "require").invoke(Symbol.intern("cl-java-introspector.core-test"));
        Map<?, ?> result = (Map<?, ?>) RT.var("clojure.test", "run-tests")
                .invoke(Symbol.intern("cl-java-introspector.core-test"));
        assertTrue(((Number) result.get(Keyword.intern("test"))).longValue() > 0);
        assertEquals(result.toString(), 0L, ((Number) result.get(Keyword.intern("fail"))).longValue());
        assertEquals(result.toString(), 0L, ((Number) result.get(Keyword.intern("error"))).longValue());
    }
}
