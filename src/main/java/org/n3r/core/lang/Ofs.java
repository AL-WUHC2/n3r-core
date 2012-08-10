package org.n3r.core.lang;

import java.util.*;

public class Ofs {
    public static <T1, T2> Tuple2<T1, T2> of(T1 o1, T2 o2) {
        return new Tuple2<T1, T2>(o1, o2);
    }

    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 o1, T2 o2, T3 o3) {
        return new Tuple3<T1, T2, T3>(o1, o2, o3);
    }

    public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> of(T1 o1, T2 o2, T3 o3, T4 o4) {
        return new Tuple4<T1, T2, T3, T4>(o1, o2, o3, o4);
    }

    public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> of(T1 o1, T2 o2, T3 o3, T4 o4, T5 o5) {
        return new Tuple5<T1, T2, T3, T4, T5>(o1, o2, o3, o4, o5);
    }

    public static <K, V> Map<K, V> of(Tuple2<K, V>... entries) {
        Map<K, V> map = new HashMap<K, V>();

        for (Tuple2<K, V> entry : entries) {
            map.put(entry._1, entry._2);
        }

        return map;
    }
}
