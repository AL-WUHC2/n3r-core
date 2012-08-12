package org.n3r.core.lang;

import org.junit.Test;

import static org.junit.Assert.*;

public class RVersionTest {
    @Test
    public void testGetVersion() {
        assertNotNull(new RVersion());

        double version = RVersion.getVersion();
        assertEquals(1.6, version, 0.01);
    }
}
