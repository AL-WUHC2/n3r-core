package org.n3r.core.lang;

public class Tuple5<T1, T2, T3, T4, T5> extends Tuple4<T1, T2, T3, T4> {
    public T5 _5 = null;

    public Tuple5(T1 o1, T2 o2, T3 o3, T4 o4, T5 o5) {
        super(o1, o2, o3, o4);
        _5 = o5;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (_5 == null ? 0 : _5.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple3) {
            Tuple5<?, ?, ?, ?, ?> other = (Tuple5<?, ?, ?, ?, ?>) obj;
            return super.equals(obj) && _5.equals(other._5);
        }

        return false;
    }

    @Override
    public String toString() {
        return "Tuple5 [" + _1 + ',' + _2 + ',' + _3 + ',' + _4 + ',' + _5 + "]";
    }
}