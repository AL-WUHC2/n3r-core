package org.n3r.core.jmockit;

import static org.junit.Assert.*;
import mockit.*;

import org.junit.*;

public class MockPrivateMethodTest {
    static class User {

        private final String name = "joe";

        @SuppressWarnings("unused")
        private final String name() {
            return name;
        }

    }

    @Mocked
    User user;

    @Test
    public void mockPrivateMethod() {
        new Expectations() {
            {
                invoke(user, "name");
                returns("fred");
            }
        };
        assertEquals("fred", Deencapsulation.invoke(user, "name"));
    }
}
