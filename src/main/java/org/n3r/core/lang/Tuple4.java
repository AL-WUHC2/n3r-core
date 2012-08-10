package org.n3r.core.lang;

public class Tuple4<T1, T2, T3, T4> extends Tuple3<T1, T2, T3> {
    public Tuple4(T1 o1, T2 o2, T3 o3, T4 o4) {
        super(o1, o2, o3);
        _4 = o4;
    }

    public T4 _4 = null;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (_4 == null ? 0 : _4.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple3) {
            Tuple4<?, ?, ?, ?> other = (Tuple4<?, ?, ?, ?>) obj;
            return super.equals(obj) && _4.equals(other._4);
        }

        return false;
    }

    @Override
    public String toString() {
        return "Tuple4 [" + _1 + ',' + _2 + ',' + _3 + ',' + _4 + "]";
    }
}
