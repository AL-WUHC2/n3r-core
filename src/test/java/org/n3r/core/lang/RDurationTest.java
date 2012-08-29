package org.n3r.core.lang;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import static org.junit.Assert.*;

public class RDurationTest {

    @Test
    public void test() {
        assertNotNull(new RDuration());

        long duration = RDuration.parseDuration("10s", TimeUnit.SECONDS);
        assertEquals(10, duration);

        duration = RDuration.parseDuration("1m 10s", TimeUnit.SECONDS);
        assertEquals(70, duration);

        duration = RDuration.parseDuration("1h 1m 10s", TimeUnit.SECONDS);
        assertEquals(3670, duration);

        duration = RDuration.parseDuration("2h 1m 10s", TimeUnit.SECONDS);
        assertEquals(7270, duration);

        duration = RDuration.parseDuration("1d 1h 1m 10s", TimeUnit.SECONDS);
        assertEquals(90070, duration);

        duration = RDuration.parseDuration("1d 1m 10s", TimeUnit.SECONDS);
        assertEquals(86470, duration);

        duration = RDuration.parseDuration("1d 1m", TimeUnit.SECONDS);
        assertEquals(86460, duration);

        duration = RDuration.parseDuration("", TimeUnit.SECONDS);
        assertEquals(0, duration);
    }

}
