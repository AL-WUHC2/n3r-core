package org.n3r.core.jmockit;

import static org.junit.Assert.*;
import mockit.Expectations;
import mockit.Mocked;

import org.junit.Test;

public class MockFinalClassTest {
    static final class User {
        final String name() { return "joe"; }
    }

    @Mocked User user;

    @Test
    public void mockFinalClass() {

        new Expectations() {{
            user.name();
            returns("fred");
        }};

        assertEquals("fred", user.name());
    }

    @Test
    public void methodTest() {
        // 1. Preparation: whatever is required before the unit under test can be exercised.
        // ...
        // 2. The unit under test is exercised, normally by calling a non-private method
        //    or constructor.
        // ...
        // 3. Verification: whatever needs to be checked to make sure the exercised unit
        //    did its job.
        // ...
    }

}
