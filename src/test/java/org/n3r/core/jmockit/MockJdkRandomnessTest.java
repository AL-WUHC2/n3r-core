package org.n3r.core.jmockit;

import static org.junit.Assert.*;

import java.security.*;

import mockit.*;

import org.junit.*;

public class MockJdkRandomnessTest {
    @Test
    public void mockSystemNanoTime() {
        new MockUp<System>() {
            @Mock
            long nanoTime() {
                return 0L;
            }
        };
        assertSame(0L, System.nanoTime());
    }

    @Test
    public void mockMathRandom() {
        new MockUp<Math>() {
            @Mock
            double random() {
                return 0D;
            }
        };
        assertEquals(0D, Math.random(), 0D);
    }

    @Mocked
    SecureRandom secureRandom;

    @Test
    public void mockSecureRandom() {

        new Expectations() {
            {
                new SecureRandom().nextInt();
                returns(0);
            }
        };
        assertSame(0, new SecureRandom().nextInt());
    }
}
