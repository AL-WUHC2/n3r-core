package org.n3r.core.jmockit;

import static org.junit.Assert.*;
import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;

public class MockDefaultConstructorTest {

    private static final User user =  User.getInstance();

    @Test
    public void mockDefaultConstructor() {
        new MockUp<User>() {
            @Mock
            public void $init() {
                user.name = "fred";
            }
        };
        
        User.getInstance();
        assertEquals("fred", user.name());
    }
}
