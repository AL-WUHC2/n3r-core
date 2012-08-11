package org.n3r.core.jmockit;

import static org.junit.Assert.*;
import mockit.*;

import org.junit.*;

public class MockNoInterfaceClassTest {
    static class User {
        String name() {
            return "joe";
        }
    }

    @Mocked
    User user;

    @Test
    public void mockNoInterfaceFinalClass() {

        new Expectations() {
            {
                user.name();
                returns("fred");
            }
        };

        assertEquals("fred", user.name());

    }
}
