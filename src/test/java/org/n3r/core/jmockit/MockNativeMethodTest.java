package org.n3r.core.jmockit;

import static org.junit.Assert.*;
import mockit.*;

import org.junit.*;

public class MockNativeMethodTest {
    @Test
    public void mockNativeMethod() {
        new MockUp<Runtime>() {
            @Mock
            int availableProcessors() {
                return 999;
            }
        };

        int availableProcessors = Runtime.getRuntime().availableProcessors();

        assertEquals(999, availableProcessors);
    }
}
