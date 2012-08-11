package org.n3r.core.jmockit;

import static org.junit.Assert.*;
import mockit.*;

import org.junit.*;

public class MockInvokesRealMethodTest {
    static class User {
        String name() {
            return "joe";
        }
    }

    @MockClass(realClass = User.class)
    static class MockUser {
        User it;

        @Mock(reentrant = true)
        String name() {
            return it.name();
        }
    }

    @BeforeClass
    public static void beforeClass() {
        Mockit.setUpMocks(MockUser.class);
    }

    @AfterClass
    public static void afterClass() {
        Mockit.tearDownMocks();
    }

    @Test
    public void mockInvokesRealMethod() {
        assertEquals("joe", new User().name());
    }
}
