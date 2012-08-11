package org.n3r.core.junit;

import static org.junit.Assert.*;

import org.junit.*;

public class JUnit4Test {
    public static StringBuilder sb = new StringBuilder();

    @Before
    public void before() {
        sb.append("@Before");
    }

    @Test
    public void test() {
        sb.append("@Test");
        assertEquals(5 + 5, 10);
    }

    @Ignore
    @Test
    public void testIgnore() {
        sb.append("@Ignore");
    }

    @Test(timeout = 50)
    public void testTimeout() {
        sb.append("@Test(timeout = 50)");
        assertEquals(5 + 5, 10);
    }

    @Test(expected = ArithmeticException.class)
    public void testExpected() {
        sb.append("@Test(expected = Exception.class)");
        throw new ArithmeticException();
    }

    @After
    public void after() {
        sb.append("@After");
    }

    @BeforeClass
    public static void beforeClass() {
        sb.append("@BeforeClass");
    };

    @AfterClass
    public static void afterClass() {
        sb.append("@AfterClass");

        assertEquals("@BeforeClass"
                + "@Before"
                + "@Test"
                + "@After"
                + "@Before"
                + "@Test(timeout = 50)"
                + "@After"
                + "@Before"
                + "@Test(expected = Exception.class)"
                + "@After"
                + "@AfterClass",
                sb.toString());
    }
}
