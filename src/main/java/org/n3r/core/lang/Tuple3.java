package org.n3r.core.lang;

public class Tuple3<T1, T2, T3> extends Tuple2<T1, T2> {
    public Tuple3(T1 o1, T2 o2, T3 o3) {
        super(o1, o2);
        _3 = o3;
    }

    public T3 _3 = null;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (_3 == null ? 0 : _3.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple3) {
            Tuple3<?, ?, ?> other = (Tuple3<?, ?, ?>) obj;
            return super.equals(obj) && _3.equals(other._3);
        }

        return false;
    }

    @Override
    public String toString() {
        return "Tuple3 [" + _1 + ',' + _2 + ',' + _3 + "]";
    }
}
