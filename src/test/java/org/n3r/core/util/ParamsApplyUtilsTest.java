package org.n3r.core.util;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ParamsApplyUtilsTest {
    @Test
    public void test1() throws ClassNotFoundException {
        Class.forName("org.n3r.core.util.ParamsApplyUtilsTest$A");
        String str = "org.n3r.core.util.ParamsApplyUtilsTest$A\r\norg.n3r.core.util.ParamsApplyUtilsTest$B(abc),org.n3r.core.util.ParamsApplyUtilsTest$B(c,d)";
        List<IAB> objects = ParamsApplyUtils.createObjects(str, IAB.class);
        for (IAB iab : objects)
            iab.doSomething();

    }

    public static interface IAB {

        void doSomething();

    }

    public static class A implements IAB {

        @Override
        public void doSomething() {
            System.out.println("A...");
        }

    }

    public static class B implements ParamsAppliable, IAB {
        @Override
        public void doSomething() {
            System.out.println("B...");
        }

        @Override
        public void applyParams(String[] params) {
            System.out.println(Arrays.toString(params));
        }

    }
}
