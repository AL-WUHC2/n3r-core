package org.n3r.core.lang;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class ForEachTest {
    int foreachCalled1 = 0;

    @Test
    public void testForEach10() {
        foreachCalled1 = 0;
        for (@SuppressWarnings("unused") String str : mockList());
        assertEquals(1, foreachCalled1);
    }

    int createListCalledTimes = 0;
    int getListCalledTimes = 0;
    @Test
    public void testForEach11() {
        for (@SuppressWarnings("unused") String str : createList().getList());
        assertEquals(1, createListCalledTimes);
        assertEquals(1, getListCalledTimes);
    }

    class MyListContainer {
        public List<String> getList() {
            ++getListCalledTimes;
            return Arrays.asList("a", "b", "c");
        }
    };

    private MyListContainer createList() {
        ++createListCalledTimes;
        return new MyListContainer();
    }

    @Test
    public void testForEach2() {
        int size = 10;
        MyContainer mycontainer = new MyContainer(size);
        for (int i = 0; i < mycontainer.size(); ++i);

        assertEquals(size + 1, mycontainer.getCalledTimes());

    }

    MyContainer myContainer = null;
    int myContainerCalledTimes = 0;

    @Test
    public void testForEach3() {
        int size = 10;
        for (int i = 0; i < createContainter(size).size(); ++i);

        assertEquals(size + 1, myContainerCalledTimes);
        assertEquals(1, myContainer.getCalledTimes());
    }

    private MyContainer createContainter(int size) {
        ++myContainerCalledTimes;
        myContainer = new MyContainer(size);
        return myContainer;
    }

    private List<String> mockList() {
        ++foreachCalled1;
        return Arrays.asList("a", "b", "c");
    }

    class MyContainer {
        int calledTimes = 0;
        int times;

        public MyContainer(int times) {
            this.times = times;
        }

        public int size() {
            ++calledTimes;
            return times;
        }

        public int get(int i) {
            return i;
        }

        public int getCalledTimes() {
            return calledTimes;
        }
    }
}
