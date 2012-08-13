package org.n3r.core.lang;

import org.junit.Test;

import static org.junit.Assert.*;

public class RVersionTest {
    @Test
    public void testGetVersion() {
        assertNotNull(new RVersion());

        assertEquals(1.6, RVersion.getVersion(), 0.01);
    }
}
