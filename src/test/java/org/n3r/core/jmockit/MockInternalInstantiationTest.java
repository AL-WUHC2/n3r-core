package org.n3r.core.jmockit;

import static org.junit.Assert.*;
import mockit.*;

import org.junit.*;

public class MockInternalInstantiationTest {
    static class User {
        Address address() {
            return new Address();
        }
    }

    static class Address {
        String postCode() {
            return "sw1";
        }
    }

    @Mocked
    Address address;

    @Test
    public void mockInternalInstantiation() {
        new Expectations() {
            {
                new Address().postCode();
                returns("w14");
            }
        };

        String postCode = new User().address().postCode();

        assertEquals("w14", postCode);
    }
}
