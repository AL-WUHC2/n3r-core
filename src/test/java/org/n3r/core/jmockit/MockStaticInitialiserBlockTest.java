package org.n3r.core.jmockit;

import static org.junit.Assert.*;
import mockit.*;

import org.junit.*;

public class MockStaticInitialiserBlockTest {
    static class UserInitialiser {
        static {
            User.name("joe");
        }
    }

    static class User {

        private static String name;

        static void name(String name) {
            User.name = name;
        }

    }

    @Test
    public void mockStaticInitialiserBlock() throws ClassNotFoundException {
        new MockUp<UserInitialiser>() {
            @Mock
            public void $clinit() {
                User.name("fred");
            }
        };
        Class.forName(UserInitialiser.class.getName());
        assertEquals("fred", User.name);
    }
}
