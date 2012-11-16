package org.n3r.core.collection;

import java.util.ArrayList;
import java.util.List;

public class RList {

    public static <T> List<T> add(List<T> list, T l2) {
        list = list == null ? new ArrayList<T>() : list;
        list.add(l2);
        return list;
    }
}
