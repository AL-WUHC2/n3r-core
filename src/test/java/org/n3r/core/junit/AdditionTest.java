package org.n3r.core.junit;

import static org.junit.Assert.*;

import org.junit.*;

public class AdditionTest {
    private int x = 1;
    private int y = 1;

    @Test
    public void testAddition() {
        int z = x + y;
        assertEquals(2, z);
    }

    public static class Add {

        public int add(int a, int b) {
            return a + b;
        }

    }

}
