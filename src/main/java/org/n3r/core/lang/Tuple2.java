package org.n3r.core.lang;

public class Tuple2<T1, T2> extends Tuple1<T1> {
    public Tuple2(T1 o1, T2 o2) {
        super(o1);
        _2 = o2;
    }

    public T2 _2 = null;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (_2 == null ? 0 : _2.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tuple2) {
            Tuple2<?, ?> other = (Tuple2<?, ?>) obj;
            return super.equals(obj) && _2.equals(other._2);
        }

        return false;
    }

    @Override
    public String toString() {
        return "Tuple2 [" + _1 + ',' + _2 + "]";
    }

}
