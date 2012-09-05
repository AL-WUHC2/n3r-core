package org.n3r.esql.impl;

import org.apache.commons.lang3.mutable.MutableInt;

public class EsqlConnectionTrace {
    private static ThreadLocal<MutableInt> counter = new ThreadLocal<MutableInt>() {
        @Override
        protected MutableInt initialValue() {
            return new MutableInt(0);
        }
    };

    public static void incr() {
        counter.get().add(1);
    }

    public static void decr() {
        counter.get().add(-1);
    }

    public static int count() {
        return counter.get().intValue();
    }
}
