package org.n3r.core.jmockit;

import static org.junit.Assert.*;
import mockit.*;

import org.junit.*;

public class MockStaticMethodTest {
    static class User {
        static String name() {
            return "joe";
        }
    }

    @Mocked
    private final User user = null;

    @Test
    public void mockStaticMethod() {

        new Expectations() {
            {
                User.name();
                returns("fred");
            }
        };

        assertEquals("fred", User.name());

    }
}
